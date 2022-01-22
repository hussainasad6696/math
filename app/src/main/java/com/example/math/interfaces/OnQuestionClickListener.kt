package com.example.math.interfaces

import com.example.math.models.QuestionsItem

interface OnQuestionClickListener {
    fun onItemClickListener(question: QuestionsItem)
}