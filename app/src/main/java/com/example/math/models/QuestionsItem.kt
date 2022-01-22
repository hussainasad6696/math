package com.example.math.models

data class QuestionsItem(
    val detail: String,
    val question: String,
    val questionType: String,
    val results: List<String>,
    val steps: List<String>
)