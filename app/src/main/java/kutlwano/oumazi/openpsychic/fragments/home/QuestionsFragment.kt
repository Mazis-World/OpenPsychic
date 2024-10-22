package kutlwano.oumazi.openpsychic.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.databinding.FragmentQuestionsBinding
import kutlwano.oumazi.openpsychic.model.Question

class QuestionsFragment : Fragment() {

    private lateinit var binding: FragmentQuestionsBinding
    private lateinit var questionsAdapter: QuestionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val userUid = FirebaseAuth.getInstance().uid.toString()
        val questionsRef = firestore.collection("users").document(userUid).collection("questions")
        val questionCountRef = firestore.collection("users").document(userUid)
        val questionsList = mutableListOf<Question>()
        questionsAdapter = QuestionsAdapter(questionsList)
        binding.questionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.questionsRecyclerView.adapter = questionsAdapter
        val questionsLeftTextView = view.findViewById<TextView>(R.id.questionsLeftView)
        questionCountRef.get()
            .addOnSuccessListener { documents ->
                questionsLeftTextView.text = "You have ${documents.get("questionsAvailable")} questions left "
            }

        questionsLeftTextView.setOnClickListener{
            findNavController().navigate(R.id.purchase_questions_fragment)
        }

        questionsRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val question = document.toObject(Question::class.java)
                    questionsList.add(question)
                }
                questionsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting questions: ", exception)
                // Handle the error
            }



    }
}
