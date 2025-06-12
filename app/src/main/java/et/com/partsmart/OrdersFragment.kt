package et.com.partsmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import et.com.partsmart.databinding.FragmentOrdersBinding
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
        val adapter = OrderAdapter(checkouts)
        binding.orderRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.orderRecycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}