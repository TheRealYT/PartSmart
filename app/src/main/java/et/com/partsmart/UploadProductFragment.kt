package et.com.partsmart

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import et.com.partsmart.databinding.FragmentUploadProductBinding
import et.com.partsmart.view_models.ProductViewModel

class UploadProductFragment : DialogFragment() {

    private var _binding: FragmentUploadProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            selectedImageUri = it
            binding.previewImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.uploadButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            val price = binding.priceInput.text.toString().trim()
            val category = binding.categoryInput.text.toString().trim()
            val condition = binding.conditionInput.text.toString().trim()
            val sellerId = (activity as HomeActivity).user.id

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() ||
                category.isEmpty() || condition.isEmpty() || sellerId.isEmpty() || selectedImageUri == null
            ) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewModel.uploadProduct(
                name,
                description,
                price,
                category,
                condition,
                sellerId,
                selectedImageUri!!
            )
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let { msg -> Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show() }
        }

        viewModel.productId.observe(viewLifecycleOwner) {
            it?.let { id ->
                Toast.makeText(
                    requireContext(),
                    "Uploaded successfully! ID: $id",
                    Toast.LENGTH_LONG
                ).show()
                dismiss()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.uploadButton.isEnabled = !isLoading
        }
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
            UploadProductFragment().show(manager, "CartDialogFragment")
        }
    }
}