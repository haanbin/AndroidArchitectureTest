package com.example.androidarchitecture.data.source.remote

import com.example.androidarchitecture.data.entities.NaverMovie
import com.example.androidarchitecture.data.entities.RandomUser
import io.reactivex.Single
import retrofit2.Response

interface RemoteDataSource {

    fun getRandomUser(queryMap: Map<String, String>, url: String): Single<RandomUser>

    suspend fun getMovie(
        headerMap: Map<String, String>,
        queryMap: Map<String, String>
    ): Response<NaverMovie>
}
