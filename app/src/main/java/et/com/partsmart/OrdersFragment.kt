package et.com.partsmart

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import et.com.partsmart.databinding.FragmentOrdersBinding
import et.com.partsmart.models.adapter.CartAdapter
import et.com.partsmart.models.adapter.OrderAdapter
import et.com.partsmart.storage.CartDBHelper

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: CartDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        activity?.getString(R.string.nav_orders)?.let { (context as HomeActivity).hide(it) }
        db = CartDBHelper(requireContext())
        setupOrderList()
        return binding.root
    }

    private fun setupOrderList() {
        val checkouts = db.getCheckouts()
        val adapter = OrderAdapter(requireContext(), checkouts) { orderId ->
            showOrderDetailDialog(orderId)
        }
        binding.orderRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.orderRecycler.adapter = adapter
    }

    private fun showOrderDetailDialog(orderId: Long) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_order_detail)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setWindowAnimations(android.R.style.Animation_Dialog)
            decorView.setPadding(0, 0, 0, 0)
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            attributes = params
        }

        val recycler = dialog.findViewById<RecyclerView>(R.id.recycler)
        val totalText = dialog.findViewById<TextView>(R.id.totalText)
        val closeBtn = dialog.findViewById<ImageButton>(R.id.closeBtn)

        val db = CartDBHelper(requireContext())
        val products = db.getCardItemsForCheckout(orderId)
        recycler.adapter =
            CartAdapter(requireContext(), products.toMutableList(), true) {}
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val total = db.getCheckoutTotal(orderId.toInt())
        totalText.text = requireContext().getString(R.string.total_etb, total.toString())

        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}