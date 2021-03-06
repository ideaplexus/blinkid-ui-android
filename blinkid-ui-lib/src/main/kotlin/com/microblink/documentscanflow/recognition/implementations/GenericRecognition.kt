package com.microblink.documentscanflow.recognition.implementations

import com.microblink.documentscanflow.*
import com.microblink.documentscanflow.recognition.BaseRecognition
import com.microblink.documentscanflow.recognition.RecognizerProvider
import com.microblink.documentscanflow.recognition.resultentry.ResultKey
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.blinkbarcode.pdf417.Pdf417Recognizer
import com.microblink.entities.recognizers.blinkid.documentface.DocumentFaceRecognizer
import com.microblink.entities.recognizers.blinkid.mrtd.MrtdRecognizer

class GenericRecognition(isFullySupported: Boolean, private val recognizerProvider: RecognizerProvider) : BaseRecognition(isFullySupported) {

    override fun getSingleSideRecognizers(): List<Recognizer<*, *>> {
        return recognizerProvider.recognizers
    }

    override fun setupRecognizers() {
        for (recognizer in recognizerProvider.recognizers) {
            if (recognizer is MrtdRecognizer) {
                recognizer.isAllowUnverifiedResults = true
            }
        }
    }

    override fun extractData(): String? {
        var result: String? = null
        for (recognizer in recognizerProvider.recognizers) {
            if (recognizer is MrtdRecognizer && recognizer.result.isNotEmpty()) {
                extractMrzResult(recognizer.result.mrzResult)
                result = buildMrtdTitle(recognizer.result.mrzResult)
            }
            if (recognizer is Pdf417Recognizer && recognizer.result.isNotEmpty()) {
                add(ResultKey.BARCODE_DATA, recognizer.result.stringData)
            }
        }
        return result
    }

    companion object {

        val residencePermit = faceMrtd(true)

        val id = faceMrtd(false)

        val drivingLicence = faceId1(false)

        val passport = mrtd(true)

        val visa = mrtd(true)

        fun mrtd(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() = listOf(MrtdRecognizer())
            })
        }

        fun id1(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() =
                        listOf(buildId1CardDetectorRecognizer())
            })
        }

        fun mrtdId1(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() = listOf(MrtdRecognizer(),
                        buildId1CardDetectorRecognizer())
            })
        }

        fun mrtdId2Vertical(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() = listOf(MrtdRecognizer(), buildId2VerticalCardDetectorRecognizer())
            })
        }

        fun faceMrtd(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers(): List<Recognizer<*, *>> {
                    return listOf(DocumentFaceRecognizer(), MrtdRecognizer())
                }
            })
        }

        fun facePdf417(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() = listOf(DocumentFaceRecognizer(), Pdf417Recognizer())
            })
        }

        fun faceId1(isFullySupported: Boolean): GenericRecognition {
            return GenericRecognition(isFullySupported, object: RecognizerProvider() {
                override fun createRecognizers() = listOf(DocumentFaceRecognizer(),
                        buildId1CardDetectorRecognizer())
            })
        }
    }
}
