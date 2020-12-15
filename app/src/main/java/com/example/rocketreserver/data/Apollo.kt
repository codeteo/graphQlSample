package com.example.rocketreserver.data

import android.content.Context
import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.example.rocketreserver.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private var instance: ApolloClient? = null

private const val SERVER_URL = "https://apollo-fullstack-tutorial.herokuapp.com"

fun apolloClient(context: Context): ApolloClient {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Only the main thread can get the apolloClient instance"
    }

    if (instance != null) {
        return instance!!
    }

    instance = ApolloClient.builder()
            .serverUrl(SERVER_URL)
            .okHttpClient(httpclient(context))
            .build()

    return instance!!
}

private fun httpclient(context: Context): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient().newBuilder()
        .addInterceptor(AuthorizationInterceptor(context))
        .addInterceptor(httpLoggingInterceptor)
        .build()
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader("Authorization", User.getToken(context) ?: "")
                .build()

        return chain.proceed(request)
    }
}

fun retrofitClient(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(SERVER_URL)
        .client(okHttpClient)
        .build()
}
