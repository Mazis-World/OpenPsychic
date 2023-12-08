package io.getmadd.openpsychic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentLoginBinding
import io.getmadd.openpsychic.services.FirebaseAuthService

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private var auth: FirebaseAuth = Firebase.auth

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser

        binding.loginButton.setOnClickListener(){
            var userEmail = binding.emailTextview.text.toString()
            var userPass = binding.passwordTextview.text.toString()

            auth.signInWithEmailAndPassword(userEmail,userPass).addOnCompleteListener {
                if (it.isSuccessful) {
                    findNavController().navigate(R.id.home_fragment)
                }
                else{
                    Toast.makeText(context,"Login Failed",Toast.LENGTH_LONG).show()
                }

            }

        }
    }

}