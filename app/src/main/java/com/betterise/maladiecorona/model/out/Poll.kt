package com.betterise.maladiecorona.model.out

/**
 * Created by Alexandre on 31/08/20.
 */
class Poll {

    var pollId : Int = 0
    var date : String = ""
    var CHW_phone : String = ""
    var CHW_name:String=""

    var firstname : String = ""
    var lastname : String = ""
    var national_ID : String = ""
    var patientgender : String = ""
    var patienttelephone: String = ""
    var dob : String = ""
    var occupation  : String = ""
    var residence : String = ""
    var nationality : String = ""
    var province: String = ""
    var district: String = ""
    var sector : String = ""
    var cell: String = ""
    var village: String = ""
    var rdt_result: String=""
    var Index: String=""
    var category:String=""
    var number_household:String=""
    var vaccine_type:String=""
    var vaccine_dose:String=""

    var ASCOV_answers : MutableList<PollAnswer> = arrayListOf()

    var eascov_result : PollResult = PollResult()

}