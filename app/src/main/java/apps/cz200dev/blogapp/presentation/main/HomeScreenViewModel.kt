package apps.cz200dev.blogapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.data.model.Post
import apps.cz200dev.blogapp.domain.home.HomeScreenRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeScreenViewModel(private val repo: HomeScreenRepo) : ViewModel() {

    fun fetchLatestPost() = liveData(Dispatchers.IO) {
        emit(Result.Loading())

        kotlin.runCatching {
            repo.getLatestPost()
        }.onSuccess { postList ->
            emit(postList)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }

    //StateFlow
    val latestPost: StateFlow<Result<List<Post>>> = flow {
        kotlin.runCatching {
            repo.getLatestPost()
        }.onSuccess { postList ->
            emit(postList)
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading()
    )

    private val post = MutableStateFlow<Result<List<Post>>>(Result.Loading())
    fun fetchPost() = viewModelScope.launch {
        kotlin.runCatching {
            repo.getLatestPost()
        }.onSuccess { postList ->
            post.value = postList
        }.onFailure { throwable ->
            post.value = Result.Failure(Exception(throwable))
        }
    }

    fun getPost() = post


    fun registerButtonState(postId: String, liked: Boolean) =
        liveData(viewModelScope.coroutineContext + Dispatchers.Main) {
            emit(Result.Loading())
            kotlin.runCatching {
                repo.registerLikeButtonState(postId, liked)
            }.onSuccess {
                emit(Result.Success(Unit))
            }.onFailure { throwable ->
                emit(Result.Failure(Exception(throwable.message)))
            }
        }
}

class HomeScreenViewModelFactory(private val repo: HomeScreenRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo) as T
    }
}
