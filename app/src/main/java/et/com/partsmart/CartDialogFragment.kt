package et.com.partsmart

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import et.com.partsmart.databinding.FragmentCartBinding
import et.com.partsmart.models.adapter.CartAdapter
import et.com.partsmart.storage.CartDBHelper

class CartDialogFragment : DialogFragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = CartDBHelper(requireContext())
        val cartItems = db.getAllCart().toMutableList()
        var total = db.getTotalCost()

        val adapter = CartAdapter(requireContext(), cartItems) {
            total = db.getTotalCost()
            binding.totalPrice.text = getString(R.string.total_etb, total.toString())
        }

        binding.cartList.adapter = adapter
        binding.cartList.layoutManager =
            LinearLayoutManager(requireContext())

        binding.totalPrice.text = getString(R.string.total_etb, total.toString())
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setWindowAnimations(android.R.style.Animation_Dialog)
            decorView.setPadding(0, 0, 0, 0)
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            attributes = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun show(manager: FragmentManager) {
            CartDialogFragment().show(manager, "CartDialogFragment")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        (activity as? HomeActivity)?.updateCartCount()
    }
} 