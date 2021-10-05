package com.betterise.maladiecorona.managers

import android.content.Context
import com.betterise.maladiecorona.model.out.Poll
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Alexandre on 31/08/20.
 */
class PollManager {

    companion object {
        const val PRIVATE_MODE = 0
        const val PREFS = "PREFS"
        const val PREF_POLLS = "PREF_POLLS"
    }

    /***
     * Adding a poll to the list of polls to send
     */
    fun addPoll(context : Context, poll : Poll){
        var polls = getPolls(context)
        polls.add(poll)
        savePolls(context, polls)
    }


    /***
     * Retrieving the polls ready to be sent
     */
    fun getPolls(context : Context) : MutableList<Poll>{

        var json  = context.getSharedPreferences(PREFS, PRIVATE_MODE).getString(PREF_POLLS, "")

        if (json.isNullOrEmpty())
            return arrayListOf()

        val itemType = object : TypeToken<List<Poll>>() {}.type
        return Gson().fromJson(json,itemType)
    }

    /***
     * Save the list of polls
     */
    private fun savePolls(context: Context, polls : MutableList<Poll>){
        context
            .getSharedPreferences(PREFS, PRIVATE_MODE)
            .edit()
            .putString(PREF_POLLS, Gson().toJson(polls))
            .apply()
    }

    /***
     * Reset the list of polls
     */
    fun clearPolls(context: Context){
        context
            .getSharedPreferences(PREFS, PRIVATE_MODE)
            .edit()
            .putString(PREF_POLLS, "")
            .apply()
    }

}