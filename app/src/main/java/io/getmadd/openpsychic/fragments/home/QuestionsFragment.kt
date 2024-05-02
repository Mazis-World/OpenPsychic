package io.getmadd.openpsychic.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.databinding.FragmentQuestionsBinding
import io.getmadd.openpsychic.model.Question

class QuestionsFragment : Fragment() {

    private lateinit var binding: FragmentQuestionsBinding
    private lateinit var questionsAdapter: QuestionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val userUid = FirebaseAuth.getInstance().uid.toString()
        val questionsRef = firestore.collection("users").document(userUid).collection("questions")
        val questionsList = mutableListOf<Question>()

        questionsRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val question = document.toObject(Question::class.java)
                    questionsList.add(question)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting questions: ", exception)
                // Handle the error
            }

        questionsAdapter = QuestionsAdapter(questionsList)
        binding.questionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.questionsRecyclerView.adapter = questionsAdapter


    }
}
