import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.getmadd.openpsychic.R
import io.getmadd.openpsychic.model.Message
import io.getmadd.openpsychic.model.MessageMetaData

class MessageThreadAdapter(private val messageList: ArrayList<Message>, val data:MessageMetaData ,val listener: (Int) -> Unit) :
    RecyclerView.Adapter<MessageThreadAdapter.MessageViewHolder>() {
    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2
    private val userid = Firebase.auth.uid


    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderid == userid)  VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_RECEIVER)  R.layout.message_thread_sender_item else R.layout.message_thread_receiver_item
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view,data)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int): Unit =
        holder.bind(messageList[position], position, listener)

    class MessageViewHolder(itemView: View, data: MessageMetaData) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        val timestamp: TextView = itemView.findViewById(R.id.textViewReceivedTimestamp)
        val image: ImageView = itemView.findViewById(R.id.imageViewReceivedBubble)
        private val userid = Firebase.auth.uid
        val data = data
        fun bind(content: Message, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            message.text = content.message
            timestamp.text = content.timestamp.toDate().toString()
            Glide.with(this).load(R.drawable.openpsychiclogo).apply(RequestOptions.circleCropTransform()).into(image)

            if(data.receiverid != null) {
                if (data.receiverid == content.senderid) {
                    Glide.with(this).load(data.psychicprofileimgsrc)
                        .apply(RequestOptions.circleCropTransform()).into(image)
                } else {
                    Glide.with(this).load(data.userprofileimgsrc)
                        .apply(RequestOptions.circleCropTransform()).into(image)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}

