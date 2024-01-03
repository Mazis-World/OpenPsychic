package io.getmadd.openpsychic.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsExpandedBinding
import io.getmadd.openpsychic.model.Psychic

class ExplorePsychicsExpandedFragment: Fragment() {

    private lateinit var _binding: FragmentExplorePsychicsExpandedBinding
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
            // Now you have access to the Psychic object in the Fragment
        }
        binding.expandedBioTextView.text = psychic.bio
        binding.expandedDisplayNameTextView.text = psychic.displayname
        binding.expandedUsernameTextView.text = "@"+psychic.username

        Glide.with(this).load(psychic.displayimgsrc).into(binding.expandedBackgroundImageView)

        binding.requestreadingbtn.setOnClickListener{
            val bundle2 = Bundle()
            bundle2.putSerializable("psychic", psychic)
            findNavController().navigate(R.id.action_explore_psychics_expanded_to_request_reading_fragment, bundle2)
        }
        binding.sendprivatemessagebtn.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }
        binding.schedulelivesessionbtn.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }
    }


}