package io.getmadd.openpsychic.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private var _binding: FragmentLaunchBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLaunchBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        _binding?.let { binding ->
            // Set up navigation actions
            binding.registerPsychicButton.setOnClickListener {
                findNavController().navigate(R.id.action_launchFragment_to_register_psychic_fragment)
            }
            binding.loginButton.setOnClickListener {
                findNavController().navigate(R.id.action_launchFragment_to_loginFragment)
            }
            binding.signUpButton.setOnClickListener {
                findNavController().navigate(R.id.action_launchFragment_to_signup_fragment)
            }

//            // Load ads
//            val adView: AdView = binding.launchfragmentadview
//            val adRequest: AdRequest = AdRequest.Builder().build()
//            adView.loadAd(adRequest)

            // Update online users count
            userCountListener()
        } ?: Log.e(TAG, "Binding is null in onViewCreated")
    }

    private fun userCountListener() {
        val onlineUsersRef = firestore.collection("users")

        onlineUsersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed", error)
                return@addSnapshotListener
            }

            val count = snapshot?.documents?.size ?: 0
            _binding?.let {
                it.usersOnlineCountTextview.text = "Join over $count+ Psychic Advisors & Seekers Today."
            } ?: Log.e(TAG, "Binding is null when trying to update online users count")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "io.getmadd.openpsychic.fragments.main.LaunchFragment"
    }
}
