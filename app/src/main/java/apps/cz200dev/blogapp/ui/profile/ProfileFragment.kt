package apps.cz200dev.blogapp.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.core.hide
import apps.cz200dev.blogapp.core.show
import apps.cz200dev.blogapp.data.remote.profile.ProfileDataSource
import apps.cz200dev.blogapp.databinding.FragmentProfileBinding
import apps.cz200dev.blogapp.domain.profile.ProfileRepoImpl
import apps.cz200dev.blogapp.presentation.profile.ProfileViewModel
import apps.cz200dev.blogapp.presentation.profile.ProfileViewModelFactory
import com.bumptech.glide.Glide

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel> {
        ProfileViewModelFactory(ProfileRepoImpl(ProfileDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        setProfileData()
    }

    private fun setProfileData() {
        viewModel.getProfileData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    binding.profileImage.show()
                    binding.txtProfileName.show()

                    binding.txtProfileName.text = result.data.username
                    Glide.with(requireContext()).load(result.data.photo_url).centerCrop()
                        .into(binding.profileImage)

                    Log.d("Data", "${result.data.username}, ${result.data.photo_url}")
                }
                is Result.Failure -> {
                    binding.progressBar.hide()
                    Toast.makeText(requireContext(), "An error ocurred try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}