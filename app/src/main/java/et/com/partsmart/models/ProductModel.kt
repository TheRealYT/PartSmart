package et.com.partsmart.models

import et.com.partsmart.api.BaseResponse

data class CardItem(val title: String, val subtitle: String, val resId: Int)

data class AddProductResponse(
    val productId: String
) : BaseResponse(true, "")


data class UserProductsResponse(
    val success: Boolean,
    val products: List<Product>
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val image: String,
    val category: String,
    val created_at: String
)
