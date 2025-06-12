package et.com.partsmart.models.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import et.com.partsmart.R
import et.com.partsmart.api.BASE_URL
import et.com.partsmart.models.Product

class ProductAdapter(
    private val context: Context,
    var products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.productName)
        private val priceText: TextView = itemView.findViewById(R.id.productPrice)
        private val categoryText: TextView = itemView.findViewById(R.id.productCategory)
        private val imageView: ImageView = itemView.findViewById(R.id.productImage)

        fun bind(product: Product) {
            nameText.text = product.name
            priceText.text = context.getString(R.string.etb, product.price)
            categoryText.text = product.category

            Glide.with(itemView.context)
                .load("${BASE_URL}${product.image}")
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}