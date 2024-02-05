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
import io.getmadd.openpsychic.services.UserPreferences

class AccountFragment: Fragment() {

    private lateinit var _binding:FragmentAccountBinding
    private val binding get() = _binding
    private var db = Firebase.firestore
    private lateinit var prefs: UserPreferences



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = UserPreferences(context!!)
        setupUI()
    }

    private fun setupUI(){

        if(prefs.usertype == "psychic"){
            binding.psychicLayout.visibility = View.VISIBLE
        }
        else binding.psychicLayout.visibility = View.GONE


        binding.usernameEditText.setText("User Name: " + prefs.username)
        binding.displaynameedittext.setText("Display Name: " + prefs.displayname)
        binding.biographytextview.setText("Biography: "+ prefs.bio)
        binding.emailedittext.setText("Email: " + prefs.email)
    }

    private fun updateButtons(){
        binding.nameupdatebutton.setOnClickListener {
            if(prefs.username != binding.usernameEditText.text.toString() && binding.usernameEditText.text != null){
                db.collection("users").document(prefs.uid!!).update("username", binding.usernameEditText.text.toString()).addOnSuccessListener {
                    prefs.username = binding.usernameEditText.text.toString()
                    Toast.makeText(context, " Username Updated", Toast.LENGTH_SHORT).show()
                }

            }
        }
        binding.displaynameupdatebutton.setOnClickListener {
            if(prefs.displayname != binding.displaynameedittext.text.toString() && binding.displaynameedittext.text != null){
                db.collection("users").document(prefs.uid!!).update("displayname", binding.displaynameedittext.text.toString())
                prefs.displayname = binding.displaynameedittext.text.toString()
                Toast.makeText(context, " Display Name Updated", Toast.LENGTH_SHORT).show()

            }
        }
        binding.biographytextview.setOnClickListener{
            if(prefs.username != binding.usernameEditText.text.toString() && binding.usernameEditText.text != null){
                db.collection("users").document(prefs.uid!!).update("bio", binding.usernameEditText.text.toString())
            }
        }
    }





}