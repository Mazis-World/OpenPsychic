package io.getmadd.openpsychic.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        fun bind(question: Question) {
            questionText.text = question.questionText
            psychicNameText.text = "Psychic Advisor: ${question.psychicUsername}"
            replyText.text = question.answerText
            dateText.text = "Date Asked: ${question.questionTimestamp}"
        }
    }
}
