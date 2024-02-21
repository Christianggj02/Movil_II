package com.CL.sicenet.data

import com.CL.sicenet.network.siceApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val siceRepository: SiceRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://sicenet.surguanajuato.tecnm.mx/"

    // Configurar el interceptor
    val cookiesInterceptor = CookiesInterceptor()

    // Configurar OkHttpClient con el interceptor
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(cookiesInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val retrofitService: siceApiService = retrofit.create(siceApiService::class.java)

    override val siceRepository: SiceRepository = NetworkSiceRepository(retrofitService, retrofit)

}

class CookiesInterceptor : Interceptor {

    // Variable que almacena las cookies
    private var cookies: List<String> = emptyList()

    // Método para establecer las cookies
    fun setCookies(cookies: List<String>) {
        this.cookies = cookies
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Agregar las cookies al encabezado de la solicitud
        if (cookies.isNotEmpty()) {
            val cookiesHeader = StringBuilder()
            for (cookie in cookies) {
                if (cookiesHeader.isNotEmpty()) {
                    cookiesHeader.append("; ")
                }
                cookiesHeader.append(cookie)
            }

            request = request.newBuilder()
                .header("Cookie", cookiesHeader.toString())
                .build()
        }

        val response = chain.proceed(request)

        // Almacenar las cookies de la respuesta para futuras solicitudes
        val receivedCookies = response.headers("Set-Cookie")
        if (receivedCookies.isNotEmpty()) {
            setCookies(receivedCookies)
        }

        return response
    }
}