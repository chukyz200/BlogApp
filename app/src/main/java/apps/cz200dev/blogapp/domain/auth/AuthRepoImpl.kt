package apps.cz200dev.blogapp.domain.auth

import android.graphics.Bitmap
import apps.cz200dev.blogapp.data.remote.auth.AuthDataSource
import com.google.firebase.auth.FirebaseUser

class AuthRepoImpl(private val dataSource: AuthDataSource) : AuthRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? =
        dataSource.signIn(email, password)

    override suspend fun singUp(email: String, password: String, username: String): FirebaseUser? =
        dataSource.singUp(email, password, username)

    override suspend fun updateProfile(imageBitmap: Bitmap, userName: String) =
        dataSource.updateUserProfile(imageBitmap, userName)


}