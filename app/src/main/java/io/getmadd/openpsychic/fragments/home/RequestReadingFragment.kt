import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentRequestReadingBinding
import io.getmadd.openpsychic.model.Psychic

class RequestReadingFragment : Fragment() {

    private lateinit var binding: FragmentRequestReadingBinding
    private var db = Firebase.firestore
    private var userid = Firebase.auth.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentRequestReadingBinding.inflate(inflater, container, false)
        val bundle = arguments
        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
            // Now you have access to the Psychic object in the Fragment
        }
        // Set up the spinner adapter
        val readingMethods = resources.getStringArray(R.array.reading_methods)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, readingMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReadingMethod.adapter = adapter

        // Set up the click listener for the submit button
        binding.button.setOnClickListener {
            // Retrieve data from the binding
            val request = Request(
                binding.editTextFullName.text.toString(),
                binding.editTextDateOfBirth.text.toString(),
                binding.editTextSpecificQuestion.text.toString(),
                binding.editTextEnergyFocus.text.toString(),
                binding.checkBoxOpenness.isChecked,
                binding.spinnerReadingMethod.selectedItem.toString(),
                binding.editTextText.text.toString(),
                userid!!,
                psychic.userid,
                System.currentTimeMillis(),
                "sent",
                "request",
                " ",
                "reading"
            )

            // Save the request to the database (replace this with your database logic)
            saveRequestToDatabase(request)

        }
        return binding.root
    }

    private fun saveRequestToDatabase(request: Request) {
        val senderref = db.collection("users").document(request.senderid).collection("messagethread")
            .document(request.receiverid).collection("messages").document()

        val receiverref = db.collection("users").document(request.receiverid).collection("messagethread")
            .document(request.senderid).collection("messages").document()

        senderref.set(request)
            .addOnSuccessListener {
                // Request saved successfully
                // You can perform additional actions here
                Log.e("Request Reading", "Sender Request Submitting Successfully")
                receiverref.set(request)
                    .addOnSuccessListener {
                        // Request saved successfully
                        // You can perform additional actions here
                        Log.e("Request Reading", "Receiver Request Submitting Successfully")
                        findNavController().popBackStack()
                        Toast.makeText(context,"Request Submitted Succesively",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // Handle failure
                        Log.e("Request Reading", it.message.toString())
                    }
            }
            .addOnFailureListener {
                // Handle failure
                Log.e("Request Reading", it.message.toString())
                Toast.makeText(context,"Request Submission Failed",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

    }
}
