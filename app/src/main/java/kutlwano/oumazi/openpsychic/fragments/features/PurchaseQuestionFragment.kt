package kutlwano.oumazi.openpsychic.fragments.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentPurchaseQuestionPackBinding
import kutlwano.oumazi.openpsychic.services.UserPreferences

class PurchaseQuestionFragment : Fragment() {

    private lateinit var binding: FragmentPurchaseQuestionPackBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var prefs: UserPreferences
    private lateinit var billingClient: BillingClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseQuestionPackBinding.inflate(inflater, container, false)
        prefs = UserPreferences(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pack50cardview.setOnClickListener {
            launchGooglePlayBilling("psychic_advisor_question_answer_pack_50")
        }

        binding.pack12cardview.setOnClickListener {
            launchGooglePlayBilling("psychic_advisor_question_answer_pack_12")
        }

        binding.pack3cardview.setOnClickListener {
            launchGooglePlayBilling("psychic_advisor_question_answer_pack_3")
        }

        binding.closedialogimageview.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.discoverpremumbuton.setOnClickListener {
            findNavController().navigate(R.id.subscribe_premium_fragment)
        }

        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                // Handle purchase updates
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        // Process the purchase
                        handlePurchase(purchase)
                    }
                }
            }
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready. Query purchases, etc.
                    querySkuDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun querySkuDetails() {
        val skuList = listOf("psychic_advisor_question_answer_pack_3", "psychic_advisor_question_answer_pack_12", "psychic_advisor_question_answer_pack_50")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                // Process the result
                for (skuDetails in skuDetailsList) {
                    // Display or use the SKU details
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Process the purchase
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val productId = purchase.products[0]
            var amount = 0
            when (productId) {
                "psychic_advisor_question_answer_pack_3" -> amount = 3
                "psychic_advisor_question_answer_pack_12" -> amount = 12
                "psychic_advisor_question_answer_pack_50" -> amount = 50
            }

            if (amount > 0) {
                val userDocRef = firestore.collection("users").document(auth.currentUser?.uid!!)
                userDocRef.get()
                    .addOnSuccessListener { document ->
                        val currentQuestionsAvailable = document.getLong("questionsAvailable") ?: 0
                        val newQuestionsAvailable = currentQuestionsAvailable + amount
                        userDocRef.update("questionsAvailable", newQuestionsAvailable)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Purchase successful.", Toast.LENGTH_SHORT).show()
                                userDocRef.collection("purchases").document().set(purchase)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to save purchase: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to get user data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun launchGooglePlayBilling(productId: String) {
        val skuList = ArrayList<String>()
        skuList.add(productId)

        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
            if (responseCode.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == productId) {
                        // Use this skuDetails object to launch the billing flow
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()

                        val responseCode = billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
                        if (responseCode.responseCode != BillingClient.BillingResponseCode.OK) {
                            // Handle the error
                        }
                        break
                    }
                }
            } else {
                // Handle the query error
            }
        }
    }

}
