package et.com.partsmart.view_models

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import et.com.partsmart.api.Repository
import et.com.partsmart.api.handleException
import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.RegisterRequest
import et.com.partsmart.models.User
import kotlinx.coroutines.launch

internal const val PREF_KEY_USERID = "user_id"
internal const val PREF_KEY_USERNAME = "username"
internal const val PREF_KEY_EMAIL = "email"
internal const val PREF_KEY_LOGGED_IN = "logged_in"
internal const val PREF_KEY_TOKEN = "token"

internal const val PREF_AUTH_KEY = "auth_prefs"

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences(PREF_AUTH_KEY, Context.MODE_PRIVATE)

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
                    _errorMessage.value =
                        Repository.getErrorMessage(response.errorBody(), getApplication())
                }
            } catch (e: Exception) {
                _errorMessage.value = handleException(e, getApplication())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = Repository.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()

                    prefs.edit()
                        .putBoolean(PREF_KEY_LOGGED_IN, true)
                        .putString(PREF_KEY_USERID, body!!.user.id)
                        .putString(PREF_KEY_USERNAME, body.user.username)
                        .putString(PREF_KEY_EMAIL, body.user.email)
                        .putString(PREF_KEY_TOKEN, response.headers()["Set-Cookie"]).apply()
                    _isLoggedIn.value = true
                } else {
                    _errorMessage.value =
                        Repository.getErrorMessage(response.errorBody(), getApplication())
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
        return prefs.getBoolean(PREF_KEY_LOGGED_IN, false)
    }

    fun getSessionToken(): String? {
        return prefs.getString(PREF_KEY_TOKEN, null)
    }

    fun getUser(): User? {
        val id = prefs.getString(PREF_KEY_USERID, null) ?: return null
        val username = prefs.getString(PREF_KEY_USERNAME, null) ?: return null
        val email = prefs.getString(PREF_KEY_EMAIL, null) ?: return null

        return User(
            id = id,
            username = username,
            email = email
        )
    }


    fun logout() {
        prefs.edit().clear().apply()
    }
}