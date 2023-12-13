package io.getmadd.openpsychic.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentLaunchBinding


class LaunchFragment : Fragment() {

    private var _binding: FragmentLaunchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLaunchBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerPsychicButton.setOnClickListener {
            findNavController().navigate(R.id.action_launchFragment_to_register_psychic_fragment)
        }
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_launchFragment_to_loginFragment)
        }
        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_launchFragment_to_signup_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}