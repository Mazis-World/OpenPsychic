package io.getmadd.openpsychic.fragments.main

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentRegisterPsychicBinding
import io.getmadd.openpsychic.model.Psychic


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
                psychic.email.let { it1 ->
                    auth.createUserWithEmailAndPassword(it1.toString(), passwordET.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInUserWithEmail:success")
                                val newUser = auth.currentUser
                                var userID = ""
                                if (newUser != null) {
                                    psychic.userid = newUser.uid
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
        binding.backimageview.setOnClickListener{
            findNavController().popBackStack()
        }
        val adView: AdView = binding.registeradview
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

    }
    fun isUsernameValid(username: String): Boolean {
        val regexPattern = "^[\\p{L}\\p{N}_]+$"
        val regex = Regex(regexPattern)
        return regex.matches(username) && !username.contains(" ")
    }
    // getUserText

    fun getUserFromFields(): Psychic? {

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

            Toast.makeText(context,"Complete Signup Form", Toast.LENGTH_SHORT).show()
        }
        if(!isUsernameValid(username)){
            Toast.makeText(context,"Invalid Username", Toast.LENGTH_SHORT).show()
        }
        else{
            val psychic = Psychic(
                userid = " ",
                email = email,
                firstname = firstname,
                lastname = lastname,
                displayname = displayname,
                profileimgsrc = " ",
                displayimgsrc = " ",
                psychicondisplaycategory = " ",
                psychicondisplay = false,
                usertype = "psychic",
                username = username,
                bio = " "
                )

            return psychic
        }

        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}