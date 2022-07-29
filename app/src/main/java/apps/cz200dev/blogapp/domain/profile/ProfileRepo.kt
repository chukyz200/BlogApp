package apps.cz200dev.blogapp.domain.profile

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.User

interface ProfileRepo {

    suspend fun getProfileData(): Result<User>

}