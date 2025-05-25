package com.example.nagomiatoru.network

import com.example.nagomiatoru.models.GeoapifyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyService {
    @GET("v2/places")
    suspend fun getRecreationalPlaces(
        @Query("categories")
        categories: String = "leisure.park,entertainment.culture,sport.sports_centre",
        @Query("filter") filter: String,
        @Query("limit") limit: Int = 20,
        @Query("apiKey") apiKey: String
    ): Response<GeoapifyResponse>

}