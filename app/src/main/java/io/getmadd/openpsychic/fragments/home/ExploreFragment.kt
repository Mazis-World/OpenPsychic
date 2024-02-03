package io.getmadd.openpsychic.fragments.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExploreBinding
import io.getmadd.openpsychic.services.UserPreferences


class ExploreFragment : Fragment() {

    private lateinit var _binding: FragmentExploreBinding

    private val binding get() = _binding
    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()

        var category_type = "category_type"
        var uid = Firebase.auth.uid!!
        var prefs = UserPreferences(requireContext())
        var userType = prefs.usertype!!
        var subscriptionstate = prefs.subscriptionstate
        Log.e("ExploreFragment", subscriptionstate!!)


        when (subscriptionstate) {
            "active" -> {
                binding.include.subscribecardview.visibility = View.GONE
            }
            else -> {
                binding.include.subscribecardview.visibility = View.VISIBLE
                val adRequest1 = AdRequest.Builder().build()

                InterstitialAd.load(
                    context!!,
                    "ca-app-pub-2450865968732279~5202685556",
                    adRequest1,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(ContentValues.TAG, adError.message)
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d(ContentValues.TAG, "Ad was loaded.")
                            activity?.let { ad.show(it) }
                        }
                    }
                )
            }
        }

        setupUI(bundle)
    }

    fun setupUI(bundle: Bundle){
        binding.include.exploreseemorebutton.setOnClickListener{
            findNavController().navigate(R.id.action_explore_fragment_to_subscribe_premium_fragment)
        }
        binding.homeCategory0.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_spirit))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory1.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_tarot_card))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory2.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_turban))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory3.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_magic_ball))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory4.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_palm))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory5.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_saturn))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory6.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_dream))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory7.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_ghost))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory8.setOnClickListener {
            bundle.putString("category_type", getString(R.string.category_heart))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }

        binding.homeCardBackgroundImageView.setOnClickListener{
            findNavController().navigate(R.id.action_explore_fragment_to_explore_article_fragment)
        }
    }
}
