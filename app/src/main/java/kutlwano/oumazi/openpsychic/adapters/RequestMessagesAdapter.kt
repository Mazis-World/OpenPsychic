import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.model.Message


class RequestMessagesAdapter(val items: MutableList<Message>, val listener: (Int) -> Unit) :
    RecyclerView.Adapter<RequestMessagesAdapter.ViewHolder>() {
    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2
    private val userid = Firebase.auth.uid

    override fun getItemViewType(position: Int): Int {
        return if (items[position].senderid == userid)  VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_RECEIVER)  R.layout.message_thread_sender_item else R.layout.message_thread_receiver_item
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messagetv: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        val timestamptv: TextView = itemView.findViewById(R.id.textViewReceivedTimestamp)
        val image: ImageView = itemView.findViewById(R.id.imageViewReceivedBubble)

        fun bind(message: Message, pos: Int, listener: (Int) -> Unit) = with(itemView) {

            messagetv.text = "${message.message}"
            timestamptv.text = message.timestamp.toDate().toString()

            if(1 == 1){
                Glide.with(this).load(R.drawable.openpsychiclogo)
                    .apply(RequestOptions.circleCropTransform()).into(image)
                timestamptv.setTextColor(resources.getColor(R.color.op_purple_foreground_text))
            }

        }

    }

}