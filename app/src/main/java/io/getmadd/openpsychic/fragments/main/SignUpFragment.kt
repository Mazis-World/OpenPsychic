package io.getmadd.openpsychic.fragments.main

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentSignupBinding


class SignUpFragment : Fragment(), PurchasesUpdatedListener {

    private var _binding: FragmentSignupBinding? = null
    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val binding get() = _binding!!
    private lateinit var username: String
    private lateinit var userpass: String
    private lateinit var useremail: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmailET = binding.fragmentSignupEmailEditText
        val userPassET = binding.fragmentSignupPasswordEditText
        val userNameET = binding.fragmentSignupUsernameEditText

        val signupBtn = binding.fragmentRegisterPsychicSignupButton

        binding.signupbackimageview.setOnClickListener{
            findNavController().popBackStack()
        }

        signupBtn.setOnClickListener {

            if(userEmailET.text.toString().isEmpty() || userPassET.text.toString().isEmpty() || userNameET.text.toString().isEmpty()){
                Toast.makeText(context,"Complete Sign Up Form", Toast.LENGTH_LONG).show()
            }
            else {
                useremail = userEmailET.text.toString()
                userpass = userPassET.text.toString()
                username = userNameET.text.toString()

                registerUser(username,useremail,userpass)

//                val billingClient = BillingClient.newBuilder(requireContext())
//                    .setListener(this)
//                    .enablePendingPurchases()
//                    .build()
//
//                billingClient.startConnection(object : BillingClientStateListener {
//                    override fun onBillingSetupFinished(billingResult: BillingResult) {
//                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            // Billing client is ready
//                            val skuDetailsParams = SkuDetailsParams.newBuilder()
//                                .setSkusList(listOf("openpsychicmembership"))
//                                .setType(BillingClient.SkuType.SUBS)
//                                .build()
//
//                            billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
//                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
//                                    val skuDetails =
//                                        skuDetailsList.find { it.sku == "openpsychicmembership" }
//                                    skuDetails?.let {
//                                        val flowParams = BillingFlowParams.newBuilder()
//                                            .setSkuDetails(it)
//                                            .build()
//                                        billingClient.launchBillingFlow(
//                                            requireActivity(),
//                                            flowParams
//                                        )
//                                    }
//                                } else {
//                                    // Handle error
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onBillingServiceDisconnected() {
//                        // Try to restart the connection on the next request to
//                        // Google Play by calling the startConnection() method.
//                    }
//                })
//
            }
        }

//        val adView: AdView = binding.signupadview
//        val adRequest: AdRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                registerUser(username,useremail,userpass)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle the user canceling the purchase
        } else {
            // Handle other errors
        }
    }

    fun registerUser(username:String, useremail:String, userpass:String){
        auth.createUserWithEmailAndPassword(useremail, username)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userID = auth.currentUser?.uid

                    val newuser = hashMapOf(
                        "firstname" to " ",
                        "lastname" to " ",
                        "displayname" to " ",
                        "usertype" to "user",
                        "bio" to " ",
                        "profileimgsrc" to " ",
                        "displayimgsrc" to " ",
                        "username" to username,
                        "email" to useremail,
                        "userid" to auth.currentUser!!.uid,
                        "questionsAvailable" to 3
                    )

                    db.collection("users").document("$userID")
                        .set(newuser)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: $userID")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // user create failed for some reason
                }
            }
    }
}