import android.media.Rating
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Psychic

class ExplorePsychicsAdapter(
    private val items: MutableList<Psychic>,
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
        abstract fun bind(item: Psychic, pos: Int, listener: (Int) -> Unit)
    }

    class AdViewHolder(itemView: View) : ViewHolder(itemView) {
        private val adView: AdView = itemView.findViewById(R.id.explore_psychics_ad_view)

        override fun bind(item: Psychic, pos: Int, listener: (Int) -> Unit) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    class PsychicViewHolder(itemView: View) : ViewHolder(itemView) {
        private val cvItem: CardView = itemView.findViewById(R.id.fragment_explore_psychics_card)
        private val displayName: TextView = itemView.findViewById(R.id.displayNameTextView)
        private val userName: TextView = itemView.findViewById(R.id.usernameTextView)
        private val starRating: RatingBar = itemView.findViewById(R.id.explorepsychicsratingBar)
        private val backgroundImg: ImageView =
            itemView.findViewById(R.id.explore_psychics_expanded_card_background_IV)

        override fun bind(item: Psychic, pos: Int, listener: (Int) -> Unit) {
            Log.e("ExplorePsychicsAdapter", item.displayimgsrc!!)
            if(item.displayimgsrc != " "){
                Glide.with(itemView).load(item.displayimgsrc).into(backgroundImg)
            }else{
                Glide.with(itemView).load(R.drawable.openpsychiclogo).into(backgroundImg)
            }

            displayName.text = item.displayname
            userName.text = "@${item.username}"

            val bundle = Bundle()
            bundle.putSerializable("psychic", item)

            cvItem.setOnClickListener {
                findNavController(view = itemView).navigate(R.id.action_explore_psychics_to_explore_psychics_expanded, bundle)
            }

            if(item.psychicrating != null){
                starRating.rating = item.psychicrating!!
            }
            else starRating.rating = 0F
        }
    }
}
