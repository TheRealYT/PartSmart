package et.com.partsmart

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OrdersFragment : Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity?.getString(R.string.nav_orders)?.let { (context as HomeActivity).hide(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }
} 