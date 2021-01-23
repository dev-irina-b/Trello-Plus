package ru.irina.trello_plus

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CancellationException
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
                    else {
                        toast(R.string.generic_webservice_error)
                    }

                } catch (e: Exception) {
                    if(e !is CancellationException) {
                        toast(R.string.generic_webservice_error)
                    }
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
    suspend fun updateCard(
        @Path("id") id: String,
        @Query("name") name: String,
        @Query("desc") desc: String,
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
    ): Response<Any?>

    @PUT("1/lists/{id}")
    suspend fun updateColumnName(
        @Path("id") id: String,
        @Query("name") name: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Column>

    @GET("1/boards/{id}/members")
    suspend fun getBoardMembers(
        @Path("id") id: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<List<Member>>

    @GET("1/tokens/{token}")
    suspend fun getTokenInfo(
        @Path("token") userToken: String,
        @Query("token") apiToken: String,
        @Query("key") key: String = API_KEY
    ): Response<TokenInfo>

    @GET("1/cards/{id}/actions")
    suspend fun getComments(
        @Path("id") id: String,
        @Query("token") apiToken: String,
        @Query("key") key: String = API_KEY,
        @Query("filter") filter: String = COMMENT_CARD_ACTION
    ): Response<List<Comment>>

    @POST("1/cards/{id}/actions/comments")
    suspend fun addComment(
        @Path("id") id: String,
        @Query("text") text: String,
        @Query("token") apiToken: String,
        @Query("key") key: String = API_KEY
    ): Response<Comment>

    @PUT("1/cards/{id}")
    suspend fun updateCardDueComplete(
        @Path("id") id: String,
        @Query("dueComplete") dueComplete: Boolean,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Card>

    @GET("1/cards/{id}/checklists")
    suspend fun getChecklists(
        @Path("id") id: String,
        @Query("token") apiToken: String,
        @Query("key") key: String = API_KEY,
    ): Response<List<CheckList>>

    @DELETE("1/cards/{id}/actions/{idAction}/comments")
    suspend fun deleteComment(
        @Path("id") id: String,
        @Path("idAction") idAction: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Any?>

    @PUT("1/cards/{id}/actions/{idAction}/comments")
    suspend fun updateComment(
        @Path("id") id: String,
        @Path("idAction") idAction: String,
        @Query("text") text: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Comment>

    @GET("1/boards/{id}/labels")
    suspend fun getLabels(
        @Path("id") id: String,
        @Query("token") apiToken: String,
        @Query("key") key: String = API_KEY,
    ): Response<List<Label>>

    @POST("1/cards/{id}/idLabels")
    suspend fun addLabel(
        @Path("id") id: String,
        @Query("value") value: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Any?>

    @DELETE("1/cards/{id}/idLabels/{idLabel}")
    suspend fun deleteLabel(
        @Path("id") id: String,
        @Path("idLabel") idLabel: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Any?>

    @DELETE("1/cards/{id}/checklists/{idChecklist}")
    suspend fun deleteChecklist(
        @Path("id") id: String,
        @Path("idChecklist") idChecklist: String,
        @Query("token") token: String,
        @Query("key") key: String = API_KEY
    ): Response<Any?>
}