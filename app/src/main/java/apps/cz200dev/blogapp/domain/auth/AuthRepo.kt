package apps.cz200dev.blogapp.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    suspend fun signIn(email: String, password: String): FirebaseUser?

    suspend fun singUp(email: String, password: String, username: String): FirebaseUser?

    suspend fun updateProfile(imageBitmap: Bitmap, userName:String)

}