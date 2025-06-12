package et.com.partsmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import et.com.partsmart.databinding.FragmentTopItemsBinding
import et.com.partsmart.models.CardItem
import et.com.partsmart.models.adapter.CardAdapter

class TopItemsFragment : Fragment() {
    private var _binding: FragmentTopItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopItemsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val horizontalRecycler = binding.horizontalList
        horizontalRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val cards = listOf(
            CardItem("Featured", "Top Pick", R.drawable.img_featured),
            CardItem("Trending", "Hot Now", R.drawable.img_trending),
            CardItem("Recommended", "For You", R.drawable.img_recommended),
            CardItem("New", "Just Dropped", R.drawable.img_new)
        )

        horizontalRecycler.adapter = CardAdapter(cards)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(horizontalRecycler)
    }
}