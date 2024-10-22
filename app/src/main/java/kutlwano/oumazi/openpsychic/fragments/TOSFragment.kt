package kutlwano.oumazi.openpsychic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kutlwano.oumazi.openpsychic.databinding.FragmentTosBinding

class TOSFragment: Fragment() {
    private lateinit var _binding: FragmentTosBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTosBinding.inflate(inflater, container, false)
        return binding.root
    }

}