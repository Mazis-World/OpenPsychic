package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.getmadd.openpsychic.databinding.FragmentExplorePsychicsExpandedBinding

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

        binding.expandedBioTextView.text = arguments?.getString("bio")
        binding.expandedDisplayNameTextView.text = arguments?.getString("displayname")
        binding.expandedUsernameTextView.text = "@"+arguments?.getString("username")


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