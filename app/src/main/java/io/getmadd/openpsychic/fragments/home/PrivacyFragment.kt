package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentAccountBinding
import io.getmadd.openpsychic.databinding.FragmentPrivacyBinding
import io.getmadd.openpsychic.databinding.FragmentTermsBinding
import io.getmadd.openpsychic.services.UserPreferences

class PrivacyFragment: Fragment() {

    private lateinit var _binding: FragmentPrivacyBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        binding.terminateaccount.setOnClickListener{
            Toast.makeText(context,"We're unable to terminate your account just yet, please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

}