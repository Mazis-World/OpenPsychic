package kutlwano.oumazi.openpsychic.fragments.features
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.model.Review

class ReviewsAdapter(private val reviewList: MutableList<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val comment = reviewList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reviewText: TextView = itemView.findViewById(R.id.text_comment_content)
        private val reviewUserName: TextView = itemView.findViewById(R.id.text_username)
        private val reviewProfileImg: ImageView = itemView.findViewById(R.id.image_profile)
        private val reviewTimeStamp: TextView = itemView.findViewById(R.id.text_timestamp)
        private val reviewRating: TextView = itemView.findViewById(R.id.text_rating_count)

        fun bind(review: Review) {
            reviewText.text = review.reviewmessage
            reviewUserName.text = review.username
            Glide.with(itemView).load(review.profileimgsrc)
                .error(Glide.with(itemView).load(R.drawable.openpsychiclogo).apply(RequestOptions.circleCropTransform()))
                .apply(RequestOptions.circleCropTransform())
                .into(reviewProfileImg)
            reviewTimeStamp.text = review.reviewtimestamp?.toDate().toString()
            reviewRating.text = "Rating: "+review.reviewrating
        }
    }
}
