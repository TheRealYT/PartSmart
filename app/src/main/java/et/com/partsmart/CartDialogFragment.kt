package et.com.partsmart

import android.app.Dialog
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

        val adapter = CartAdapter(cartItems) {
            total = db.getTotalCost()
            binding.totalPrice.text = "Total: $$total"
        }

        binding.cartList.adapter = adapter
        binding.cartList.layoutManager =
            LinearLayoutManager(requireContext())

        binding.totalPrice.text = "Total: $$total"
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
} 