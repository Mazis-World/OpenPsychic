package io.getmadd.openpsychic.fragments.features

import Comment
import Dream
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.services.UserPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DreamAdapter(private val context: Context?, private val dreams: List<Dream>) :
    RecyclerView.Adapter<DreamAdapter.DreamViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val prefs = context?.let { UserPreferences(it) }

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
        private val dreamUserName: TextView = itemView.findViewById(R.id.text_username)
        private val dreamProfileImageView: ImageView = itemView.findViewById(R.id.image_profile)
        private val commentSection: LinearLayout = itemView.findViewById(R.id.commentSection)
        private val reply: ImageButton = itemView.findViewById(R.id.button_reply)
        private val heart: ImageButton = itemView.findViewById(R.id.button_like)
        private val dreamPostDate: TextView = itemView.findViewById(R.id.text_dream_post_date)
        private val postCommentButton: Button = itemView.findViewById(R.id.commentButton)
        private val commentEditText: EditText = itemView.findViewById(R.id.commentEditText)
        private val commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.commentsRecyclerView)
        private val viewCommentsButton: TextView = itemView.findViewById(R.id.text_view_comments)
        private val heartCounts: TextView = itemView.findViewById(R.id.hearts_count)
        private val viewsCounts: TextView = itemView.findViewById(R.id.view_count)
        private val likedDreams = HashSet<String>()
        private val commentsList = mutableListOf<Comment>()
        private val adapter = CommentsAdapter(commentsList)
        private val user = FirebaseAuth.getInstance()

        fun bind(dream: Dream) {
            commentsList.clear()
            val commentsRef = firestore.collection("dreamPost").document(dream.dreamId).collection("comments")
            val docRef = firestore.collection("dreamPost").document(dream.dreamId)
            val heartersRef = firestore.collection("dreamPost").document(dream.dreamId).collection("hearters")
            val layoutManager = LinearLayoutManager(itemView.context)
            commentsRecyclerView.layoutManager = layoutManager
            commentsRecyclerView.adapter = adapter
            dreamContentTextView.text = dream.content
            dreamUserName.text = dream.userName
            Glide.with(itemView)
                .load(dream.userProfileImgSrc)
                .placeholder(R.drawable.openpsychiclogo) // Use the op_logo drawable as the placeholder
                .apply(RequestOptions.circleCropTransform())
                .into(dreamProfileImageView)

            dreamPostDate.text = dream.date

            commentsRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    viewCommentsButton.visibility = View.VISIBLE
                    viewCommentsButton.text = "View ${snapshot.size()} Comments"

                    commentsList.clear()
                    for (document in snapshot.documents) {
                        val comment = document.toObject(Comment::class.java)
                        if (comment != null) {
                            commentsList.add(comment)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "No comments found")
                }
            }

            heartersRef.document(auth.uid.toString()).get().addOnSuccessListener {
                if (it.exists()) {
                    heart.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    heart.setImageResource(R.drawable.ic_heart)
                }
            }

            heartersRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    heartCounts.visibility = View.VISIBLE
                    heartCounts.text = snapshot.documents.count().toString()

                    for (document in snapshot.documents) {
                        val comment = document.toObject(Comment::class.java)
                        if (comment != null) {
                            commentsList.add(comment)
                        }
                    }
                    notifyItemChanged(adapterPosition)
                } else {
                    heartCounts.visibility = View.GONE
                    Log.d(TAG, "Current data: null")
                }
            }

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    viewsCounts.text = snapshot.getField<Int>("views").toString()
                    notifyItemChanged(adapterPosition)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

            viewCommentsButton.setOnClickListener {
                if (commentsRecyclerView.visibility == View.VISIBLE) {
                    commentsRecyclerView.visibility = View.GONE
                    viewCommentsButton.text = "View ${commentsList.size} Comments"
                } else {
                    commentsRecyclerView.visibility = View.VISIBLE
                    viewCommentsButton.text = "Close Comments"
                }
            }

            heart.setOnClickListener {
                if (likedDreams.contains(dream.dreamId)) {
                    heartersRef.document(auth.uid.toString()).delete()
                    likedDreams.remove(dream.dreamId)
                    heart.setImageResource(R.drawable.ic_heart)
                } else {
                    heartersRef.document(auth.uid.toString()).set(mapOf("dreamId" to dream.dreamId))
                    likedDreams.add(dream.dreamId)
                    heart.setImageResource(R.drawable.ic_heart_filled)
                }
            }

            reply.setOnClickListener {
                commentSection.visibility = if (commentSection.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            postCommentButton.setOnClickListener {
                if (commentEditText.text.isNotEmpty()) {
                    val comment = Comment(
                        dreamId = dream.dreamId,
                        content = commentEditText.text.toString(),
                        userId = auth.uid.toString(),
                        userProfileImgSrc = prefs?.profileimgsrc!!,
                        userName = prefs?.username!!,
                        date = getCurrentDate()
                    )
                    firestore.collection("dreamPost").document(dream.dreamId).collection("comments").add(comment)
                    commentEditText.text.clear()
                    commentSection.visibility = View.GONE
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Calendar.getInstance().time
        return dateFormat.format(date)
    }

    companion object {
        private const val TAG = "DreamAdapter"
    }
}
