package io.getmadd.openpsychic.fragments.features

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Psychic
import io.getmadd.openpsychic.model.Question
import io.getmadd.openpsychic.services.UserPreferences

class AskAdvisorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var prefs: UserPreferences
    private lateinit var questionEditText: EditText
    private var questionsAvailable = 0
    private var askedQuestions = 0
    private lateinit var psychic: Psychic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        val view = inflater.inflate(R.layout.fragment_psychic_advisor_question, container, false)
        prefs = UserPreferences(requireContext())
        // Initialize Views
        questionEditText = view.findViewById(R.id.enterQuestionEditText)
        lateinit var psychic: Psychic
        if (bundle != null) {
            psychic = (bundle.getSerializable("psychic") as? Psychic)!!
        }
        view.findViewById<TextView>(R.id.psychicUserNameTextView).text = "@"+psychic.username
        view.findViewById<ImageView>(R.id.closeDialogImageView).setOnClickListener{
            findNavController().popBackStack()
        }
        // Check if user has questions available
        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                questionsAvailable = document.getLong("questionsAvailable")?.toInt() ?: 0
                askedQuestions = document.getLong("questionsAsked")?.toInt() ?: 0
                view.findViewById<TextView>(R.id.questionCountTextView).text = questionsAvailable.toString()
                if (questionsAvailable == 0) {
                    findNavController().navigate(
                        R.id.purchase_questions_fragment,
                    )
                    Toast.makeText(requireActivity(), "You don't have any questions available.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
//                Toast.makeText(requireActivity(), "Failed to check questions available: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Send question to Firestore
        val sendQuestionButton: TextView = view.findViewById(R.id.sendQuestioextview)
        sendQuestionButton.setOnClickListener {
            if (this.questionsAvailable == 0) {
                findNavController().navigate(
                    R.id.purchase_questions_fragment,
                )
                Toast.makeText(requireContext(), "You don't have any questions available.", Toast.LENGTH_SHORT).show()
            }
            else{
                val questionText = questionEditText.text.toString().trim()
                if (questionText.isNotEmpty()) {
                    val question = Question(
                        questionText,
                        Timestamp.now(),
                        false,
                        null,
                        null,
                        false,
                        auth.currentUser!!.uid,
                        psychic.userid!!,
                        null,
                        null
                    )
                    psychic.userid?.let { it1 ->
                        firestore.collection("users").document(it1).collection("questions")
                            .add(question)
                            .addOnSuccessListener {
                                prefs.uid?.let { it1 ->
                                    firestore.collection("users").document(it1).collection("questions")
                                        .add(question)
                                        .addOnSuccessListener {
                                            firestore.collection("users")
                                                .document(auth.currentUser!!.uid).update("questionsAvailable", questionsAvailable-1)
                                            firestore.collection("users")
                                                .document(auth.currentUser!!.uid).update("askedQuestions", askedQuestions+1)
                                            firestore.collection("users")
                                                .document(psychic.userid!!)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    var questionsAsked = document.getLong("questionsAsked")?.toInt() ?: 0
                                                    firestore.collection("users")
                                                        .document(psychic.userid!!).update("questionsAsked", questionsAsked+1)
                                                    findNavController().popBackStack()
                                                    Toast.makeText(context,"You have submitted your question, be patient!", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                }
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a question.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

}
