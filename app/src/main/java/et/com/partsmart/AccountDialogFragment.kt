package et.com.partsmart

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import et.com.partsmart.databinding.FragmentAccountBinding
import et.com.partsmart.view_models.AuthViewModel

class AccountDialogFragment : DialogFragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = viewModel.getUser()

        val usernameInput = binding.editUsername
        val emailInput = binding.editEmail
        val saveButton = binding.saveButton

        user?.let {
            usernameInput.setText(it.username)
            emailInput.setText(it.email)
        }

        saveButton.setOnClickListener {
            val newUsername = usernameInput.text.toString()
            val newEmail = emailInput.text.toString()

            if (user != null) {
                viewModel.updateUser(newUsername, newEmail)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it != null) Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) {
            if (it == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_updated), Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            saveButton.isEnabled = !it
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

    companion object {
        fun show(manager: FragmentManager) {
            AccountDialogFragment().show(manager, "AccountDialogFragment")
        }
    }
} 