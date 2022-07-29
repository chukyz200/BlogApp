package apps.cz200dev.blogapp.ui.camera

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.core.hideKeyword
import apps.cz200dev.blogapp.data.remote.camera.CameraDataSource
import apps.cz200dev.blogapp.databinding.FragmentCameraBinding
import apps.cz200dev.blogapp.domain.camera.CameraRepoImpl
import apps.cz200dev.blogapp.presentation.camera.CameraViewModel
import apps.cz200dev.blogapp.presentation.camera.CameraViewModelFactory


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.imgAddPhoto.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }
    private var bitmap: Bitmap? = null

    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(CameraRepoImpl(CameraDataSource()))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        try {
            openActivityForResult()
        } catch (e: ActivityNotFoundException) {

        }
        binding.btnUploadPhoto.setOnClickListener {
            it.hideKeyword()
            bitmap?.let {
                viewModel.uploadPhoto(it, binding.etxPhotoDescription.text.toString().trim())
                    .observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Uploading photo...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Success -> {
                                findNavController().popBackStack()
                            }
                            is Result.Failure -> {
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
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//
//        }
    }

    private fun openActivityForResult() {
        startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            binding.imgAddPhoto.setImageBitmap(imageBitmap)
//        }
//    }
}