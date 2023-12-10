package io.getmadd.openpsychic.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentRegisterPsychicBinding


class RegisterPsychicFragment : Fragment() {

    private var _binding: FragmentRegisterPsychicBinding? = null
    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        _binding = FragmentRegisterPsychicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signupBtn = binding.fragmentRegisterPsychicSignupButton
        val db = Firebase.firestore

        var firstNameET = binding.fragmentRegisterPsychicFirstnameEditText
        val lastNameET = binding.fragmentRegisterPsychicLastnameEditText
        val displaynameET = binding.fragmentRegisterPsychicUsernameEditText
        val passwordET = binding.fragmentRegisterPsychicPasswordEditText
        val emailET = binding.fragmentRegisterPsychicEmailEditText


        signupBtn.setOnClickListener(){

            var psychic = getUserFromFields()

            if(psychic != null){
                psychic.get(key = "email")?.let { it1 ->
                    auth.createUserWithEmailAndPassword(it1, passwordET.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInUserWithEmail:success")
                                val newUser = auth.currentUser

                                if (newUser != null) {
                                    psychic.put("uID",newUser.uid)
                                }

                                db.collection("psychics")
                                    .add(psychic)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                        findNavController().navigate(R.id.action_register_psychic_fragment_to_home_fragment)
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

        var firstNameET = binding.fragmentRegisterPsychicFirstnameEditText
        val lastNameET = binding.fragmentRegisterPsychicLastnameEditText
        val displaynameET = binding.fragmentRegisterPsychicUsernameEditText
        val passwordET = binding.fragmentRegisterPsychicPasswordEditText
        val emailET = binding.fragmentRegisterPsychicEmailEditText

        var firstname = firstNameET.text.toString()
        var lastname = lastNameET.text.toString()
        var email = emailET.text.toString()
        var displayname = displaynameET.text.toString()
        var password = passwordET.text.toString()


        if(firstname.isEmpty() || lastname.isEmpty() || displayname.isEmpty() || email.isEmpty() || password.isEmpty()){

            Toast.makeText(context,"Complete Signup Form", Toast.LENGTH_LONG).show()
        }
        else{
            val user = hashMapOf(
                "firstname" to firstname,
                "lastname" to lastname,
                "displayname" to displayname,
                "email" to email,
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