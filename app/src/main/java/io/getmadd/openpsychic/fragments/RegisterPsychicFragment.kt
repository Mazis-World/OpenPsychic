package io.getmadd.openpsychic.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentRegisterPsychicBinding


class RegisterPsychicFragment : Fragment() {

    private var _binding: FragmentRegisterPsychicBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterPsychicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = binding.fragmentRegisterPsychicUsernameEditText
        val passwordEditText = binding.fragmentRegisterPsychicPasswordEditText
        val emailEditText = binding.fragmentRegisterPsychicEmailEditText
        val signupEditText = binding.fragmentRegisterPsychicSignupButton



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}