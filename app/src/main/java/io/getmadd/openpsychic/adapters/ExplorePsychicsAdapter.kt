import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.User

class ExplorePsychicsAdapter(val items: MutableList<User>, val listener: (Int) -> Unit): RecyclerView.Adapter<ExplorePsychicsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_explore_psychics_card, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: User, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val cvItem = findViewById<CardView>(R.id.fragment_explore_psychics_card)
            val displayName = findViewById<TextView>(R.id.displayNameTextView)
            val userName = findViewById<TextView>(R.id.usernameTextView)
            val backgroundImg = findViewById<ImageView>(R.id.explore_psychics_expanded_card_background_IV)

            displayName.text = item.displayname
            userName.text = "@"+item.username

            val bundle = Bundle().apply {
                putString("displayname", item.displayname)
                putString("username", item.username)
                putString("firstname", item.firstname)
                putString("lastname", item.lastname)
                putString("profileImgSrc", item.profileImgSrc)
                putString("backgroundImgSrc", item.displayImgSrc)
                putString("email", item.email)
                putString("bio", item.bio)
            }

            cvItem.setOnClickListener(){
                findNavController().navigate(R.id.action_explore_psychics_to_explore_psychics_expanded, bundle)
            }
        }

    }

}