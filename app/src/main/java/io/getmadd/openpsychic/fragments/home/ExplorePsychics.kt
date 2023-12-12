package io.getmadd.openpsychic.fragments.home

import ExplorePsychicsAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsBinding
import io.getmadd.openpsychic.model.Psychic

class ExplorePsychics : Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsBinding
    private val binding get() = _binding
    val db = Firebase.firestore

    var listofPsychics = ArrayList<Psychic>()
    lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = (arguments!!.getString("Category").toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorePsychicsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.explorePsychicsCategoryTextView.text = category

        db.collection("psychics")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var psychic = Psychic(
                        displayname = document.getString("displayname")!!,
                        email = document.getString("uID")!!,
                        uID = document.getString("email")!!,
                        firstname = document.getString("firstname")!!,
                        lastname = document.getString("lastname")!!
                    )
                    listofPsychics.add(psychic)
                }
                binding.recyclerView.adapter = ExplorePsychicsAdapter(items = listofPsychics, {})

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


    }


}