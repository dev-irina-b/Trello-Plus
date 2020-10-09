package ru.irina.trello_plus

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    companion object {
        fun build(): WebService {
            val mapper = jacksonObjectMapper()

            val retrofit  = Retrofit.Builder()
                .baseUrl("https://api.trello.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build()
            return retrofit.create(WebService::class.java)
        }
    }

    @GET("1/members/me/boards")
    suspend fun getBoards(
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): List<Board>
}