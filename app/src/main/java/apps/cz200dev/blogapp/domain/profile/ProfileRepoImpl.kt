package apps.cz200dev.blogapp.domain.profile

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.User
import apps.cz200dev.blogapp.data.remote.profile.ProfileDataSource

class ProfileRepoImpl(private val dataSource: ProfileDataSource) : ProfileRepo {
    override suspend fun getProfileData(): Result<User> = dataSource.getProfileData()

}