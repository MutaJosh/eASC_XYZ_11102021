package com.betterise.maladiecorona.networking

import com.betterise.maladiecorona.model.out.Poll
import com.betterise.maladiecorona.model.out.PollItems
import com.google.gson.JsonObject
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Alexandre on 31/08/20.
 */
interface ApiService {

    companion object {
        const val API_SUCCESS = "200"
        const val API_STATUS = "status"
    }
    // Posting the polls
    @POST(".")
    @Headers("Content-Type: application/json")
    fun postPolls(@Body polls : PollItems) : Observable<JsonObject>
}