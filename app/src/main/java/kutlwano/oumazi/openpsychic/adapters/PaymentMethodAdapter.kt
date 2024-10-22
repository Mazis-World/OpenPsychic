package kutlwano.oumazi.openpsychic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kutlwano.oumazi.openpsychic.R
import kutlwano.oumazi.openpsychic.model.PaymentMethod

class PaymentMethodAdapter(val items: ArrayList<PaymentMethod>, val listener: (Int) -> Unit) :
    RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position, listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var provider: TextView = itemView.findViewById(R.id.provideredittext)
        var address: TextView = itemView.findViewById(R.id.addressedittext)

        fun bind(method: PaymentMethod, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            provider.text = method.provider
            address.text = method.address

        }
    }
}