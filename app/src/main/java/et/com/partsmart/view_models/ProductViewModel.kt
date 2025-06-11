package et.com.partsmart.view_models

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import et.com.partsmart.api.Repository
import et.com.partsmart.api.handleException
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _productId = MutableLiveData<String?>()
    val productId: LiveData<String?> get() = _productId

    fun uploadProduct(
        name: String,
        description: String,
        price: String,
        category: String,
        condition: String,
        sellerId: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = Repository.addProduct(
                    name,
                    description,
                    price,
                    category,
                    condition,
                    sellerId,
                    imageUri,
                    getApplication()
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _productId.value = response.body()?.productId
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
}