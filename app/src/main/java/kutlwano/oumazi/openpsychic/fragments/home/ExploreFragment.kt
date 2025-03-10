package kutlwano.oumazi.openpsychic.fragments.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentExploreBinding
import kutlwano.oumazi.openpsychic.services.UserPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExploreFragment : Fragment() {

    private lateinit var _binding: FragmentExploreBinding
    private val binding get() = _binding
    private var db = Firebase.firestore
    private var listenerRegistration: ListenerRegistration? = null
    private val listOfPsychics = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()

        val uid = Firebase.auth.uid!!
        val prefs = UserPreferences(requireContext())
        val subscriptionState = prefs.subscriptionstate
        val adRequest1 = AdRequest.Builder().build()

//        if (subscriptionState != "active") {
//            InterstitialAd.load(
//                requireContext(),
//                "ca-app-pub-2450865968732279~5202685556",
//                adRequest1,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.d(ContentValues.TAG, adError.message)
//                    }
//
//                    override fun onAdLoaded(ad: InterstitialAd) {
//                        Log.d(ContentValues.TAG, "Ad was loaded.")
//                        activity?.let { ad.show(it) }
//                    }
//                }
//            )
//        }
        setupUI(bundle)
    }

    private fun setupUI(bundle: Bundle) {
//        binding.hotNewPsychicsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        binding.hotNewPsychicsRecyclerView.adapter = ExplorePsychicsAdapter(items = listOfPsychics, {})


        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val visFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val today = dateFormat.format(Date())
        binding.dailyReadingDate.text = visFormat.format(Date())

        db.collection("daily_readings").document(today.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val alignment = document.getString("current_alignment")
                    val card = document.getString("current_card")
                    val message = document.getString("current_message")
                    val date = document.getString("date")

                    // Update your UI with the daily reading message
                    binding.dailyReading.text = message

                } else {
                    // Handle the case where there is no reading for today
                    binding.dailyReading.text = "No reading available for today."
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                binding.dailyReading.text = "Error retrieving daily reading."
            }

        listenerRegistration = db.collection("explore")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    processSnapshotChanges(snapshots)
                }
            }

        setupCategoryButtons(bundle)
    }

    private fun processSnapshotChanges(snapshots: QuerySnapshot) {
        listOfPsychics.clear()

        for (document in snapshots) {
            val uid = document.id
            listOfPsychics.add(uid)
        }

        binding.viewAllTextView.setOnClickListener {
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics)
        }

//        binding.hotNewPsychicsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setupCategoryButtons(bundle: Bundle) {
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
        binding.viewQAButton.setOnClickListener {
            findNavController().navigate(R.id.action_explore_fragment_to_questions_fragment)
        }
        binding.discoverButton.setOnClickListener {
            findNavController().navigate(R.id.action_explore_fragment_to_subscribe_premium_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}
