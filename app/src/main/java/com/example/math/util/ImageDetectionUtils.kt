package com.example.math.util

import android.graphics.Bitmap
import android.util.Log
import com.example.math.R
import com.example.math.interfaces.TextTranslationInterface
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

class ImageDetectionUtils {

    private lateinit var functions: FirebaseFunctions
    private lateinit var textTranslationInterface: TextTranslationInterface

    fun withTextTranslationFromImageListener(textTranslationInterface: TextTranslationInterface) {
        this.textTranslationInterface = textTranslationInterface
    }

    fun initFirebaseFunction(functions: FirebaseFunctions) {
        this.functions = functions
    }

    fun runTextRecognition(bitmap: Bitmap) {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { p0 ->
                processTextRecognitionResult(p0!!)
                textTranslationInterface.onSuccessTextRecognitionListener(p0.text)
            }.addOnFailureListener { e ->
                textTranslationInterface.onFilerTextRecognitionListener(e.toString())
            }
    }


    private fun processTextRecognitionResult(firebaseVisionText: FirebaseVisionText) {
        if (firebaseVisionText.textBlocks.size == 0) {
            Log.e("TAG", "processTextRecognitionResult: ${R.string.error_not_found}")
        }
        for (block in firebaseVisionText.textBlocks) {
            Log.e("TAG", "processTextRecognitionResult: ${block.text}")
            //In case you want to extract each line
            /*
			for (FirebaseVisionText.Line line: block.getLines()) {
				for (FirebaseVisionText.Element element: line.getElements()) {
					mTextView.append(element.getText() + " ");
				}
			}
			*/
        }
    }
}