package kutlwano.oumazi.openpsychic
import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kutlwano.oumazi.openpsychic.activity.HomeActivity
import kutlwano.oumazi.openpsychic.activity.MainActivity
import kutlwano.oumazi.openpsychic.services.UserPreferences
import org.json.JSONObject
import java.util.*

// real ad unit id = ca-app-pub-2450865968732279/1583553486
// test ad unit id = ca-app-pub-3940256099942544/3419835294
private const val AD_UNIT_ID = "ca-app-pub-2450865968732279/1583553486"
private const val LOG_TAG = "OpenPsychicApp"

/** Application class that initializes, loads and show ads when activities change states. */
class OpenPsychicApp : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver,
    PurchasesUpdatedListener {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var activesubscription = false
    private lateinit var billingClient: BillingClient

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        setupBillingClient()
        registerActivityLifecycleCallbacks(this)
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
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
                Log.i("firebase", "AuthState changed to " + firebaseAuth.currentUser!!.uid)
                setupBillingClient()
                saveusertosharedprefs()
                setUserOnlineStatus(true)
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        setUserOnlineStatus(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        setUserOnlineStatus(false)
    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
        setUserOnlineStatus(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        setUserOnlineStatus(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        // App is destroyed
        setUserOnlineStatus(false)
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

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
    }

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

    private fun setUserOnlineStatus(isOnline: Boolean) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val userRef = db.collection("users").document(firebaseUser.uid)
            userRef.update("isOnline", isOnline)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "User status updated to: $isOnline")
                    updateUserOnlineStatus(firebaseUser.uid, isOnline)
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error updating user status", e)
                }

            db.collection("users").document(firebaseUser.uid).update("lastOnline", Timestamp.now())
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "User last online updated to: ${Timestamp.now()}")
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error updating user status", e)
                }
        }
    }

    private fun updateUserOnlineStatus(uid: String, online: Boolean) {
        val onlineUsersRef = db.collection("onlineUsers").document(uid)

        if (online) {
            // Add the UID as a document in the onlineUsers collection
            onlineUsersRef.set(emptyMap<String, Any>())
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully added user $uid to online users")
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error adding user $uid to online users", e)
                }
        } else {
            // Remove the UID document from the onlineUsers collection
            onlineUsersRef.delete()
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully removed user $uid from online users")
                }
                .addOnFailureListener { e ->
                    Log.w(LOG_TAG, "Error removing user $uid from online users", e)
                }
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    checkSubscriptionStatus()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle the error
            }
        })
    }

   private fun checkSubscriptionStatus() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            } else {
                // Handle the error
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>?) {
        val sharedPreferences = UserPreferences(this)
        var isPremium = false
        val currentTime = System.currentTimeMillis()

        purchases?.forEach { purchase ->
            val purchaseData = JSONObject(purchase.originalJson)
            val expiryTime = purchaseData.optLong("expiryTimeMillis", 0)
            val orderId = purchaseData.optString("orderId", "")
            Log.e("Purchase", purchase.toString())

            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && expiryTime > currentTime) {
                if (!purchase.isAutoRenewing && sharedPreferences.subscriptionstate == "active") {
                    // Check if subscription is manually canceled but still active
                    sharedPreferences.saveSubscription("expired")
                    updateFirestoreSubscriptionStatus("expired", orderId, expiryTime)
                } else {
                    isPremium = true
                    // Update Firestore with active subscription order ID
//                    updateFirestoreSubscriptionStatus(true, orderId)
                }
            }
        }

    }

    private fun updateFirestoreSubscriptionStatus(state: String, orderId: String, enddate: Long) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid   // Replace with actual user ID logic

        val subscriptionState = hashMapOf(
            "autorenewing" to "false",
            "enddate" to enddate,
            "isPremium" to false,
            "orderId" to orderId,
            "state" to "expired"
        )

        val userDocRef = userId?.let { firestore.collection("users").document(it).collection("subscriptions").document(orderId)}
        val subscriptionDocRef = firestore.collection("subscriptions").document(userId.toString()).collection("subscriptions").document(orderId)
        val profilePremium = userId?.let { firestore.collection("users").document(it)}

        firestore.runBatch { batch ->
            if (userDocRef != null) {
                batch.update(userDocRef, subscriptionState as Map<String, Any>)
            }
            if (profilePremium != null) {
                batch.update(profilePremium,"isPremium","false")
            }
            batch.update(subscriptionDocRef, subscriptionState as Map<String, Any>)

        }.addOnSuccessListener {
            // Successfully updated
        }.addOnFailureListener {
            // Handle the error
        }
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

            var state = UserPreferences(applicationContext).subscriptionstate
            Log.e("State", state.toString())

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
            if(state != "active") {
                isShowingAd = true
                appOpenAd!!.show(activity)
            }
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

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        return
    }

}