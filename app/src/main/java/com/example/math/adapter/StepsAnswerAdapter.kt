package com.example.math.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.math.R
import com.example.math.models.StepAnswerModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StepsAnswerAdapter(private val context: Context,private val onCameraFabClicked: (StepAnswerModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = ArrayList<StepAnswerModel>()
    private var currentIndex = 0

    fun currentIndex(): Int {
        return currentIndex
    }

    fun currentIndex(index: Int) {
        currentIndex += index
    }

    fun addDataToList(stepAnswerModel: StepAnswerModel) {
        list.add(stepAnswerModel)
        notifyItemChanged(list.size - 1)
    }

    class ViewHolderAllData(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.allData)
        fun bind(list: ArrayList<StepAnswerModel>) {
            list.forEach {
                textView.append("Step: ${it.index} \n")
                textView.append("${it.detail} \n")
                textView.append("${it.operandOne} ${it.operator} ${it.operandTwo} \n")
                if (it.result != "")
                    textView.append("Result: ${it.result} \n")
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val stepNumber: TextView = view.findViewById(R.id.stepNumber)
        private val detail: TextView = view.findViewById(R.id.detail)
        private val operandOne: TextView = view.findViewById(R.id.operandOne)
        private val operandTwo: TextView = view.findViewById(R.id.operandTwo)
        private val operator: TextView = view.findViewById(R.id.operator)
        private val cameraFab: FloatingActionButton = view.findViewById(R.id.cameraFab)
        fun bind(stepAnswerModel: StepAnswerModel, onCameraFabClicked: (StepAnswerModel) -> Unit) {
            stepNumber.text = stepAnswerModel.index.toString()
            detail.text = stepAnswerModel.detail
            operandOne.text = stepAnswerModel.operandOne
            operandTwo.text = stepAnswerModel.operandTwo
            operator.text = stepAnswerModel.operator

            if (stepAnswerModel.detail.contains("quadratic")) {
                operandOne.textSize = 12f
                operandTwo.textSize = 12f
            } else {
                operandOne.textSize = 24f
                operandTwo.textSize = 24f
            }

            cameraFab.setOnClickListener {
                onCameraFabClicked(stepAnswerModel)
            }
        }
    }

    val listSize: Int
        get() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.steps_answer_view, parent, false)
            )
            1 -> ViewHolderAllData(
                LayoutInflater.from(context).inflate(R.layout.view_holder_all_view, parent, false)
            )
            else -> ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.steps_answer_view, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> (holder as ViewHolder).bind(list[position],onCameraFabClicked)
            1 -> (holder as ViewHolderAllData).bind(list)
        }

    }

    override fun getItemCount(): Int = list.size

}