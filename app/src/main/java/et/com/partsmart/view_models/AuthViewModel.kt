package et.com.partsmart.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import et.com.partsmart.R
import et.com.partsmart.api.Repository
import et.com.partsmart.models.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun login(username: String, password: String) {
        Log.d("LoginViewModel", "login() called with $username")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = Repository.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    _isLoggedIn.value = true
                } else {
                    _errorMessage.value =
                        getApplication<Application>().getString(R.string.incorrect_credentials)
                }
            } catch (e: HttpException) {
                _errorMessage.value =
                    getApplication<Application>().getString(R.string.network_error)
                Log.e("LoginViewModel", "HttpException: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.value =
                    getApplication<Application>().getString(R.string.unknown_error)
                Log.e("LoginViewModel", "Exception: ${e.message}")
            } finally {
                _isLoggedIn.value = false
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

