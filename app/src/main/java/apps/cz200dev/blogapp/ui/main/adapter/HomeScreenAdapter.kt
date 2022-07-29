package apps.cz200dev.blogapp.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import apps.cz200dev.blogapp.R
import apps.cz200dev.blogapp.core.BaseViewHolder
import apps.cz200dev.blogapp.core.TimeUtils
import apps.cz200dev.blogapp.core.hide
import apps.cz200dev.blogapp.core.show
import apps.cz200dev.blogapp.data.model.Post
import apps.cz200dev.blogapp.databinding.PostItemViewBinding
import com.bumptech.glide.Glide

class HomeScreenAdapter(
    private val postList: List<Post>,
    private val onPostClickListener: OnPostClickListener
) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var postClickListener: OnPostClickListener? = null

    init {
        postClickListener = onPostClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    override fun getItemCount(): Int = postList.size

    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
    ) : BaseViewHolder<Post>(binding.root) {

        override fun bind(item: Post) {
            setUpProfileInfo(item)
            addPostTimestamp(item)
            setUpPostImage(item)
            setUpPostDescription(item)
            tintHeartIcon(item)
            setUpLikeCount(item)
            setLikeClickAction(item)
        }


        private fun setUpProfileInfo(item: Post) {
            Glide.with(context).load(item.poster?.profile_picture).centerCrop()
                .into(binding.profilePicture)
            binding.profileName.text = item.poster?.username
        }

        private fun addPostTimestamp(item: Post) {
            val createdAt = item.created_at?.time?.div(1000L)?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.postTimeStamp.text = createdAt
        }

        private fun setUpPostImage(item: Post) {
            Glide.with(context).load(item.post_image).centerCrop().into(binding.postImage)
        }

        private fun setUpPostDescription(item: Post) {
            if (item.post_description.isEmpty()) {
                binding.postDescription.hide()
            } else {
                binding.postDescription.text = item.post_description
            }
        }

        private fun tintHeartIcon(item: Post) {
            if (!item.liked) binding.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_favorite_border_24
                )
            )
            else binding.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_favorite_24
                )
            )

        }

        private fun setUpLikeCount(item: Post) {
            if (item.likes > 0) {
                binding.likeCount.show()
                binding.likeCount.text = "${item.likes} likes"
            } else {
                binding.likeCount.hide()
            }
        }

        private fun setLikeClickAction(item: Post) {
            binding.likeButton.setOnClickListener {
                if (item.liked) item.apply { liked = false } else item.apply { liked = true }
                tintHeartIcon(item)
                postClickListener?.onLikeButtonClick(item, item.liked)
            }
        }
    }
}

interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
}