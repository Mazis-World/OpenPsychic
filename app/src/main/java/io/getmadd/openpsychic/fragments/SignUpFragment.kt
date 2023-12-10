package io.getmadd.openpsychic.fragments

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentSignupBinding
import io.getmadd.openpsychic.model.User


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmailET = binding.fragmentSignupEmailEditText
        val userPassET = binding.fragmentSignupPasswordEditText
        val userNameET = binding.fragmentSignupUsernameEditText

        val signupBtn = binding.fragmentRegisterPsychicSignupButton

        signupBtn.setOnClickListener(){

            if(userEmailET.text.toString().isEmpty() || userPassET.text.toString().isEmpty() || userNameET.text.toString().isEmpty()){
                Toast.makeText(context,"Complete Sign Up Form", Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(userEmailET.text.toString(), userPassET.text.toString())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            //val user = auth.currentUser

                            val newuser = hashMapOf(
                                "displayname" to userNameET.text.toString(),
                                "email" to userEmailET.text.toString(),
                                "uID" to auth.currentUser!!.uid
                            )

                            db.collection("users")
                                .add(newuser)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                    findNavController().navigate(R.id.home_fragment)
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                context,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            // user create failed for some reason
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}