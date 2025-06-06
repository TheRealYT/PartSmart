package et.com.partsmart

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import et.com.partsmart.databinding.FragmentRegisterBinding
import et.com.partsmart.view_models.AuthViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireActivity() as AuthActivity).binding.authFragmentContainer.id, LoginFragment())
                .commit()
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                binding.nameInput.error = getString(R.string.invalid_name)
                isValid = false
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = getString(R.string.invalid_email)
                isValid = false
            }

            if (password.length < 6) {
                binding.passwordInput.error = getString(R.string.password_min_6)
                isValid = false
            }

            if (isValid) {
                viewModel.register(name, email, password)
            }
        }

        viewModel.isRegistered.observe(viewLifecycleOwner) { success ->
            if (success) {
                val snackBar = Snackbar.make(requireView(),
                    getString(R.string.registered_successfully), Snackbar.LENGTH_LONG)
                snackBar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                snackBar.show()

                binding.switchToLogin.performClick()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                val snackBar = Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                snackBar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                snackBar.show()
                viewModel.clearError()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.registerButton.isEnabled = !loading
            binding.nameInput.isEnabled = !loading
            binding.emailInput.isEnabled = !loading
            binding.passwordInput.isEnabled = !loading
            binding.switchToLogin.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}