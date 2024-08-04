package io.getmadd.openpsychic.fragments.home

import ExplorePsychicsAdapter
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsBinding
import io.getmadd.openpsychic.model.MessageMetaData
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.services.UserPreferences


class ExplorePsychics : Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsBinding
    private val binding get() = _binding
    val db = Firebase.firestore
    val listofPsychicIds = mutableListOf<String>()
    lateinit var category_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category_type = (arguments?.getString("category_type").toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorePsychicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        if (category_type != "null") {
            binding.explorePsychicsCategoryTextView.text = category_type
            db.collection("psychicOnDisplay").document(category_type)
                .collection("psychicsOnDisplay")
                .get()
                .addOnSuccessListener { result ->

                    if (result.isEmpty) {
                        binding.emptyExploreTextView.visibility = View.VISIBLE
                    }

                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        listofPsychicIds.add(document.id)
                    }
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        } else {
            binding.explorePsychicsCategoryTextView.text = "All Psychic Advisors"
//            db.collection("users")
//                .get()
//                .addOnSuccessListener { result ->
//                    if (result.isEmpty) {
//                        Toast.makeText(context, "Loading All Hot New Psychics", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    for (document in result) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
//                        val userType = document.getString("usertype")
//                        val displayimg = document.getString("displayimgsrc")
//
//                        if (userType == "psychic") {
//                            val psychic = document.toObject(Psychic::class.java)
//                            listofPsychics.add(psychic)
//                        }
//                    }
//                    listofPsychics.shuffle()
//                    binding.recyclerView.adapter?.notifyDataSetChanged()
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents.", exception)
//                }
        }
        binding.recyclerView.adapter = ExplorePsychicsAdapter(items = listofPsychicIds, {})

//        val prefs = UserPreferences(requireContext())
//        val subscriptionstate = prefs.subscriptionstate

//        val adView: AdView = binding.explorebannerad
//        val adRequest: AdRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//
//        val adRequest1 = AdRequest.Builder().build()
//        var state = context?.let { UserPreferences(it).subscriptionstate }
//        if (state != "active") {
//            InterstitialAd.load(
//                context!!,
//                "ca-app-pub-2450865968732279/9837064209",
//                adRequest1,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.d(TAG, adError.message)
//                    }
//
//                    override fun onAdLoaded(ad: InterstitialAd) {
//                        Log.d(TAG, "Ad was loaded.")
//                        activity?.let { ad.show(it) }
//                    }
//                }
//            )
//        }
    }
}