package et.com.partsmart.api

import et.com.partsmart.models.AddProductResponse
import et.com.partsmart.models.LoginRequest
import et.com.partsmart.models.LoginResponse
import et.com.partsmart.models.RegisterRequest
import et.com.partsmart.models.RegisterResponse
import et.com.partsmart.models.UserProductsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @Multipart
    @POST("addProduct.php")
    @Headers("Auth: true")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("seller_id") sellerId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<AddProductResponse>

    @GET("getUserProducts.php")
    @Headers("Auth: true")
    suspend fun getUserProducts(@Query("userId") userId: String): Response<UserProductsResponse>
}