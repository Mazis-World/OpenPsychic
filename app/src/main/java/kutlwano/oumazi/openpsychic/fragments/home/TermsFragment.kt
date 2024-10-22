package kutlwano.oumazi.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentTermsBinding

class TermsFragment: Fragment() {

    private lateinit var _binding: FragmentTermsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        binding.termsofservice.setOnClickListener{
            findNavController().navigate(R.id.tos_policy_fragment)
        }

        binding.privacypolicy.setOnClickListener{
            findNavController().navigate(R.id.privacy_policy_fragment)
        }
    }

}