import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Psychic
import java.util.*
import javax.sql.DataSource


class ExplorePsychicsAdapter(
    private val items: MutableList<String>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<ExplorePsychicsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PsychicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_explore_psychics_card, parent, false), this
        )
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position, listener)
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: String, pos: Int, listener: (Int) -> Unit)
    }

    class AdViewHolder(itemView: View) : ViewHolder(itemView) {
        private val adView: AdView = itemView.findViewById(R.id.explore_psychics_ad_view)

        override fun bind(item: String, pos: Int, listener: (Int) -> Unit) {
//            val adRequest = AdRequest.Builder().build()
//            adView.loadAd(adRequest)
        }
    }

    class PsychicViewHolder(itemView: View, private val adapter: RecyclerView.Adapter<*>) :
        ExplorePsychicsAdapter.ViewHolder(itemView) {
        private val cvItem: CardView = itemView.findViewById(R.id.fragment_explore_psychics_card)
        private val displayName: TextView = itemView.findViewById(R.id.displayNameTextView)
        private val userName: TextView = itemView.findViewById(R.id.usernameTextView)
        private val starRating: RatingBar = itemView.findViewById(R.id.explorepsychicsratingBar)
        private val status_icon: ImageView = itemView.findViewById(R.id.status_indicator_imageview)
        private val profile_image: ImageView = itemView.findViewById(R.id.psychicProfileImageView)
        private val backgroundImg: ImageView =
            itemView.findViewById(R.id.explore_psychics_expanded_card_background_IV)
        private val twentyFourHoursAgo =
            Date(Date().time - 24 * 60 * 60 * 1000) // 24 hours in milliseconds
        private val db = FirebaseFirestore.getInstance()
        private var item = Psychic()

        override fun bind(uid: String, pos: Int, listener: (Int) -> Unit) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { result ->
                    val newItem = result.toObject(Psychic::class.java)
                    if (newItem != null) {
                        item = newItem
                        updateUI(item, pos)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }

        }

        private fun updateUI(item: Psychic, pos: Int) {
            Glide.with(itemView)
                .load(item.profileimgsrc)
                .apply(RequestOptions.circleCropTransform())
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(profile_image)

            Glide.with(itemView)
                .load(item.displayimgsrc ?: R.drawable.openpsychiclogo)
                .error(R.drawable.openpsychiclogo) // Load default logo if the image fails to load
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
//                        adapter.notifyItemChanged(pos)
                        return false
                    }
                })
                .into(backgroundImg)

            status_icon.setImageResource(
                when {
                    item.isOnline -> R.drawable.ic_status_online
                    item.lastOnline?.toDate()
                        ?.before(twentyFourHoursAgo) == true -> R.drawable.ic_status_away

                    else -> R.drawable.ic_status_offline
                }
            )

            displayName.text = item.displayname
            userName.text = "@${item.username}"

            val bundle = Bundle()
            bundle.putSerializable("psychic", item)

            cvItem.setOnClickListener {
                findNavController(view = itemView).navigate(R.id.explore_psychics_expanded, bundle)
            }

            if (profile_image.drawable == null) {
                setProfileImage(itemView,profile_image)
            }
        }

        fun setProfileImage(itemView: View, profile_image: ImageView){
            Glide.with(itemView)
                .load(R.drawable.openpsychiclogo)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_image)
        }
    }
}