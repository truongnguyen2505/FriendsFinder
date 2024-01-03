package com.finals.friendsfinder.networks

import android.annotation.SuppressLint
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.Utils
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


open class ServicesManager {

    companion object {
        private const val HEADER_DEVICEID_KEY: String = "device_id"
        private const val HEADER_TOKEN_KEY: String = "Authorization"
        private const val TIME_OUT = 3600

        inline fun <reified SV> builder(baseURL: String): SV {
            val builder: Retrofit.Builder = Retrofit.Builder()
            val httpClient: OkHttpClient = getUnsafeOkHttpClient()

            val gson = GsonBuilder()
                .setLenient()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
            val retrofit: Retrofit = builder
                .baseUrl(baseURL)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit.create(SV::class.java)
        }

        fun getUnsafeOkHttpClient(): OkHttpClient {
            return getUnsafeOkHttpClient(TIME_OUT, TIME_OUT)
        }

        @SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
        fun getUnsafeOkHttpClient(
            readTimeoutSecs: Int,
            connectionTimeout: Int,
        ): OkHttpClient {
            try {

                // Create a trust manager that does not validate certificate chains
                val trustAllCerts =
                    arrayOf<TrustManager>(
                        object : X509TrustManager {
                            override fun checkClientTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            override fun checkServerTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate> {
                                return arrayOf()
                            }
                        }
                    )
                // Install the all-trusting trust manager
                val sslContext =
                    SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.addInterceptor { chain ->
                    val original = chain.request()
                    val chainBuilder = original.newBuilder()
                    if (BaseAccessToken.token.isNotEmpty())
                        chainBuilder.header(HEADER_TOKEN_KEY, "Bearer ${BaseAccessToken.token}")
                    val request =
                        chainBuilder.header(HEADER_DEVICEID_KEY, Utils.shared.getDeviceId())
                            .method(original.method, original.body)
                            .build()
                    chain.proceed(request)
                }
                builder.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                builder.hostnameVerifier { _, _ -> true }
                builder.connectTimeout(connectionTimeout.toLong(), TimeUnit.SECONDS)
                    .readTimeout(readTimeoutSecs.toLong(), TimeUnit.SECONDS)
                    .connectionPool(
                        ConnectionPool(0, connectionTimeout.toLong(), TimeUnit.SECONDS)
                    )
                return builder.build()
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            }
        }
    }
}