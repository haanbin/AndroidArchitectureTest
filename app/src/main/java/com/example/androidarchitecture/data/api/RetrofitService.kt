package com.example.androidarchitecture.data.api

import com.example.androidarchitecture.data.entities.NaverMovie
import com.example.androidarchitecture.data.entities.RandomUser
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface RetrofitService {

    @GET
    fun getRandomUser(
        @Url
        url: String,
        @QueryMap
        queryMap: Map<String, String>
    ): Single<RandomUser>

    @GET("movie.json")
    suspend fun getMovie(
        @HeaderMap
        headerMap: Map<String, String>,
        @QueryMap
        queryMap: Map<String, String>
    ): Response<NaverMovie>
}
