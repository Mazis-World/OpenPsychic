package io.getmadd.openpsychic.fragments.home

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsExpandedBinding
import io.getmadd.openpsychic.model.MessageMetaData
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.User

class ExplorePsychicsExpandedFragment: Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsExpandedBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorePsychicsExpandedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val db = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        var userId = auth.uid.toString()
        var userType: String? = null
        var usermetadata = MessageMetaData()

        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
            // Now you have access to the Psychic object in the Fragment
        }
        binding.explorepsychicexpandedsratingBar.rating = psychic.psychicrating!!
        binding.expandedBioTextView.text = psychic.bio
        binding.expandedDisplayNameTextView.text = psychic.displayname
        binding.expandedUsernameTextView.text = "@"+psychic.username

        if(psychic.displayimgsrc != " "){
            Glide.with(this).load(psychic.displayimgsrc).into(binding.expandedBackgroundImageView)
        }
        else
            Glide.with(this).load(R.drawable.openpsychiclogo).into(binding.expandedBackgroundImageView)

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { result ->
                userType = result.getString("usertype").toString()

                if(userType == "user") {
                    usermetadata.userprofileimgsrc = result.getString("profileimgsrc").toString()
                    usermetadata.username = result.getString("username").toString()
                }
                else if (userType == "psychic"){
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
        binding.requestreadingbtn.setOnClickListener{
            if(userType == "psychic") {
                Toast.makeText(context,"Psychic Function Not Allowed",Toast.LENGTH_SHORT).show()
            }
            else{
                val bundle = Bundle()
                bundle.putSerializable("psychic", psychic)
                findNavController().navigate(R.id.action_explore_psychics_expanded_to_request_reading_fragment, bundle)
            }
        }
        binding.sendprivatemessagebtn.setOnClickListener{
            if(userType == "psychic") {
                Toast.makeText(context,"Psychic Function Not Allowed",Toast.LENGTH_SHORT).show()
            }
            else {
                usermetadata.psychicprofileimgsrc = psychic.profileimgsrc
                val bundle = Bundle()
                bundle.putSerializable("psychic", psychic)
                bundle.putSerializable("usermetadata",usermetadata)
                findNavController().navigate(
                    R.id.action_explore_psychics_expanded_to_message_thread_fragment,
                    bundle
                )
            }
        }
        binding.schedulelivesessionbtn.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }

    }


}