package io.getmadd.openpsychic.fragments.main

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentRegisterPsychicBinding


class RegisterPsychicFragment : Fragment() {

    private var _binding: FragmentRegisterPsychicBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!
    

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        _binding = FragmentRegisterPsychicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signupBtn = binding.fragmentRegisterPsychicSignupButton
        val db = Firebase.firestore
        val passwordET = binding.fragmentRegisterPsychicPasswordEditText


        signupBtn.setOnClickListener {

            val psychic = getUserFromFields()

            if(psychic != null){
                psychic.get(key = "email")?.let { it1 ->
                    auth.createUserWithEmailAndPassword(it1, passwordET.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInUserWithEmail:success")
                                val newUser = auth.currentUser
                                var userID = ""
                                if (newUser != null) {
                                    psychic.put("userID",newUser.uid)
                                    userID = newUser.uid
                                }

                                db.collection("users").document(userID).set(psychic)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot added with ID: ${userID}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }
        }

    }

    // getUserText

    fun getUserFromFields(): HashMap<String, String>? {

        val firstNameET = binding.fragmentRegisterPsychicFirstnameEditText
        val lastNameET = binding.fragmentRegisterPsychicLastnameEditText
        val userNameET = binding.fragmentRegisterPsychicUsernameEditText
        val passwordET = binding.fragmentRegisterPsychicPasswordEditText
        val emailET = binding.fragmentRegisterPsychicEmailEditText
        val displaynameET = binding.fragmentRegisterPsychicDisplaynameEditText

        val firstname = firstNameET.text.toString()
        val lastname = lastNameET.text.toString()
        val email = emailET.text.toString()
        val username = userNameET.text.toString()
        val password = passwordET.text.toString()
        val displayname = displaynameET.text.toString()


        if(firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || displayname.isEmpty()) {

            Toast.makeText(context,"Complete Signup Form", Toast.LENGTH_LONG).show()
        }
        else{
            val user = hashMapOf(
                "email" to email,
                "firstname" to firstname,
                "lastname" to lastname,
                "displayname" to displayname,
                "username" to username,
                "usertype" to "psychic",
                "bio" to " ",
                "profileImgSrc" to " ",
                "displayImgSrc" to " ",
            )

            return user
        }

        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}