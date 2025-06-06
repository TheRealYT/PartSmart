package et.com.partsmart.api

import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<User>
}