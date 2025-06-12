package et.com.partsmart.models

data class CartItem(
    val id: String,
    val name: String,
    val image: String,
    val price: Double,
    var quantity: Int
)