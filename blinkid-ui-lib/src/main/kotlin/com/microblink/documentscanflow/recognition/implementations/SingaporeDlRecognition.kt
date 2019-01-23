package com.microblink.documentscanflow.recognition.implementations

import com.microblink.documentscanflow.R
import com.microblink.documentscanflow.buildId1CardDetectorRecognizer
import com.microblink.documentscanflow.isEmpty
import com.microblink.documentscanflow.recognition.BaseRecognition
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.blinkid.singapore.SingaporeDlFrontRecognizer

class SingaporeDlRecognition : BaseRecognition() {

    private val frontRecognizer by lazy { SingaporeDlFrontRecognizer() }
    private val backRecognizer by lazy { buildId1CardDetectorRecognizer() }

    override fun getSingleSideRecognizers() = listOf<Recognizer<*, *>>(frontRecognizer, backRecognizer)

    override fun extractData(): String? {
        val result = frontRecognizer.result
        if (result.isEmpty()) {
            return null
        }

        add(R.string.keyDocumentNumber, result.licenceNumber)
        add(R.string.keyFullName, result.name)
        add(R.string.keyDateOfBirth, result.birthDate)
        add(R.string.keyIssueDate, result.issueDate)
        addDateOfExpiry(result.validTill.date)

        return result.name
    }
}