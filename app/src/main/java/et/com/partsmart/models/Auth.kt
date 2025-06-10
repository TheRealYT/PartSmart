package et.com.partsmart.models

import et.com.partsmart.api.BaseResponse

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val user: User
) : BaseResponse(success = true, message = "")

data class RegisterResponse(
    val user: User
) : BaseResponse(success = true, message = "")

data class RegisterRequest(val username: String, val email: String, val password: String)