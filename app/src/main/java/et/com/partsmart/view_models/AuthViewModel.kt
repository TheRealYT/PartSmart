package et.com.partsmart.view_models

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import et.com.partsmart.R
import et.com.partsmart.api.Repository
import et.com.partsmart.api.handleException
import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // state variables
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = Repository.register(RegisterRequest(name, email, password))
                if (response.isSuccessful) {
                    _isRegistered.value = true
                } else {
                    _errorMessage.value = getApplication<Application>().getString(R.string.registration_failed)
                }
            } catch (e: Exception) {
                _errorMessage.value = handleException(e, getApplication())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = Repository.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    prefs.edit().putBoolean("logged_in", true).apply()
                    _isLoggedIn.value = true
                } else {
                    _errorMessage.value =
                        getApplication<Application>().getString(R.string.incorrect_credentials)
                }
            } catch (e: Exception) {
                _errorMessage.value = handleException(e, getApplication())
            } finally {
                _isLoggedIn.value = false
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // check if the user has a stored login session
    fun hasLoginSession(): Boolean {
        return prefs.getBoolean("logged_in", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}