package io.getmadd.openpsychic
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.activity.HomeActivity
import io.getmadd.openpsychic.activity.MainActivity
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.User
import io.getmadd.openpsychic.services.UserPreferences
import java.util.*

// real ad unit id = ca-app-pub-2450865968732279/1583553486
// test ad unit id = ca-app-pub-3940256099942544/3419835294
private const val AD_UNIT_ID = "ca-app-pub-2450865968732279/1583553486"
private const val LOG_TAG = "OpenPsychicApp"

/** Application class that initializes, loads and show ads when activities change states. */
class OpenPsychicApp : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var activesubscription = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                if (javaClass != MainActivity::class.java) {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                saveusertosharedprefs()
                Log.i("firebase", "AuthState changed to " + firebaseAuth.currentUser!!.uid)
                if (javaClass != HomeActivity::class.java) {
                    var intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
        // Log the Mobile Ads SDK version.
        Log.d(LOG_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())


        MobileAds.initialize(this) {}
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()

    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
//        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Shows an app open ad.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
     * dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable() || activesubscription) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                AD_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        Log.d(LOG_TAG, "onAdLoaded.")
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                    }
                }
            )
        }

        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                }
            )
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            Log.d(LOG_TAG, "Will show ad.")

            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                /** Called when full screen content is dismissed. */
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                /** Called when fullscreen content failed to show. */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                /** Called when fullscreen content is shown. */
                override fun onAdShowedFullScreenContent() {
                    Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                }
            }
            isShowingAd = true
            appOpenAd!!.show(activity)
        }
    }

    fun saveusertosharedprefs(){
        var userid = auth.uid
        var sharedpref = UserPreferences(this)

        db.collection("users").document(userid!!)
            .get()
            .addOnSuccessListener { result ->
                sharedpref.saveUser(
                        userid = result.getString("userid").toString(),
                        email = result.getString("email").toString(),
                        displayname = result.getString("displayname").toString(),
                        username = result.getString("username").toString(),
                        usertype = result.getString("usertype").toString(),
                        firstname = result.getString("firstname").toString(),
                        lastname = result.getString("lastname").toString(),
                        bio = result.getString("bio").toString(),
                        profileimgsrc = result.getString("profileimgsrc").toString(),
                        displayimgsrc = result.getString("displayimgsrc").toString(),
                )
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }


        db.collection("users").document(userid).collection("subscriptions").whereEqualTo("state","active")
            .get()
            .addOnSuccessListener { result ->
                Log.e("Subscription", result.toString())
                if(!result.isEmpty){
                    result.documents[0].getString("state")?.let { Log.e("Subscription", it) }
                    sharedpref.saveSubscription("active")
                    activesubscription = true
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

}