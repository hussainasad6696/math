package com.example.math.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.math.R
import com.example.math.models.StepAnswerModel
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class StepsAnswerAdapter(private val context: Context): RecyclerView.Adapter<StepsAnswerAdapter.ViewHolder>() {

    private val list = ArrayList<StepAnswerModel>()
    private var currentIndex = 0

    fun currentIndex(): Int {
        return currentIndex
    }

    fun currentIndex(index: Int){
        currentIndex += index
    }

    fun addDataToList(stepAnswerModel: StepAnswerModel){
        list.add(stepAnswerModel)
        notifyItemChanged(list.size - 1)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val stepNumber: TextView = view.findViewById(R.id.stepNumber)
        private val detail: TextView = view.findViewById(R.id.detail)
        private val operandOne: TextView = view.findViewById(R.id.operandOne)
        private val operandTwo: TextView = view.findViewById(R.id.operandTwo)
        private val operator: TextView = view.findViewById(R.id.operator)
        fun bind(stepAnswerModel: StepAnswerModel) {
            stepNumber.text = stepAnswerModel.index.toString()
            detail.text = stepAnswerModel.detail
            operandOne.text = stepAnswerModel.operandOne
            operandTwo.text = stepAnswerModel.operandTwo
            operator.text = stepAnswerModel.operator
        }
    }

    val listSize: Int
    get() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.steps_answer_view,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

}