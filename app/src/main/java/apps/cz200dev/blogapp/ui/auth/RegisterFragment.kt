package apps.cz200dev.blogapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.remote.auth.AuthDataSource
import apps.cz200dev.blogapp.databinding.FragmentRegisterBinding
import apps.cz200dev.blogapp.domain.auth.AuthRepoImpl
import apps.cz200dev.blogapp.presentation.auth.AuthViewModel
import apps.cz200dev.blogapp.presentation.auth.AuthViewModelFactory


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            AuthRepoImpl(AuthDataSource())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        singUp()
    }

    private fun singUp() {
        binding.btnSingUp.setOnClickListener {
            val userName = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            validateUserData(userName, email, password, confirmPassword)
            createUser(email, password, userName)
        }
    }

    private fun createUser(email: String, password: String, userName: String) {
        viewModel.signUp(email, password, userName).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSingUp.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerFragment_to_setUpProfileFragment)
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSingUp.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun validateUserData(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (userName.isEmpty()) {
            binding.editTextUsername.error = "Username is empty"
            return
        }
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Email is empty"
            return
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is empty"
            return
        }
        if (confirmPassword.isEmpty()) {
            binding.editTextConfirmPassword.error = "Confirm Password Username is empty"
            return
        }
        if (password != confirmPassword) {
            binding.editTextConfirmPassword.error = "Password does not match"
            binding.editTextPassword.error = "Password does not match"
            return
        }
    }
}