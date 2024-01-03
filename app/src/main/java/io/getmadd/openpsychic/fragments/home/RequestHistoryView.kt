package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentRequestHistoryViewBinding
import io.getmadd.openpsychic.databinding.FragmentRequestReadingBinding
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.Request


class RequestHistoryView : Fragment() {
    private lateinit var binding: FragmentRequestHistoryViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentRequestHistoryViewBinding.inflate(inflater, container, false)
        val bundle = arguments
        lateinit var request: Request
        if (bundle != null) {
            request = (bundle.getSerializable("request") as? Request)!!
            // Now you have access to the Psychic object in the Fragment
            binding.closebutton.setOnClickListener{
                findNavController().popBackStack()
            }
            binding.timestamptextview.text = request.timestamp.toDate().toString()
            binding.statustextview.text = request.requeststatus
            binding.fullnametextview.text = request.fullName
            binding.subjecttextview.text = request.specificQuestion
            binding.dobtextview.text = "DOB: " + request.dateOfBirth
            binding.energyfocustextview.text = "Energy Focus: " + request.energyFocus
            binding.preferredreadingmethodtextview.text = "Preferred Reading Method: " + request.preferredReadingMethod
            binding.opentoinsightstextview.text = "Open To Insights: " + request.openToInsights
            binding.requestmessagetextview.text = request.message
            binding.usernamettextview.visibility = View.GONE

        }
        return binding.root
    }
}
