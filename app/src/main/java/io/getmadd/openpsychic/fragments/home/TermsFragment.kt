package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentAccountBinding
import io.getmadd.openpsychic.databinding.FragmentTermsBinding
import io.getmadd.openpsychic.services.UserPreferences

class TermsFragment: Fragment() {

    private lateinit var _binding:FragmentTermsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        binding.termsofservice.setOnClickListener{
            findNavController().navigate(R.id.tos_policy_fragment)
        }

        binding.privacypolicy.setOnClickListener{
            findNavController().navigate(R.id.privacy_policy_fragment)
        }
    }

}