import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.MessageMetaData


class MessagesAdapter(val items: MutableList<MessageMetaData>, val listener: (Int) -> Unit) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.history_fragment_recycler_view_item, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messagetotextview: TextView = itemView.findViewById(R.id.titletextview)
        var timestamptextview: TextView = itemView.findViewById(R.id.timestampTextView)
        var requestitemcardview: CardView = itemView.findViewById(R.id.requestitemcardview)
        var historytypetextview: TextView = itemView.findViewById(R.id.historytypetextview)

        fun bind(messagemetadata: MessageMetaData, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            historytypetextview.text = "Message"
            val bundle = Bundle()
            bundle.putSerializable("messagemetadata", messagemetadata)

            if(messagemetadata.senderid == Firebase.auth.uid){
                messagetotextview.text = messagemetadata.psychicdisplayname
            }else{
                messagetotextview.text = "@"+messagemetadata.username
            }

            timestamptextview.visibility = View.GONE

            requestitemcardview.setOnClickListener{
                Navigation.findNavController(view = itemView)
                    .navigate(R.id.action_messages_fragment_to_message_thread_fragment, bundle)
            }
        }

    }

}