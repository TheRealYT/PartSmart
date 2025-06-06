package et.com.partsmart.models

data class LoginRequest(val name: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)