package kutlwano.oumazi.openpsychic.fragments.features
import Comment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kutlwano.oumazi.openpsychic.R

class CommentsAdapter(private val commentsList: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentText: TextView = itemView.findViewById(R.id.text_comment_content)
        private val commentUserName: TextView = itemView.findViewById(R.id.text_username)
        private val commentProfileImg: ImageView = itemView.findViewById(R.id.image_profile)

        fun bind(comment: Comment) {
            commentText.text = comment.content
            commentUserName.text = comment.userName
            Glide.with(itemView).load(comment.userProfileImgSrc)
                .error(Glide.with(itemView).load(R.drawable.openpsychiclogo))
                .apply(RequestOptions.circleCropTransform())
                .into(commentProfileImg)

        }
    }
}
