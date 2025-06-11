package et.com.partsmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import et.com.partsmart.databinding.FragmentItemsBinding
import et.com.partsmart.models.GridItem

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gridRecycler = binding.gridList
        gridRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        val gridItems = listOf(
            GridItem("Item A"),
            GridItem("Item B"),
            GridItem("Item C"),
            GridItem("Item D"),
            GridItem("Item E"),
            GridItem("Item F")
        )

        gridRecycler.adapter = GridAdapter(gridItems)
    }
}