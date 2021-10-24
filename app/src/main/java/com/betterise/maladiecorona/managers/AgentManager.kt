package com.betterise.maladiecorona.managers

import android.content.Context

/**
 * Created by mjc on 01/10/21.
 */
class AgentManager() {

    companion object {
        const val PRIVATE_MODE = 0
        const val PREF_POLLS = "patient_data"
        const val PREFS = "PREFS"
        const val PREF_AGENT_NUMBER = "CHW_phone"
        const val PREF_AGENT_NAME = "CHW_name"

    }

    /***
     * Saving the agent phone number
     */
    fun saveAgentNumber(context: Context, agentNumber : String){
        var sharedPrefs = context.getSharedPreferences(PREFS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("CHW_phone", agentNumber)
            .apply()
    }

    /***
     * Saving the patient data
     */
    fun saveAgentName(context: Context, agentName: String){
        var sharedPrefs = context.getSharedPreferences(PREFS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString(PREF_AGENT_NAME, agentName)
            .apply()
    }



    fun savefirstname(context: Context, firstname: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("firstname", firstname)
            .apply()
    }

    fun savelastname(context: Context, lastname: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("lastname", lastname)
            .apply()
    }

    fun savenational_ID(context: Context, national_ID: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("national_ID", national_ID)
            .apply()
    }
    fun savegender(context: Context, gender: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("gender", gender)
            .apply()
    }
    fun savetelephone(context: Context, telephone: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("telephone", telephone)
            .apply()
    }

    fun savedob(context: Context, dob: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("dob", dob)
            .apply()
    }

    fun saveoccupation(context: Context, occupation: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("occupation", occupation)
            .apply()
    }
    fun saveresidence(context: Context, residence: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("residence", residence)
            .apply()
    }
    fun savenationality(context: Context, nationality: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("nationality", nationality)
            .apply()
    }
    fun saveprovince(context: Context, province: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("province", province)
            .apply()
    }
    fun savedistrict(context: Context, district: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("district", district)
            .apply()
    }
    fun savesector(context: Context, sector: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("sector", sector)
            .apply()
    }

    fun savecell(context: Context, cell: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("cell", cell)
            .apply()
    }
    fun savevillage(context: Context, village: String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("village", village)
            .apply()
    }
    fun saverdt_result(context:Context,rdt_result:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("rdt_result", rdt_result)
            .apply()
    }
    fun saveindexcode(context:Context,index:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("Index", index)
            .apply()
    }

    fun savecategory(context:Context,category:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("category", category)
            .apply()
    }

    fun saveNumberhousehold(context:Context,index:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("number_household", index)
            .apply()
    }
    fun savevaccine_type(context:Context,index:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("vaccine_type", index)
            .apply()
    }
    fun savevaccine_dose(context:Context,index:String){
        var sharedPrefs = context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE)
        sharedPrefs
            .edit()
            .putString("vaccine_dose", index)
            .apply()
    }


    /***
     * Retrieving the current agent number
     */
    fun getAgentNumber(context: Context)= context.getSharedPreferences(PREFS, PRIVATE_MODE).getString(PREF_AGENT_NUMBER, "")

    fun getAgentName(context: Context)= context.getSharedPreferences(PREFS, PRIVATE_MODE).getString(PREF_AGENT_NAME, "")
    fun getFirstname(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("firstname", "")
    fun getLastname(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("lastname", "")
    fun getNid(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("national_ID", "")
    fun getGender(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("gender", "")
    fun getTelephone(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("telephone", "")
    fun getDob(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("dob", "")
    fun getOccupation(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("occupation", "")
    fun getResidence(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("residence", "")
    fun getNationality(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("nationality", "")
    fun getProvince(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("province", "")
    fun getDistrict(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("district", "")
    fun getSector(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("sector", "")
    fun getCell(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("cell", "")
    fun getVillage(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("village", "")
    fun getrdt_result(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("rdt_result", "")
    fun getindexcode(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("Index", "")
    fun getcategory(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("category", "")
    fun getnumberhousehold(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("number_household", "")
    fun getvaccine_type(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("vaccine_type", "")
    fun getvaccine_dose(context: Context)= context.getSharedPreferences(PREF_POLLS, PRIVATE_MODE).getString("vaccine_dose", "")


}