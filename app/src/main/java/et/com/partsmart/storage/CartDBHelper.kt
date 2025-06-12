package et.com.partsmart.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import et.com.partsmart.models.CartItem

private const val CART = "cart"
private const val CHECKOUT = "checkout"
private const val CHECKOUT_ITEMS = "checkout_items"

class CartDBHelper(context: Context) : SQLiteOpenHelper(context, "cart.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $CART (
                id TEXT PRIMARY KEY,
                name TEXT,
                image TEXT,
                price REAL,
                quantity INTEGER
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $CHECKOUT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT,
                total REAL
            )
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $CHECKOUT_ITEMS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                checkout_id INTEGER,
                product_id INTEGER,
                name TEXT,
                image TEXT,
                price REAL,
                quantity INTEGER
            )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $CART")
        db.execSQL("DROP TABLE IF EXISTS $CHECKOUT")
        db.execSQL("DROP TABLE IF EXISTS $CHECKOUT_ITEMS")
        onCreate(db)
    }

    fun addToCart(item: CartItem) {
        val db = writableDatabase

        val cursor = db.rawQuery("SELECT quantity FROM $CART WHERE id = ?", arrayOf(item.id))
        if (cursor.moveToFirst()) {
            val currentQty = cursor.getInt(0)
            val newQty = currentQty + 1
            db.execSQL("UPDATE $CART SET quantity = ? WHERE id = ?", arrayOf(newQty, item.id))
        } else {
            val values = ContentValues().apply {
                put("id", item.id)
                put("name", item.name)
                put("image", item.image)
                put("price", item.price)
                put("quantity", 1)
            }
            db.insert(CART, null, values)
        }
        cursor.close()
    }

    fun removeFromCart(productId: String) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT quantity FROM $CART WHERE id = ?", arrayOf(productId))
        if (cursor.moveToFirst()) {
            val qty = cursor.getInt(0)
            if (qty > 1) {
                val values = ContentValues().apply { put("quantity", qty - 1) }
                db.update(CART, values, "id = ?", arrayOf(productId))
            } else {
                db.delete(CART, "id = ?", arrayOf(productId))
            }
        }
        cursor.close()
    }

    fun getAllCart(): List<CartItem> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $CART", null)
        val items = mutableListOf<CartItem>()

        while (cursor.moveToNext()) {
            items.add(
                CartItem(
                    id = cursor.getString(0),
                    name = cursor.getString(1),
                    image = cursor.getString(2),
                    price = cursor.getDouble(3),
                    quantity = cursor.getInt(4)
                )
            )
        }
        cursor.close()
        return items
    }

    fun getCartCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(quantity) FROM $CART", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getTotalCost(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(price * quantity) FROM $CART", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun clearCart() {
        writableDatabase.execSQL("DELETE FROM $CART")
    }

    fun checkout(): Long {
        val db = writableDatabase
        val cartItems = getAllCart()
        if (cartItems.isEmpty()) return -1

        val total = cartItems.sumOf { it.price * it.quantity }
        val values = ContentValues().apply {
            put("timestamp", System.currentTimeMillis().toString())
            put("total", total)
        }
        val checkoutId = db.insert("checkout", null, values)

        cartItems.forEach {
            val itemValues = ContentValues().apply {
                put("checkout_id", checkoutId)
                put("product_id", it.id)
                put("name", it.name)
                put("image", it.image)
                put("price", it.price)
                put("quantity", it.quantity)
            }
            db.insert("checkout_items", null, itemValues)
        }

        clearCart()
        return checkoutId
    }

    fun getCheckouts(): List<Pair<Long, Double>> {
        val result = mutableListOf<Pair<Long, Double>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, total FROM checkout ORDER BY id DESC", null)
        while (cursor.moveToNext()) {
            result.add(cursor.getLong(0) to cursor.getDouble(1))
        }
        cursor.close()
        return result
    }

    fun getCheckoutTotal(id: Int): Double {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT total FROM checkout WHERE id = ?", arrayOf(id.toString()))
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun getCardItemsForCheckout(checkoutId: Long): List<CartItem> {
        val db = readableDatabase
        val items = mutableListOf<CartItem>()
        val cursor = db.rawQuery(
            """
        SELECT id, name, image, price, quantity
        FROM checkout_items WHERE checkout_id = ?
          """.trimIndent(), arrayOf(checkoutId.toString())
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            items.add(CartItem(id.toString(), name, image, price, quantity))
        }
        cursor.close()
        return items
    }
}