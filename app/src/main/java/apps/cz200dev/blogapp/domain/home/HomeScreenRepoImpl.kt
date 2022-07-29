package apps.cz200dev.blogapp.domain.home

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.Post
import apps.cz200dev.blogapp.data.remote.home.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {
    override suspend fun getLatestPost(): Result<List<Post>> = dataSource.getLatestPost()
    override suspend fun registerLikeButtonState(postId: String, liked: Boolean) = dataSource.registerLikeButtonState(postId, liked)
}