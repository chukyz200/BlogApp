package apps.cz200dev.blogapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import apps.cz200dev.blogapp.core.Result
import apps.cz200dev.blogapp.domain.profile.ProfileRepo
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class ProfileViewModel(private val repo: ProfileRepo) : ViewModel() {

    fun getProfileData() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        kotlin.runCatching {
            repo.getProfileData()
        }.onSuccess { result ->
            emit(result)
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }
}

class ProfileViewModelFactory(private val repo: ProfileRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProfileRepo::class.java).newInstance(repo)
    }
}