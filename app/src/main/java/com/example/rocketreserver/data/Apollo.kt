package com.example.rocketreserver.data

import android.content.Context
import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.example.rocketreserver.LaunchListQuery
import com.example.rocketreserver.User
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private var instance: ApolloClient? = null

private var okHttpClient: OkHttpClient? = null

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
            .okHttpClient(okHttpClient!!)
            .build()

    return instance!!
}

fun initHttpClient(context: Context): OkHttpClient {
    if (instance != null) {
        return okHttpClient!!
    }

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    okHttpClient =  OkHttpClient().newBuilder()
        .addInterceptor(AuthorizationInterceptor(context))
        .addInterceptor(httpLoggingInterceptor)
        .build()

    return okHttpClient!!
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader("Authorization", User.getToken(context) ?: "")
                .build()

        return chain.proceed(request)
    }
}

fun retrofitClient(context: Context): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(SERVER_URL)
        .client(okHttpClient!!)
        .build()
}

interface ApiService {

    @POST("/")
    fun getLaunches(@Body params: RequestBody): Observable<retrofit2.Response<LaunchListQuery.Data>>

}