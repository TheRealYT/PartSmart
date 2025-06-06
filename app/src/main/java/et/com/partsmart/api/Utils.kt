package et.com.partsmart.api

import android.app.Application
import et.com.partsmart.R
import retrofit2.HttpException
import java.io.IOException

fun handleException(e: Exception, context: Application): String {
    return when (e) {
        is HttpException -> {
            context.getString(R.string.network_error)
        }
        is IOException -> {
            context.getString(R.string.network_error)
        }
        else -> {
            context.getString(R.string.unknown_error)
        }
    }
}