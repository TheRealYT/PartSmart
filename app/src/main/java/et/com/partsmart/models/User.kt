package et.com.partsmart.models

data class User(val name: String)

data class LoginRequest(val username: String, val password: String)