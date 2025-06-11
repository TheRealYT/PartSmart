package et.com.partsmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import et.com.partsmart.databinding.FragmentMyProductsBinding
import et.com.partsmart.models.adapter.ProductAdapter
import et.com.partsmart.view_models.MyProductsViewModel

class MyProductsFragment : Fragment() {

    private var _binding: FragmentMyProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activity?.getString(R.string.nav_my_products)?.let { (context as HomeActivity).hide(it) }
        _binding = FragmentMyProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userId = (activity as HomeActivity).user.id

        val adapter = ProductAdapter(emptyList())

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.products.observe(viewLifecycleOwner) {
            val productList = it
            adapter.apply {
                this.products = productList
                notifyDataSetChanged()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.fetchUserProducts(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
