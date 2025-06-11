package et.com.partsmart.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import et.com.partsmart.api.Repository
import kotlinx.coroutines.launch
import et.com.partsmart.R
import et.com.partsmart.models.Product

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val res = Repository.getProducts()
                if (res.isSuccessful && res.body()?.success == true) {
                    _products.value = res.body()?.products
                } else {
                    _errorMessage.value =
                        Repository.getErrorMessage(res.errorBody(), getApplication())
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    getApplication<Application>().getString(R.string.unknown_error)
            }
        }
    }
}
