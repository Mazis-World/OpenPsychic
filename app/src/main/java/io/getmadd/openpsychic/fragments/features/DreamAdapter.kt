package io.getmadd.openpsychic.fragments.features

import Dream
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.getmadd.openpsychic.R

class DreamAdapter(private val dreams: List<Dream>) :
    RecyclerView.Adapter<DreamAdapter.DreamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dream, parent, false)
        return DreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        val dream = dreams[position]
        holder.bind(dream)
    }

    override fun getItemCount(): Int {
        return dreams.size
    }

    inner class DreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dreamContentTextView: TextView = itemView.findViewById(R.id.text_dream_content)
        private val reply: ImageButton = itemView.findViewById(R.id.button_reply)

        fun bind(dream: Dream) {
            dreamContentTextView.text = dream.content

            reply.setOnClickListener {

            }
        }
    }
}

