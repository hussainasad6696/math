package com.example.math.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.math.R
import com.example.math.interfaces.OnQuestionClickListener
import com.example.math.models.QuestionsItem

class QuestionsAdapter(
    private val context: Context,
    private val onQuestionClickListener: OnQuestionClickListener
) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    private val list = ArrayList<QuestionsItem>()

    fun addDataToList(questionsItem: QuestionsItem) {
        list.add(questionsItem)
        notifyItemChanged(list.size - 1)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val question: TextView = view.findViewById(R.id.question)
        private val questionDetail: TextView = view.findViewById(R.id.questionDetail)
        fun bind(questionsItem: QuestionsItem, onQuestionClickListener: OnQuestionClickListener) {
            question.text = questionsItem.question
            questionDetail.text = questionsItem.detail

            itemView.setOnClickListener {
                onQuestionClickListener.onItemClickListener(questionsItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.custom_questions_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position],onQuestionClickListener)
    }

    override fun getItemCount(): Int = list.size
}