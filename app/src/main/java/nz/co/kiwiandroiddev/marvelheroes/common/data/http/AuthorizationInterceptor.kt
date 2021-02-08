package nz.co.kiwiandroiddev.marvelheroes.common.data.http

import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.PrivateApiKey
import nz.co.kiwiandroiddev.marvelheroes.di.qualifiers.PublicApiKey
import okhttp3.Interceptor
import okhttp3.Response
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Adds the marvel developer API key, timestamp and hash as a query parameters to all requests.
 *
 * Reference: https://futurestud.io/tutorials/retrofit-2-how-to-add-query-parameters-to-every-request
 */
class AuthorizationInterceptor @Inject constructor(
    @PublicApiKey private val publicApiKey: String,
    @PrivateApiKey private val privateApiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val md5Hash = md5(timestamp + privateApiKey + publicApiKey)

        val original = chain.request()
        val originalHttpUrl = original.url
        val newUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("apikey", publicApiKey)
            .addQueryParameter("ts", timestamp)
            .addQueryParameter("hash", md5Hash)
            .build()

        val newRequest = original.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }

    private fun md5(input: String): String {
        val digest: MessageDigest = MessageDigest.getInstance("MD5")
        digest.update(input.toByteArray())
        return BigInteger(1, digest.digest()).toString(16).padStart(32, '0')
    }

}