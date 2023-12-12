import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Psychic

class ExplorePsychicsAdapter(val items: MutableList<Psychic>, val listener: (Int) -> Unit): RecyclerView.Adapter<ExplorePsychicsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_explore_psychics_card, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Psychic, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val cvItem = findViewById<CardView>(R.id.explore_psychics_card)

        }


    }

}