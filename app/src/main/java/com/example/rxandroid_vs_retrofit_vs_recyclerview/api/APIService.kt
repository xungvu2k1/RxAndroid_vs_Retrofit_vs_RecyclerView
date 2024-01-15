package com.example.retrofit_vs_recyclerview.api

import com.example.retrofit_vs_recyclerview.models.Comment
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIService {
    val BASE_URL2: String
        get() = "https://jsonplaceholder.typicode.com/"

    val apiService: APIService get() = Retrofit.Builder()
        .baseUrl(BASE_URL2)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // call Adapter Factory RxJava3
        .build()
        .create(APIService::class.java)

    @GET("comments")
    fun getComments() : Observable<List<Comment>>
}