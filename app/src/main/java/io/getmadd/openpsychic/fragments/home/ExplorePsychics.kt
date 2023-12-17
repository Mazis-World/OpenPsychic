package io.getmadd.openpsychic.fragments.home

import ExplorePsychicsAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsBinding
import io.getmadd.openpsychic.model.User


class ExplorePsychics : Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsBinding
    private val binding get() = _binding
    val db = Firebase.firestore

    var listofPsychics = ArrayList<User>()
    lateinit var category_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category_type = (arguments!!.getString("category_type").toString())

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
        binding.explorePsychicsCategoryTextView.text = category_type
        db.collection("psychicOnDisplay").document(category_type).collection("psychicsOnDisplay")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var psychic = User(
                        username = document.getString("username")!!,
                        displayname = document.getString("displayname")!!,
                        email = document.getString("email")!!,
                        userID = document.getString("userID")!!,
                        firstname = document.getString("firstname")!!,
                        lastname = document.getString("lastname")!!,
                        displayImgSrc = document.getString("backgroundImgSrc")!!,
                        profileImgSrc = document.getString("profileImgSrc")!!,
                        bio = document.getString("bio")!!,
                        usertype = document.getString("usertype")!!,
                        joinedLiveStreams = null
                    )
                    listofPsychics.add(psychic)
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        binding.recyclerView.adapter = ExplorePsychicsAdapter(items = listofPsychics, {})


    }


}