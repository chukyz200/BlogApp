package apps.cz200dev.blogapp.data.remote.home

import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeScreenDataSource {
    suspend fun getLatestPost(): Result<List<Post>> {
        val postList = mutableListOf<Post>()

        withContext(Dispatchers.IO) {
            val queryString = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING).get().await()
            for (post in queryString.documents) {
                post.toObject(Post::class.java)?.let {
                    //Check the post liked obtains the user and iterate the postLikes
                    val isLiked = FirebaseAuth.getInstance().currentUser?.let { safeUser ->
                        isPostLiked(post.id, safeUser.uid)
                    }
                    //Method to begin a time whe is right now uploaded the post
                    it.apply {
                        created_at = post.getTimestamp(
                            "created_at",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()
                        id = post.id
                        if (isLiked != null) {
                            liked = isLiked
                        }
                    }
                    postList.add(it)
                }
            }
        }
        return Result.Success(postList)
    }

    private suspend fun isPostLiked(postId: String, uid: String): Boolean {
        val post =
            FirebaseFirestore.getInstance().collection("postsLikes").document(postId).get().await()
        if (!post.exists()) return false
        val likeArray: List<String> = post.get("likes") as List<String>
        return likeArray.contains(uid)
    }

    fun registerLikeButtonState(postId: String, liked: Boolean) {
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        val postLikeRef = FirebaseFirestore.getInstance().collection("postsLikes").document(postId)

        val database = FirebaseFirestore.getInstance()

        database.runTransaction { transaction ->

            val snapshot = transaction.get(postRef)
            val likeCount = snapshot.getLong("likes")

            if (likeCount != null) {
                if (likeCount >= 0) {
                    if (liked) {
                        if (transaction.get(postLikeRef).exists()) {
                            transaction.update(postLikeRef, "likes", FieldValue.arrayUnion(uid))
                        } else {
                            transaction.set(
                                postLikeRef,
                                hashMapOf("likes" to arrayListOf(uid)),
                                SetOptions.merge()
                            )
                        }
                        transaction.update(postRef, "likes", increment)
                    } else {
                        transaction.update(postRef, "likes", decrement)
                        transaction.update(postLikeRef, "likes", FieldValue.arrayRemove(uid))
                    }
                }
            }

        }.addOnFailureListener { throw Exception(it.message) }
    }
}


//-------------------------------Flow------------------------------------
//suspend fun getLatestPost(): Flow<Result<List<Post>>> = callbackFlow {
//    val postList = mutableListOf<Post>()
//
//    var postReference: Query? = null
//
//    try {
//        postReference = FirebaseFirestore.getInstance().collection("posts")
//            .orderBy("created_at", Query.Direction.DESCENDING)
//    } catch (e: Throwable) {
//        close(e)
//    }
//
//    val subscription = postReference?.addSnapshotListener { value, error ->
//        if (value == null) return@addSnapshotListener
//        try {
//            //Clear list to disable doublelist
//            postList.clear()
//            for (post in value.documents) {
//                post.toObject(Post::class.java)?.let {
//                    //Method to begin a time whe is right now uploaded the post
//                    it.apply {
//                        created_at = post.getTimestamp(
//                            "created_at",
//                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
//                        )?.toDate()
//                    }
//                    postList.add(it)
//                }
//            }
//            //Replace offer by deprecated
//            trySend(Result.Success(postList)).isSuccess
//        } catch (e: Exception) {
//            close(e)
//        }
//
//    }
//    awaitClose { subscription?.remove() }
//}