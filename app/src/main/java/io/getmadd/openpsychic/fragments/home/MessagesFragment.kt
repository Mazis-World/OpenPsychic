package io.getmadd.openpsychic.fragments.home

import MessagesAdapter
import RequestAdapter
import io.getmadd.openpsychic.model.Request
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentHistoryBinding
import io.getmadd.openpsychic.databinding.FragmentMessagesBinding
import io.getmadd.openpsychic.model.Message
import io.getmadd.openpsychic.model.MessageMetaData
import io.getmadd.openpsychic.services.UserPreferences


class MessagesFragment : Fragment() {
    private lateinit var _binding: FragmentMessagesBinding
    private val binding get() = _binding
    var db = Firebase.firestore
    var messageslist = ArrayList<MessageMetaData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = Firebase.auth.uid.toString()

        val messagesref = db.collection("users")
            .document(userId)
            .collection("messages")

        binding.messagesRecyclerView.adapter = MessagesAdapter(messageslist) {}
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(context)
        var rcv = binding.messagesRecyclerView.adapter


        messagesref.get()
            .addOnSuccessListener { userDocumentSnapshot ->
                if (!userDocumentSnapshot.isEmpty) {
                    // User document exists, check for the 'request' collection
                    val messagesCollection = messagesref

                    messagesCollection.get()
                        .addOnSuccessListener { messageQuerySnapshot ->
                            // Clear the list before adding new items
                            messageslist.clear()

                            for (requestDocument in messageQuerySnapshot.documents) {
                                // Process each document in the 'request' collection
                                // For example, add data to userHistoryList

                                val message = requestDocument.toObject(MessageMetaData::class.java)

                                if (message != null) {
                                    messageslist.add(message)
                                }
                            }
                            rcv?.notifyDataSetChanged()

                            if (messageQuerySnapshot.isEmpty) {
                                // 'request' collection is empty
                                binding.emptyMessagesTextView.visibility = View.VISIBLE
                            } else {
                                binding.emptyMessagesTextView.visibility = View.GONE
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors while fetching 'request' collection
                            Log.e(TAG, "Error getting 'request' collection: ${exception.message}", exception)
                        }

                } else {
                    // User document does not exist
                    Log.d(TAG, "User document does not exist for user ID: $userId")
                    binding.emptyMessagesTextView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors while fetching user document
                Log.e(TAG, "Error getting user document: ${exception.message}", exception)
            }

        var prefs = UserPreferences(requireContext())
        var subscriptionstate = prefs.subscriptionstate

        val adView: AdView = binding.messagesbannerad
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val adRequest1 = AdRequest.Builder().build()

        InterstitialAd.load(
            context!!,
            "ca-app-pub-2450865968732279/9483822289",
            adRequest1,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    activity?.let { ad.show(it) }
                }
            }
        )
    }

}