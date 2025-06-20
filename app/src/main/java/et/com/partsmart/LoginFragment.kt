package et.com.partsmart

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import et.com.partsmart.databinding.FragmentLoginBinding
import et.com.partsmart.view_models.AuthViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireActivity() as AuthActivity).binding.authFragmentContainer.id, RegisterFragment())
                .commit()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = getString(R.string.invalid_email)
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordInput.error = getString(R.string.password_min_6)
                return@setOnClickListener
            }

            // call the API
            viewModel.login(email, password)
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner) { success ->
            if (success) {
                val snackBar = Snackbar.make(requireView(),
                    getString(R.string.login_successful), Snackbar.LENGTH_LONG)
                snackBar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                snackBar.show()

                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
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
            binding.loginButton.isEnabled = !loading
            binding.emailInput.isEnabled = !loading
            binding.passwordInput.isEnabled = !loading
            binding.switchToRegister.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}