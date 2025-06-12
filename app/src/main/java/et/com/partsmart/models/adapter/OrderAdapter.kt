package et.com.partsmart.models.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import et.com.partsmart.R
import et.com.partsmart.databinding.ItemOrderBinding

class OrderAdapter(private val context: Context, private val items: List<Pair<Long, Double>>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Pair<Long, Double>) {
            binding.orderId.text = context.getString(R.string.order, order.first.toString())
            binding.totalPrice.text = context.getString(R.string.total_etb, order.second.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }
}