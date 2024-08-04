import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.databinding.FragmentRequestReadingBinding
import io.getmadd.openpsychic.fragments.home.SubscribeFragment
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.Request
import io.getmadd.openpsychic.model.RequestStatusUpdate

class RequestReadingFragment : Fragment() {

    private lateinit var binding: FragmentRequestReadingBinding
    private var db = Firebase.firestore
    private var userid = Firebase.auth.uid
    private lateinit var psychicid: String
    private var isPremium = false

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = FragmentRequestReadingBinding.inflate(inflater, container, false)
        val bundle = arguments
        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
            psychicid = psychic.userid!!
            Log.e("RequestReadingFragment","Psychic Id:"+psychic.userid)
        }
        db.collection("users").document("${userid}")
            .get()
            .addOnSuccessListener { result ->
                isPremium = result.getBoolean("isPremium") ?: false
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        val readingMethods = resources.getStringArray(R.array.reading_methods)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, readingMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReadingMethod.adapter = adapter
        binding.button.setOnClickListener {
            if(binding.editTextFullName.text.isNotEmpty()
                && binding.editTextText.text.isNotEmpty()
                && binding.editTextFullName.text.isNotEmpty()
                && binding.editTextEnergyFocus.text.isNotEmpty()
                && binding.editTextDateOfBirth.text.isNotEmpty()
                && binding.editTextSpecificQuestion.text.isNotEmpty()){

            val request = Request(
                binding.editTextFullName.text.toString(),
                binding.editTextDateOfBirth.text.toString(),
                binding.editTextSpecificQuestion.text.toString(),
                binding.editTextEnergyFocus.text.toString(),
                binding.checkBoxOpenness.isChecked,
                binding.spinnerReadingMethod.selectedItem.toString(),
                binding.editTextText.text.toString(),
                userid!!,
                psychicid,
                Timestamp.now(),
                "sent",
                "request",
                " ",
                "Text Reading",
                psychic.psychicondisplaycategory!!
                )

                saveRequestToDatabase(request)
        }
            else{
                Toast.makeText(context,"Complete Request Form", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }

    private fun saveRequestToDatabase(request: Request) {
        val senderref = db.collection("users").document(request.senderid).collection("request")
            .document(request.receiverid)

        val receiverref = db.collection("users").document(request.receiverid).collection("request")
            .document(request.senderid)

        if(request.senderid == request.receiverid){
            Toast.makeText(context,"You can't submit a request to yourself", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        senderref.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Toast.makeText(context,"You can only make one request at a time", Toast.LENGTH_SHORT).show()
                    // Document exists
                    val data = documentSnapshot.data
                    // Process data or perform further actions
                } else {
                    senderref.set(request)
                        .addOnSuccessListener {
                            // io.getmadd.openpsychic.model.Request saved successfully
                            // You can perform additional actions here
                            var requestStatusUpdate = RequestStatusUpdate()
                            requestStatusUpdate.status = "created"
                            requestStatusUpdate.timestamp = Timestamp.now()
                            Log.e("io.getmadd.openpsychic.model.Request Reading", "Sender io.getmadd.openpsychic.model.Request Submitting Successfully")
                            receiverref.set(request)
                                .addOnSuccessListener {
                                    // io.getmadd.openpsychic.model.Request saved successfully
                                    // You can perform additional actions here
                                    Log.e("io.getmadd.openpsychic.model.Request Reading", "Receiver io.getmadd.openpsychic.model.Request Submitting Successfully")
                                    findNavController().popBackStack()
                                    Toast.makeText(context,"Request Submitted Succesively",Toast.LENGTH_SHORT).show()
                                    senderref.collection("status").document().set(requestStatusUpdate)
                                    receiverref.collection("status").document().set(requestStatusUpdate)
                                }
                                .addOnFailureListener {
                                    // Handle failure
                                    Log.e("Request Reading", it.message.toString())
                                }
                        }
                        .addOnFailureListener {
                            // Handle failure
                            Log.e("io.getmadd.openpsychic.model.Request Reading", it.message.toString())
                            Toast.makeText(context,"io.getmadd.openpsychic.model.Request Submission Failed",Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e(TAG, "Error getting document: ", exception)
            }
    }
}
