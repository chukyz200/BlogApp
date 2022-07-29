package apps.cz200dev.blogapp.ui.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.remote.auth.AuthDataSource
import apps.cz200dev.blogapp.databinding.FragmentSetUpProfileBinding
import apps.cz200dev.blogapp.domain.auth.AuthRepoImpl
import apps.cz200dev.blogapp.presentation.auth.AuthViewModel
import apps.cz200dev.blogapp.presentation.auth.AuthViewModelFactory


class SetUpProfileFragment : Fragment(R.layout.fragment_set_up_profile) {
    private lateinit var binding: FragmentSetUpProfileBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(AuthRepoImpl(AuthDataSource()))
    }
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.profileImage.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

    private var bitmap: Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetUpProfileBinding.bind(view)
        binding.profileImage.setOnClickListener {
            openActivityForResult()
        }
        binding.btnCreateProfile.setOnClickListener {
            val username = binding.etxtUsername.text.toString().trim()

            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Uploading photo...").create()
            bitmap?.let {
                if (username.isNotEmpty()) viewModel.updateUserProfile(it, username)
                    .observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                alertDialog.show()
                            }
                            is Result.Success -> {
                                alertDialog.dismiss()
                                findNavController().navigate(R.id.action_setUpProfileFragment_to_homeScreenFragment)
                            }
                            is Result.Failure -> {
                                alertDialog.dismiss()
                            }
                        }

                    }
            }
        }
    }

    private fun openActivityForResult() {
        startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }
}