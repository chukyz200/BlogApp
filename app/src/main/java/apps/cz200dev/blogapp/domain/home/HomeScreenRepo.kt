package apps.cz200dev.blogapp.domain.home

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.Post

interface HomeScreenRepo {
    suspend fun getLatestPost(): Result<List<Post>>
    suspend fun registerLikeButtonState(postId: String, liked: Boolean)
}