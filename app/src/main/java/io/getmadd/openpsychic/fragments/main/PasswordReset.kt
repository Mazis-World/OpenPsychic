package io.getmadd.openpsychic.fragments.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentLoginBinding
import io.getmadd.openpsychic.databinding.FragmentPasswordResetBinding

class PasswordReset: Fragment() {

    private var _binding: FragmentPasswordResetBinding? = null
    private var auth: FirebaseAuth = Firebase.auth

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser

        binding.resetBackImageview.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.resetButton.setOnClickListener{
            var email = binding.emailResetTextview.text.toString()
            sendPasswordResetEmail(email)
        }

//        val adView: AdView = binding.loginadview
//        val adRequest: AdRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)

    }

    fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    Toast.makeText(context,"Password Reset Email Sent.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    // Error occurred
                    Toast.makeText(context,"${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    println("Error: ${task.exception?.message}")
                }
            }
    }

}