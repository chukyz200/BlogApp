package apps.cz200dev.blogapp.data.remote.profile

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.User
import com.google.firebase.auth.FirebaseAuth

class ProfileDataSource {

    suspend fun getProfileData(): Result<User> {
        val user = FirebaseAuth.getInstance().currentUser
        return Result.Success(
            User(
                username = user?.displayName.toString(),
                photo_url = user?.photoUrl.toString()
            )
        )
    }
}