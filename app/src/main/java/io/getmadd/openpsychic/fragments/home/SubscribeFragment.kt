package io.getmadd.openpsychic.fragments.home

import ExplorePsychicsAdapter
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentSubscribePremiumBinding


class SubscribeFragment : Fragment(), PurchasesUpdatedListener {

    private lateinit var _binding: FragmentSubscribePremiumBinding
    private lateinit var customLayout: View
    private val binding get() = _binding
    val db = Firebase.firestore
    private lateinit var billingClient: BillingClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(this)
            .enablePendingPurchases()
            .build()

        // Start the connection to Google Play Billing Service
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready. You can query purchases or make purchases here.
                    Log.e("BillingClient",billingResult.responseCode.toString() )
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle the event when the billing service is disconnected
            }
        })
    }

    // Implement PurchasesUpdatedListener
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle the user canceling the purchase
        } else {
            // Handle other errors
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val db = Firebase.firestore
        val uid = Firebase.auth.uid
        val subsref = db.collection("subscriptions").document("$uid")
        val userref = db.collection("users").document("$uid")
        val thirtyonedays = 1646467200

    // Handle the purchase here, you can update UI or grant the purchased content
        val subscriptionData = hashMapOf(
            "orderid" to purchase.orderId,
            "purchasetoken" to purchase.purchaseToken,
            "autorenewing" to purchase.isAutoRenewing,
            "stardate" to purchase.purchaseTime,
            "enddate" to purchase.purchaseTime + thirtyonedays, //logic for purchase time + 30 days
            "state" to "active" // active or inactive
        )

        subsref.collection("subscriptions").document(purchase.orderId!!).set(subscriptionData)
        userref.collection("subscriptions").document(purchase.orderId!!).set(subscriptionData)
        userref.update("isPremium", true)
        binding.root.addView(customLayout)
    }

    // To initiate the purchase
    private fun initiatePurchase() {
        val skuList = listOf("openpsychicpremium")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                Log.e("Billing Client", skuDetailsList.toString())
                for (skuDetails in skuDetailsList) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        Log.e("BillingClient",flowParams.toString() )
                        billingClient.launchBillingFlow(requireActivity(), flowParams)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient.endConnection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribePremiumBinding.inflate(inflater, container, false)
        customLayout = inflater.inflate(R.layout.view_welcome_subscriber, container, false)
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.subscribepremiumbutton.setOnClickListener{
            Log.e("BillingClient","Subscribe Premium" )
            initiatePurchase()
        }
    }
}