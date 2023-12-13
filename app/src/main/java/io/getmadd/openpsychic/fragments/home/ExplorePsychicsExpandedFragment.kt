package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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

}