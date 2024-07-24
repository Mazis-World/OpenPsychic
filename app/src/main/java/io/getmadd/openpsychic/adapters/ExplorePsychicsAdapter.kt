import android.content.ContentValues
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.FirebaseFirestore
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Psychic
import java.util.*



class ExplorePsychicsAdapter(
    private val items: MutableList<String>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<ExplorePsychicsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  PsychicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_explore_psychics_card, parent, false)
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
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    class PsychicViewHolder(itemView: View) : ViewHolder(itemView) {
        private val cvItem: CardView = itemView.findViewById(R.id.fragment_explore_psychics_card)
        private val displayName: TextView = itemView.findViewById(R.id.displayNameTextView)
        private val userName: TextView = itemView.findViewById(R.id.usernameTextView)
        private val starRating: RatingBar = itemView.findViewById(R.id.explorepsychicsratingBar)
        private val status_icon: ImageView = itemView.findViewById(R.id.status_indicator_imageview)
        private val backgroundImg: ImageView =
            itemView.findViewById(R.id.explore_psychics_expanded_card_background_IV)
        val currentTime = Date()
        val twentyFourHoursAgo = Date(currentTime.time - 24 * 60 * 60 * 1000) // 24 hours in milliseconds
        val db = FirebaseFirestore.getInstance()
        var item = Psychic()

        override fun bind(uid: String, pos: Int, listener: (Int) -> Unit){

            db.collection("users").document("${uid}")
                .get()
                .addOnSuccessListener { result ->
                    if(result.toObject(Psychic::class.java) != null){
                        item = result.toObject(Psychic::class.java)!!
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }

            item.displayimgsrc?.let { Log.e("ExplorePsychicsAdapter", it) }
            if(item.displayimgsrc != " "){
                Glide.with(itemView).load(item.displayimgsrc).into(backgroundImg)
            }else {
                Glide.with(itemView).load(R.drawable.openpsychiclogo).into(backgroundImg)
            }

            if(item.isOnline == true){
                status_icon.setImageResource(R.drawable.ic_status_online)
            }
            else if(item.lastOnline?.toDate()?.before(twentyFourHoursAgo) == true){
                status_icon.setImageResource(R.drawable.ic_status_away)
            }
            else{
                status_icon.setImageResource(R.drawable.ic_status_offline)
            }

            displayName.text = item.displayname
            userName.text = "@${item.username}"

            val db = FirebaseFirestore.getInstance()
            val bundle = Bundle()
            bundle.putSerializable("psychic", item)

            cvItem.setOnClickListener {
                findNavController(view = itemView).navigate(R.id.explore_psychics_expanded, bundle)
            }
        }
    }
}
