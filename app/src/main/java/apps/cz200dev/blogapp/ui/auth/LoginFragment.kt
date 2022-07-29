package apps.cz200dev.blogapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.core.hideKeyword
import apps.cz200dev.blogapp.data.remote.auth.AuthDataSource
import apps.cz200dev.blogapp.databinding.FragmentLoginBinding
import apps.cz200dev.blogapp.domain.auth.AuthRepoImpl
import apps.cz200dev.blogapp.presentation.auth.AuthViewModel
import apps.cz200dev.blogapp.presentation.auth.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            AuthRepoImpl(AuthDataSource())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLogged()
        doLogin()
        goToSingUpPage()
    }


    private fun isUserLogged() {
        firebaseAuth.currentUser?.let {
            if (it.displayName.toString().isNullOrEmpty()) {
                findNavController().navigate(R.id.action_loginFragment_to_setUpProfileFragment)
            } else {
                findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
            }
        }
    }

    private fun doLogin() {
        binding.btnSingIn.setOnClickListener {
            it.hideKeyword()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            validateCredentials(email, password)
            singIn(email, password)
        }
    }

    private fun goToSingUpPage() {
        binding.txtSingUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateCredentials(email: String, password: String) {

        if (email.isEmpty()) {
            binding.editTextEmail.error = "E-mail is empty"
            return
        }
//        if (!email.isValidEmail()) {
//            binding.editTextEmail.error = "E-mail is incorrect"
//            return
//        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is empty"
            return
        }
    }

    private fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun singIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSingIn.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data?.displayName.toString().isNullOrEmpty()) {
                        findNavController().navigate(R.id.action_loginFragment_to_setUpProfileFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
                    }
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSingIn.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }
    }

}