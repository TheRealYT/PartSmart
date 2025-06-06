package et.com.partsmart.api

import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    suspend fun login(data: LoginRequest): Response<User> {
        return api.login(data)
    }
}
