package kutlwano.oumazi.openpsychic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kutlwano.oumazi.openpsychic.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicyFragment: Fragment() {
    private lateinit var _binding: FragmentPrivacyPolicyBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

}