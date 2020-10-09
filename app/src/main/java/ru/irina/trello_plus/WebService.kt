package ru.irina.trello_plus

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
}