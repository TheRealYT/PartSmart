package et.com.partsmart.models.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import et.com.partsmart.api.BASE_URL
import et.com.partsmart.databinding.ItemGridBinding
import et.com.partsmart.models.Product

class GridAdapter(private var items: List<Product>) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.productName.text = item.name
            binding.productPrice.text = "$${item.price}"
            Glide.with(itemView.context)
                .load("$BASE_URL${item.image}")
                .into(binding.productImage)

            binding.addToCartButton.setOnClickListener {
                // TODO: handle add to cart logic
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
        notifyDataSetChanged()
    }
}