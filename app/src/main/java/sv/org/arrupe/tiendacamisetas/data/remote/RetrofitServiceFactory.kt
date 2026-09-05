package sv.org.arrupe.tiendacamisetas.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.org.arrupe.tiendacamisetas.data.session.SessionManager
import java.util.concurrent.TimeUnit


object RetrofitServiceFactory {

    // ✅ La BASE_URL debe terminar exactamente en la barra del puerto
    private const val BASE_URL = "http://10.0.2.2:5200/"

    fun makeRetrofitService(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val clienteHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}