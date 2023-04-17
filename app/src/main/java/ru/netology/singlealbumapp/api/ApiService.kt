package ru.netology.singlealbumapp.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.netology.singlealbumapp.BuildConfig
import ru.netology.singlealbumapp.model.Album


private const val BASE_URL = BuildConfig.BASE_URL

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()


interface ApiService {
    @GET("album.json")
    suspend fun loadAlbum(): Response<Album>

}

object AlbumApi {

    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

}