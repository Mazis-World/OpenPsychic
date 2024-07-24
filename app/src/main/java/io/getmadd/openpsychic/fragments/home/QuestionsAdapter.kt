package io.getmadd.openpsychic.fragments.home

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Question

class QuestionsAdapter(private val questions: List<Question>) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.question_asked_textview)
        private val psychicNameText: TextView = itemView.findViewById(R.id.psychic_username)
        private val replyText: TextView = itemView.findViewById(R.id.question_response_textview)
        private val dateText: TextView = itemView.findViewById(R.id.question_ask_date_textview)
        private val placeholderAnswerTextView: TextView = itemView.findViewById(R.id.psychic_response_placeholder_textview)
        private val psychicprofileimgview: ImageView = itemView.findViewById(R.id.psychic_profile_image)
        private val psychicResponseEditText: EditText = itemView.findViewById(R.id.psychic_response_edittext)
        private val psychicResponseButton: Button = itemView.findViewById(R.id.submit_answer_button)

        private val firestore = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()

        fun bind(question: Question) {
            questionText.text = question.questionText
            psychicNameText.text = "@${question.psychicUsername}"
            replyText.text = question.answerText
            dateText.text = "${question.questionTimestamp?.toDate().toString()}"
            val uid = auth.uid

            // Determine visibility based on whether the question is answered and the user is the psychic
            if (question.answerText.isNullOrEmpty() && question.psychicUid == uid) {
                psychicResponseEditText.visibility = View.VISIBLE
                psychicResponseButton.visibility = View.VISIBLE
                replyText.visibility = View.GONE
                placeholderAnswerTextView.visibility = View.GONE
            } else if (question.answerText.isNullOrEmpty()) {
                replyText.visibility = View.GONE
                placeholderAnswerTextView.visibility = View.VISIBLE
            } else {
                replyText.visibility = View.VISIBLE
                placeholderAnswerTextView.visibility = View.GONE
            }

            // Load psychic's profile image
            Glide.with(itemView)
                .load(question.psychicProfileImg)
                .apply(RequestOptions.circleCropTransform())
                .error(
                    Glide.with(itemView)
                        .load(R.drawable.openpsychiclogo)
                        .apply(RequestOptions.circleCropTransform())
                )
                .into(psychicprofileimgview)

            // Handle submitting response
            psychicResponseButton.setOnClickListener {
                val response = psychicResponseEditText.text.toString().trim()

                if (response.isNotEmpty()) {
                    val timestamp = Timestamp.now()
                    val updates = mapOf(
                        "answerText" to response,
                        "answered" to true,
                        "timestamp" to timestamp
                    )

                    val batch = firestore.batch()

                    uid?.let { userId ->
                        question.questionId?.let { questionId ->
                            val usersQuestionRef = firestore.collection("users").document(question.userUid!!)
                                .collection("questions").document(questionId)
                            val psychicQuestionRef = firestore.collection("users").document(question.psychicUid!!)
                                .collection("questions").document(questionId)

                            batch.update(usersQuestionRef, updates)
                            batch.update(psychicQuestionRef, updates)

                            batch.commit()
                                .addOnSuccessListener {
                                    // Update UI and show success message
                                    psychicResponseEditText.text.clear()
                                    psychicResponseEditText.visibility = View.GONE
                                    psychicResponseButton.visibility = View.GONE
                                    replyText.text = response
                                    replyText.visibility = View.VISIBLE

                                    Toast.makeText(itemView.context, "Response posted successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                    Log.e(TAG, "Error updating documents", e)
                                    Toast.makeText(itemView.context, "Failed to post response", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                } else {
                    Toast.makeText(itemView.context, "You must enter a response", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
