package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

        binding.button.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }
        binding.button2.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }
        binding.button3.setOnClickListener{
            Toast.makeText(context,"We're Working On It",Toast.LENGTH_SHORT).show()
        }
    }


}