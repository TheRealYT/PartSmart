package et.com.partsmart.api

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefs: SharedPreferences, private val prefKey: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requiresAuth = originalRequest.header("Auth") == "true"

        val requestBuilder = originalRequest.newBuilder()
            .removeHeader("Auth")

        if (requiresAuth) {
            val cookie = prefs.getString(prefKey, null)
            if (cookie != null) {
                requestBuilder.addHeader("Cookie", cookie)
            }
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}