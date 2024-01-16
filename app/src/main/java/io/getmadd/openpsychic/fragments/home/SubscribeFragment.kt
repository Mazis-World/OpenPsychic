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
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsBinding
import io.getmadd.openpsychic.databinding.FragmentSubscribePremiumBinding
import io.getmadd.openpsychic.model.Psychic


class SubscribeFragment : Fragment(), PurchasesUpdatedListener {

    private lateinit var _binding: FragmentSubscribePremiumBinding
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
        // Handle the purchase here, you can update UI or grant the purchased content
    }

    // To initiate the purchase
    private fun initiatePurchase() {
        val skuList = listOf("new_user_subscription")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()

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
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.subscribepremiumbutton.setOnClickListener{
            initiatePurchase()
        }
    }
}