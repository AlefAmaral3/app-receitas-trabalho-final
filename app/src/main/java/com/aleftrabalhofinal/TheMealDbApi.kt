package com.aleftrabalhofinal

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class MealResponse(val meals: List<MealDto>?)
data class MealDto(val idMeal: String, val strMeal: String, val strCategory: String, val strInstructions: String, val strMealThumb: String)

interface TheMealDbApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse

    companion object {
        private const val BASE_URL = "https://themealdb.p.rapidapi.com/"
        private const val RAPID_API_KEY = "2093d4908amsh804142b57db5699p15e1cejsn9ec44f4f1fa2"
        private const val RAPID_API_HOST = "themealdb.p.rapidapi.com"

        fun create(): TheMealDbApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", RAPID_API_KEY)
                    .addHeader("x-rapidapi-host", RAPID_API_HOST)
                    .build())
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMealDbApi::class.java)
    }
}
