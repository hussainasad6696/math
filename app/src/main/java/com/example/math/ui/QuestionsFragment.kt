package com.example.math.ui

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.math.MainActivity
import com.example.math.R
import com.example.math.adapter.StepsAnswerAdapter
import com.example.math.databinding.FragmentQuestionsBinding
import com.example.math.interfaces.CloseFragmentInterface
import com.example.math.interfaces.TextTranslationInterface
import com.example.math.models.QuestionTypes
import com.example.math.models.QuestionsItem
import com.example.math.models.StepAnswerModel
import com.example.math.util.ImageDetectionUtils
import com.google.firebase.functions.FirebaseFunctions
import java.lang.Exception


class QuestionsFragment(
    private val functions: FirebaseFunctions,
    private val question: QuestionsItem,
    private val questionTypes: QuestionTypes
) : Fragment(), TextTranslationInterface {

    private lateinit var binding: FragmentQuestionsBinding
    private lateinit var adapter: StepsAnswerAdapter
    private lateinit var imageDetectionUtils: ImageDetectionUtils
    private var answerTries = 3
    private lateinit var mainActivity: MainActivity
    private lateinit var closeFragmentInterface: CloseFragmentInterface
    private lateinit var currentStepAnswerModel: StepAnswerModel
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data!!.extras.also { bundle ->
                    bundle!!.get("data").also { bitmap ->
                        imageDetectionUtils.runTextRecognition(bitmap = bitmap as Bitmap)
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_questions, container, false)

        imageDetectionUtils = ImageDetectionUtils()
        imageDetectionUtils.initFirebaseFunction(functions)
        imageDetectionUtils.withTextTranslationFromImageListener(this)

        initViews()

        return binding.root
    }

    private fun initViews() {
        mainActivity = activity as MainActivity
        closeFragmentInterface = mainActivity.closeFragmentInterface()
        adapter = StepsAnswerAdapter(requireContext()) {
            onCameraFabClicked(it)
        }
        binding.answerStepsRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.answerStepsRecyclerview.adapter = this.adapter

        binding.question.text = question.question
        try {
            question.steps.forEachIndexed { index, steps ->
                adapter.addDataToList(
                    StepAnswerModel(
                        index = index+1,
                        operandOne = if(questionTypes != QuestionTypes.QUADRATIC && questionTypes != QuestionTypes.LCM
                            && questionTypes != QuestionTypes.MIX) readOperands(steps, "before") else steps,
                        operandTwo = if(questionTypes != QuestionTypes.QUADRATIC && questionTypes != QuestionTypes.LCM
                            && questionTypes != QuestionTypes.MIX) readOperands(steps, "after") else "",
                        operator = readOperands(steps, ""),
                        detail = question.detail,
                        result = question.results[index],
                        hint = question.hint[index],
                        viewType =  0
                    )
                )
            }
        }catch (e: Exception){
            Log.e("TAG", "initViews: $e")
        }


        adapter.addDataToList(
            StepAnswerModel(
                index = adapter.listSize,
                operandOne = "",
                operandTwo = "",
                operator = "",
                detail = "",
                result = "",
                hint = "",
                viewType =  1
            )
        )

        binding.cameraFab.setOnClickListener {

        }

        binding.backPressed.setOnClickListener {
            closeFragmentInterface.closeFragmentListener(questionTypes)
        }
    }

    private fun onCameraFabClicked(it: StepAnswerModel) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
        currentStepAnswerModel = it
    }

    private fun readOperands(stepsData: String, subStringFor: String): String {
        val operatorArray = arrayOf("+", "-", "*", "/",",")
        var stringData = ""
        operatorArray.forEach {
            if (stepsData.contains(it)) {
                stringData = when (subStringFor) {
                    "before" -> stepsData.substringBefore(it)
                    "after" -> stepsData.substringAfter(it)
                    else -> it
                }
            }
        }
        return stringData
    }

//    private fun onSuccessDialog(answer: String, index: Int) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Good work")
//            .setMessage("$answer is the correct answer.")
//            .setPositiveButton("Next ->") { dialog, _ ->
//                if (adapter.listSize > index) {
//                    adapter.currentIndex(1)
//                    binding.answerStepsRecyclerview.smoothScrollToPosition(index + 1)
//                }
//                else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Congratulation you have completed the question",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    closeFragmentInterface.closeFragmentListener(questionTypes)
//                }
//                dialog.dismiss()
//            }.setIcon(R.drawable.ic_baseline_navigate_next_24)
//            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.show()
//    }
//
    private fun onSuccessDialog(answer: String, index: Int){
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.verification, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        dialogView.findViewById<TextView>(R.id.title).text = "Good work"
        dialogView.findViewById<TextView>(R.id.text).text = "$answer is the correct answer."
        dialogView.findViewById<TextView>(R.id.next).setOnClickListener {
            if (adapter.listSize > index) {
                adapter.currentIndex(1)
                binding.answerStepsRecyclerview.smoothScrollToPosition(index + 1)
            }
            else {
                Toast.makeText(
                    requireContext(),
                    "Congratulation you have completed the question",
                    Toast.LENGTH_LONG
                ).show()
                closeFragmentInterface.closeFragmentListener(questionTypes)
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onSuccessTextRecognitionListener(text: String) {
            val index = adapter.currentIndex()
            val s = if (!::currentStepAnswerModel.isInitialized) question.results[index] else currentStepAnswerModel.result
            if (text.contains(s)) {
                answerTries = 3
                onSuccessDialog(index = index, answer = s)
            } else {
                answerTries--
                Toast.makeText(requireContext(), "Wrong answer please try again", Toast.LENGTH_LONG)
                    .show()
                if (answerTries == 0) {
                    answerDialog(s)
                }
            }
    }

    private fun answerDialog(answer: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Better luck next time")
            .setMessage("The correct is $answer.")
            .setPositiveButton("Close") { dialog, _ ->
                binding.answerStepsRecyclerview.smoothScrollToPosition(adapter.currentIndex() + 1)
                dialog.dismiss()
            }.setIcon(R.drawable.ic_baseline_navigate_next_24)
            .show()
    }

    override fun onFilerTextRecognitionListener(text: String) {
        Toast.makeText(requireContext(), "Error: $text", Toast.LENGTH_SHORT).show()
    }

}