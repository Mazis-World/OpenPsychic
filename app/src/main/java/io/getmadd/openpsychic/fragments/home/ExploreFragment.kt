package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentExploreBinding


class ExploreFragment : Fragment() {

    private lateinit var _binding: FragmentExploreBinding

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()

        binding.homeCategory0.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_spirit))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory1.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_tarot_card))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory2.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_turban))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory3.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_magic_ball))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory4.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_palm))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory5.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_saturn))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory6.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_dream))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory7.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_ghost))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }
        binding.homeCategory8.setOnClickListener {
            bundle.putString("Category", getString(R.string.category_heart))
            findNavController().navigate(R.id.action_explore_fragment_to_explore_psychics, bundle)
        }

    }
}
