import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Request


class HistoryFragmentAdapter(val items: MutableList<Request>, val listener: (Int) -> Unit) :
    RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.history_fragment_recycler_view_item, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var specificquestiontextview: TextView = itemView.findViewById(R.id.specificquestiontextview)
        var timestamptextview: TextView = itemView.findViewById(R.id.timestampTextView)
        var requestitemcardview: CardView = itemView.findViewById(R.id.requestitemcardview)

        fun bind(request: Request, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            specificquestiontextview.text = request.specificQuestion
            timestamptextview.text = request.timestamp.toDate().toString()

            val bundle = Bundle()
            bundle.putSerializable("request", request)

            requestitemcardview.setOnClickListener{
                Navigation.findNavController(view = itemView)
                    .navigate(R.id.action_history_fragment_to_request_history_fragment, bundle)
            }
        }

    }

}