package et.com.partsmart.api

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import et.com.partsmart.R
import et.com.partsmart.models.AddProductResponse
import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.LoginResponse
import et.com.partsmart.models.RegisterRequest
import et.com.partsmart.models.RegisterResponse
import et.com.partsmart.models.UserProductsResponse
import et.com.partsmart.view_models.PREF_AUTH_KEY
import et.com.partsmart.view_models.PREF_KEY_TOKEN
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal const val BASE_URL = "http://localhost:8000/api"

object Repository {
    private lateinit var prefs: SharedPreferences

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs, PREF_KEY_TOKEN))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_URL/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_AUTH_KEY, Context.MODE_PRIVATE)
    }

    suspend fun login(data: LoginRequest): Response<LoginResponse> {
        return api.login(data)
    }

    suspend fun register(data: RegisterRequest): Response<RegisterResponse> {
        return api.register(data)
    }

    private fun parseErrorBody(errorBody: ResponseBody?): BaseResponse? {
        return try {
            val converter =
                retrofit.responseBodyConverter<BaseResponse>(BaseResponse::class.java, arrayOf())
            errorBody?.let { converter.convert(it) }
        } catch (e: Exception) {
            null
        }
    }

    fun getErrorMessage(errorBody: ResponseBody?, app: Application): String {
        return parseErrorBody(errorBody)?.message ?: app.getString(R.string.unknown_error)
    }

    suspend fun addProduct(
        name: String,
        description: String,
        price: String,
        category: String,
        condition: String,
        sellerId: String,
        imageUri: Uri,
        context: Context
    ): Response<AddProductResponse> {
        val contentResolver = context.contentResolver

        val imageStream = contentResolver.openInputStream(imageUri)
        val imageBytes = imageStream?.readBytes() ?: byteArrayOf()
        val requestImage =
            imageBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, imageBytes.size)
        val imagePart = MultipartBody.Part.createFormData("images", "image.jpg", requestImage)

        return api.addProduct(
            name.toRequestBody(),
            description.toRequestBody(),
            price.toRequestBody(),
            category.toRequestBody(),
            condition.toRequestBody(),
            sellerId.toRequestBody(),
            imagePart
        )
    }

    private fun String.toRequestBody(): RequestBody =
        this.toRequestBody("text/plain".toMediaTypeOrNull())

    suspend fun getUserProducts(userId: String): Response<UserProductsResponse> {
        return api.getUserProducts(userId)
    }
}
