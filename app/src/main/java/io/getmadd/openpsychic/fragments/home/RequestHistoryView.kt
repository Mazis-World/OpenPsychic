package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.getmadd.openpsychic.databinding.FragmentRequestHistoryViewBinding
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.Request


class RequestHistoryView : Fragment() {
    private lateinit var binding: FragmentRequestHistoryViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding
        binding = FragmentRequestHistoryViewBinding.inflate(inflater, container, false)
        val bundle = arguments
        lateinit var request: Request
        var userPsychic: Psychic
        var db = Firebase.firestore
        var userid = Firebase.auth.uid
        var usertype = "null"

        db.collection("users").document(userid!!).get()
            .addOnSuccessListener { result ->
                usertype = result.getString("usertype").toString()
            }

        var input : TextInputLayout = binding.replyreditextinputlayout

        if (bundle != null) {

            request = (bundle.getSerializable("request") as? Request)!!
            // Now you have access to the Psychic object in the Fragment
            binding.closebutton.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.timestamptextview.text = request.timestamp.toDate().toString()
            binding.statustextview.text = request.requeststatus
            binding.fullnametextview.text = request.fullName
            binding.subjecttextview.text = request.specificQuestion
            binding.dobtextview.text = "DOB: " + request.dateOfBirth
            binding.energyfocustextview.text = "Energy Focus: " + request.energyFocus
            binding.preferredreadingmethodtextview.text =
                "Preferred Reading Method: " + request.preferredReadingMethod
            binding.opentoinsightstextview.text = "Open To Insights: " + request.openToInsights
            binding.requestmessagetextview.text = request.message
            binding.usernamettextview.visibility = View.GONE

            val userrequestfef = db.collection("users").document("$userid").collection("requests").document(request.senderid)
            val psychicrequestref = db.collection("users").document("$userid").collection("requests").document(request.receiverid)

            userrequestfef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // The document exists, you can now update it
                        val requestData = documentSnapshot.data
                        // Continue to the next step
                    } else {
                        println("No such document!")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting document: $exception")
                }

            if(usertype == "psychic"){
                binding.replyreditextinputlayout.visibility = View.VISIBLE

                input.setEndIconOnClickListener {
                    if(binding.replyedittext.text.toString() != ""){
                        var txt = binding.replyedittext.text
                        val reply = hashMapOf(
                            "reply" to txt,
                        )

                        userrequestfef.update(reply as Map<String, Any>)
                            .addOnSuccessListener {
                                println("Document successfully updated!")
                            }
                            .addOnFailureListener { exception ->
                                println("Error updating document: $exception")
                            }

                        psychicrequestref.update(reply as Map<String, Any>)
                            .addOnSuccessListener {
                                println("Document successfully updated!")
                            }
                            .addOnFailureListener { exception ->
                                println("Error updating document: $exception")
                            }

                        binding.replytextview.text = txt
                        binding.replyedittext.text =  Editable.Factory.getInstance().newEditable(" ")
                        binding.replyedittext.visibility = View.GONE
                    }
                }
            }
            else{
                binding.replyreditextinputlayout.visibility = View.GONE
            }

            if(request.reply.isNotEmpty()){
                binding.replytextview.text = request.reply
            }
        }
        return binding.root
    }

}
