package ru.irina.trello_plus

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

typealias ResponseCallback<T> = suspend () -> Response<T>

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

        fun <T>AppCompatActivity.makeSafeApiCall (request: ResponseCallback<T>, success: DataCallback<T>) {
            lifecycleScope.launch {
                try {
                    val response = request()

                    if(response.isSuccessful)
                        success(response.body()!!)
                    else
                        toast(R.string.generic_webservice_error)

                } catch (e: Exception) {
                    toast(R.string.generic_webservice_error)
                }
            }
        }
    }

    @GET("1/members/me/boards")
    suspend fun getBoards(
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<List<Board>>

    @GET("1/boards/{id}/cards")
    suspend fun getCards(
        @Path("id") id: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<List<Card>>

    @PUT("1/cards/{id}")
    suspend fun updateCardName(
        @Path("id") id: String,
        @Query("name") name: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Card>

    @PUT("1/boards/{id}")
    suspend fun updateBoardName(
        @Path("id") id: String,
        @Query("name") name: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Board>

    @GET("1/boards/{id}/lists")
    suspend fun getColumns(
        @Path("id") id: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<List<Column>>

    @POST("1/cards")
    suspend fun createNewCard(
        @Query("name") name: String,
        @Query("desc") desc: String,
        @Query("idList") idList: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Card>

    @DELETE("1/cards/{id}")
    suspend fun deleteCard(
        @Path("id") id: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Any>



}