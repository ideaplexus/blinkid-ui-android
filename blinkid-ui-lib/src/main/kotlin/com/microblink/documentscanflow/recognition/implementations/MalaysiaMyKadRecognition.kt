package com.microblink.documentscanflow.recognition.implementations

import com.microblink.documentscanflow.recognition.resultentry.ResultKey.*
import com.microblink.documentscanflow.isNotEmpty
import com.microblink.documentscanflow.recognition.BaseTwoSideRecognition
import com.microblink.documentscanflow.recognition.ResultValidator
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.blinkid.malaysia.MalaysiaMyKadBackRecognizer
import com.microblink.entities.recognizers.blinkid.malaysia.MalaysiaMyKadFrontRecognizer

class MalaysiaMyKadRecognition
    : BaseTwoSideRecognition() {

    val frontRecognizer by lazy { MalaysiaMyKadFrontRecognizer() }
    val backRecognizer by lazy { MalaysiaMyKadBackRecognizer() }

    val frontResult by lazy { frontRecognizer.result }
    val backResult by lazy { backRecognizer.result }

    override fun getSingleSideRecognizers(): List<Recognizer<*, *>> {
        return listOf(frontRecognizer, backRecognizer)
    }

    override fun createValidator(): ResultValidator {
        return ResultValidator()
                .match(frontResult.nric, backResult.nric)
                .match(frontResult.birthDate.date, backResult.dateOfBirth.date)
    }

    override fun extractFields() {
        if (frontResult.isNotEmpty()) {
            extractFrontSide()
        }
        if (backResult.isNotEmpty()) {
            extractBackSide()
        }
    }

    override fun getResultTitle(): String? {
        if (frontResult.isNotEmpty()) {
            return frontResult.fullName
        } else if (backResult.isNotEmpty()) {
            return backResult.nric
        }
        return null
    }

    private fun extractFrontSide() {
        add(FULL_NAME, frontResult.fullName)
        add(NRIC_NUMBER, frontResult.nric)
        add(DATE_OF_BIRTH, frontResult.birthDate)
        add(ADDRESS, frontResult.fullAddress)
        add(SEX, frontResult.sex)
        add(RELIGION, frontResult.religion)
    }

    private fun extractBackSide() {
        add(NRIC_NUMBER, backResult.nric)
        add(DATE_OF_BIRTH, backResult.dateOfBirth)
    }

}
