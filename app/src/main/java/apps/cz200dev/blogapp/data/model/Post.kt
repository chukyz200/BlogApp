package apps.cz200dev.blogapp.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(

    @Exclude @JvmField
    var id: String = "",
    //Tag to obtain the time when is upload at null on firebase and map at Date
    @ServerTimestamp
    var created_at: Date? = null,
    val post_image: String = "",
    val post_description: String = "",
    val poster: Poster? = null,
    val likes: Long = 0,
    // Tags for cancel the request from firebase - only local
    @Exclude @JvmField
    var liked: Boolean = false,

    )

data class Poster(
    val username: String = "", val uid: String? = null, val profile_picture: String = "",
)