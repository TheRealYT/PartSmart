package et.com.partsmart.models.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import et.com.partsmart.api.BASE_URL
import et.com.partsmart.databinding.ItemCartBinding
import et.com.partsmart.models.CartItem
import et.com.partsmart.storage.CartDBHelper

class CartAdapter(private val items: MutableList<CartItem>, private val onItemChanged: () -> Unit) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.productName.text = item.name
            binding.productPrice.text = "Price: $${item.price}"
            binding.productQuantity.text = item.quantity.toString()
            binding.productTotalPrice.text = "Total: $${item.price * item.quantity}"

            Glide.with(binding.root.context)
                .load("$BASE_URL${item.image}")
                .into(binding.productImage)

            val db = CartDBHelper(binding.root.context)

            binding.plusButton.setOnClickListener {
                item.quantity += 1
                binding.productQuantity.text = item.quantity.toString()
                binding.productTotalPrice.text = "Total: $${item.price * item.quantity}"
                db.addToCart(item)
                onItemChanged()
            }

            binding.minusButton.setOnClickListener {
                db.removeFromCart(item.id)
                if (item.quantity > 1) {
                    item.quantity -= 1
                    binding.productQuantity.text = item.quantity.toString()
                    binding.productTotalPrice.text = "Total: $${item.price * item.quantity}"
                } else {
                    // remove from list and notify adapter
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        items.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                onItemChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}