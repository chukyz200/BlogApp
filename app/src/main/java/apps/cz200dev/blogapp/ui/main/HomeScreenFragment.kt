package apps.cz200dev.blogapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.core.hide
import apps.cz200dev.blogapp.core.show
import apps.cz200dev.blogapp.data.model.Post
import apps.cz200dev.blogapp.data.remote.home.HomeScreenDataSource
import apps.cz200dev.blogapp.databinding.FragmentHomeScreenBinding
import apps.cz200dev.blogapp.domain.home.HomeScreenRepoImpl
import apps.cz200dev.blogapp.presentation.main.HomeScreenViewModel
import apps.cz200dev.blogapp.presentation.main.HomeScreenViewModelFactory
import apps.cz200dev.blogapp.ui.main.adapter.HomeScreenAdapter
import apps.cz200dev.blogapp.ui.main.adapter.OnPostClickListener

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen), OnPostClickListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(HomeScreenRepoImpl(HomeScreenDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)
        viewModel.fetchLatestPost().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        binding.emptyContainer.show()
                        return@observe
                    } else {
                        binding.emptyContainer.hide()
                    }
                    binding.rvHome.adapter = HomeScreenAdapter(result.data, this)
                }
                is Result.Failure -> {
                    binding.progressBar.hide()
                    Toast.makeText(
                        requireContext(),
                        "Ocurrió un error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }


    }

    override fun onLikeButtonClick(post: Post, liked: Boolean) {

        viewModel.registerButtonState(post.id, liked).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {}
                is Result.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Ocurrió un error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}