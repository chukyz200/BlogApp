package apps.cz200dev.blogapp.domain.camera

import android.graphics.Bitmap

interface CameraRepo {

    suspend fun uploadPhoto(imageBitmap: Bitmap, description: String)

}