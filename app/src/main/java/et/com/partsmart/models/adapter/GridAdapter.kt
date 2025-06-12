package et.com.partsmart.models.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import et.com.partsmart.R
import et.com.partsmart.api.BASE_URL
import et.com.partsmart.databinding.ItemGridBinding
import et.com.partsmart.models.CartItem
import et.com.partsmart.models.Product
import et.com.partsmart.storage.CartDBHelper

class GridAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onCartChanged: () -> Unit
) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    private var fullList = items.toMutableList()

    inner class GridViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.productName.text = item.name
            binding.productPrice.text = context.getString(R.string.etb, item.price)

            Glide.with(itemView.context)
                .load("$BASE_URL${item.image}")
                .into(binding.productImage)

            binding.addToCartButton.setOnClickListener {
                val db = CartDBHelper(context)
                db.addToCart(CartItem(item.id, item.name, item.image, item.price.toDouble(), 1))
                onCartChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateData(newList: List<Product>) {
        items = newList
        fullList = items.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}