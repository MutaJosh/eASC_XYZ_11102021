package com.betterise.maladiecorona.managers

import android.annotation.SuppressLint
import android.content.Context
import com.betterise.maladiecorona.model.Answer
import com.betterise.maladiecorona.model.Question.Companion.BREATH
import com.betterise.maladiecorona.model.Question.Companion.CANCER
import com.betterise.maladiecorona.model.Question.Companion.CONTACT
import com.betterise.maladiecorona.model.Question.Companion.COUGH
import com.betterise.maladiecorona.model.Question.Companion.DIABETES
import com.betterise.maladiecorona.model.Question.Companion.DIARRHEA
import com.betterise.maladiecorona.model.Question.Companion.EAT_DRINK
import com.betterise.maladiecorona.model.Question.Companion.FIVER
import com.betterise.maladiecorona.model.Question.Companion.HEIGHT
import com.betterise.maladiecorona.model.Question.Companion.MUSCLE_PAIN
import com.betterise.maladiecorona.model.Question.Companion.PREGNANT
import com.betterise.maladiecorona.model.Question.Companion.RESPIRATORY_DISEASE
import com.betterise.maladiecorona.model.Question.Companion.TASTE
import com.betterise.maladiecorona.model.Question.Companion.TEMPERATURE
import com.betterise.maladiecorona.model.Question.Companion.TEMP_PERIOD
import com.betterise.maladiecorona.model.Question.Companion.TENSION
import com.betterise.maladiecorona.model.Question.Companion.THROAT
import com.betterise.maladiecorona.model.Question.Companion.TRAVEL
import com.betterise.maladiecorona.model.Question.Companion.WEIGHT
import com.betterise.maladiecorona.model.ResultType
import kotlin.math.pow

/**
 * Created by Alexandre on 25/06/20.
 */
class ResultManager {

    /***
     * Algorithm that defines a diagnoses depending on the answers
     */

    fun getResults(answers : MutableList<Answer>) : ResultType {

        var result = ResultType.CASE1

//        if (answers[TEMPERATURE2].value == 2){
//
//            if (answers[COUGH].value == 1 && answers[TASTE].value == 1 && answers[SORE].value == 1)
//                result = ResultType.CASE2
//            else if (answers[TENSION].value == 1 &&
//                answers[TENSION].value == 3 &&
//                answers[DIABETES].value == 1 &&
//                answers[CANCER].value == 1 &&
//                answers[RESPIRATORY_DISEASE].value == 1 &&
//                answers[PREGNANT].value == 1 &&
//                getIMC(answers) >= QuestionManager.IMC_LIMIT){
//
//                if (answers[BREATH].value == 1 && answers[ALIMENTATION].value == 1) {
//                    result = ResultType.CASE3bis
//
//                }  else{
//                    result = ResultType.CASE3}
//            }
//        }
//        else if (answers[COUGH].value == 1){
//
//            if (answers[TASTE].value == 1 && answers[SORE].value == 1) {
//
//                if (answers[TENSION].value == 1 &&
//                    answers[TENSION].value == 3 &&
//                    answers[DIABETES].value == 1 &&
//                    answers[CANCER].value == 1 &&
//                    answers[RESPIRATORY_DISEASE].value == 1 &&
//                    answers[PREGNANT].value == 1 &&
//                    getIMC(answers) >= QuestionManager.IMC_LIMIT) {
//
//                    if (answers[BREATH].value == 1 && answers[ALIMENTATION].value == 1)
//                        result = ResultType.CASE3bis
//                    else
//                        result = ResultType.CASE3
//                }
//
//            }
//        } else if (answers[DIARRHEA].value == 1){
//
//            if (answers[TENSION].value == 1 &&
//                answers[TENSION].value == 3 &&
//                answers[DIABETES].value == 1 &&
//                answers[CANCER].value == 1 &&
//                answers[RESPIRATORY_DISEASE].value == 1 &&
//                answers[PREGNANT].value == 1 &&
//
//                getIMC(answers) >= QuestionManager.IMC_LIMIT) {
//
//                if (answers[BREATH].value == 1 && answers[ALIMENTATION].value == 1)
//                    result = ResultType.CASE3bis
//                else
//                    result = ResultType.CASE3
//            }
//
//
//
//        } else if (answers[BREATH].value == 1 && answers[ALIMENTATION].value == 1)
//            result = ResultType.CASE5



        if (
                answers[TRAVEL].value == 1 || answers[CONTACT].value == 1 || answers[TEMPERATURE].value == 2

//                &&
//                answers[CONTACT].value == 1 &&
//                answers[FIVER].value == 1 &&
//                answers[TEMPERATURE].value == 2 &&
//                answers[COUGH].value == 1 &&
//                answers[EAT_DRINK].value == 1 &&
//                answers[BREATH].value == 1 &&
//                answers[THROAT].value == 1 &&
//                answers[TASTE].value == 1 &&
//                answers[MUSCLE_PAIN].value == 1 &&
//                answers[DIARRHEA].value == 1 &&
//                answers[WEIGHT].value == 1 &&
//                answers[HEIGHT].value == 1 &&
//                answers[TENSION].value == 1 && answers[TENSION].value == 3 &&    // When the answer is YES or DO NOT KNOW
//                answers[DIABETES].value == 1 &&
//                answers[CANCER].value == 1 &&
//                answers[RESPIRATORY_DISEASE].value == 1 &&
//                answers[PREGNANT].value == 1

        ){

          //  POSITIVE as the screening outcome
          result = ResultType.CASE2


        }else{

            // This is when all the conditions are all false.
                // NO as the answer for all choices
                // When someone chose the lowest conditions, ...

//                    NEGATIVE as the screening outcome
            result = ResultType.CASE1
        }



        return result
    }




    /***
     * Calculating IMC
     */
    private fun getIMC(answers : MutableList<Answer>) =


    if (answers[WEIGHT].value <= 1 && answers[HEIGHT].value <= 1)
            100.0
        else
            answers[WEIGHT].value / (answers[HEIGHT].value.toDouble()/100).pow(2)


}