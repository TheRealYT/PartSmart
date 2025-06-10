package et.com.partsmart.api

import android.app.Application
import et.com.partsmart.R
import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.LoginResponse
import et.com.partsmart.models.RegisterRequest
import et.com.partsmart.models.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8000/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    suspend fun login(data: LoginRequest): Response<LoginResponse> {
        return api.login(data)
    }

    suspend fun register(data: RegisterRequest): Response<RegisterResponse> {
        return api.register(data)
    }

    fun parseErrorBody(errorBody: ResponseBody?): BaseResponse? {
        return try {
            val converter = retrofit.responseBodyConverter<BaseResponse>(BaseResponse::class.java, arrayOf())
            errorBody?.let { converter.convert(it) }
        } catch (e: Exception) {
            null
        }
    }

    fun getErrorMessage(errorBody: ResponseBody?, app: Application): String {
        return parseErrorBody(errorBody)?.message ?: app.getString(R.string.unknown_error)
    }
}
