package io.getmadd.openpsychic.fragments.home

import HistoryFragmentAdapter
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
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentHistoryBinding
import io.getmadd.openpsychic.model.UserHistoryObject


class HistoryFragment : Fragment() {
    private lateinit var _binding: FragmentHistoryBinding
    private val binding get() = _binding
    var db = Firebase.firestore

    var userHistoryList = ArrayList<UserHistoryObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userId = Firebase.auth.uid
        
        var colRef = db.collection("users").document("$userId").collection("messageThreads")


        colRef.get()
            .addOnSuccessListener { result ->

                if(result.isEmpty){
                    binding.emptyHistoryTextView.visibility = View.VISIBLE
                }
                else{
                    binding.emptyHistoryTextView.visibility = View.GONE
                    binding.historyRecyclerView.adapter!!.notifyDataSetChanged()
                }

                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                }
            }
            .addOnFailureListener { exception ->
            }

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = HistoryFragmentAdapter(
            userHistoryList
        ) {}

        val adView: AdView = binding.historybannerad
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val adRequest1 = AdRequest.Builder().build()

        InterstitialAd.load(
            context!!,
            "ca-app-pub-2450865968732279/8716388373",
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