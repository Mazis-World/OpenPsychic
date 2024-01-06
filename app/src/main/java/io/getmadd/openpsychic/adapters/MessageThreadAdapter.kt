import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Message

class MessageThreadAdapter(private val messageList: List<Message>, val listener: (Int) -> Unit) :
    RecyclerView.Adapter<MessageThreadAdapter.MessageViewHolder>() {
    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2
    private val userid = Firebase.auth.uid


    override fun getItemViewType(position: Int): Int {
        // Determine the view type based on the sender or receiver

        return if (messageList[position].senderid == userid) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_SENDER) R.layout.message_thread_receiver_item else R.layout.message_thread_sender_item
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.bind(messageList[position], position, listener)

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        val timestamp: TextView = itemView.findViewById(R.id.textViewReceivedTimestamp)

        fun bind(content: Message, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            message.text = content.message
            timestamp.text = content.timestamp.toDate().toString()
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}

