package apps.cz200dev.blogapp.domain.camera

import android.graphics.Bitmap
import apps.cz200dev.blogapp.data.remote.auth.AuthDataSource
import apps.cz200dev.blogapp.data.remote.camera.CameraDataSource

class CameraRepoImpl(private val dataSource: CameraDataSource) : CameraRepo {
    override suspend fun uploadPhoto(imageBitmap: Bitmap, description: String) =
        dataSource.uploadPhoto(imageBitmap, description)
}