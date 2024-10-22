package kutlwano.oumazi.openpsychic.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kutlwano.oumazi.openpsychic.databinding.FragmentNotificationsBinding

class NotificationsFragment: Fragment() {

    private lateinit var _binding: FragmentNotificationsBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        binding.switch1.isEnabled = false

        binding.notificationSwitchLayout.setOnClickListener{
            Toast.makeText(context,"We're working on Notifications, Keep Your App Updated", Toast.LENGTH_SHORT).show()
        }
    }

}