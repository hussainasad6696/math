package com.example.math.interfaces

import com.example.math.models.QuestionTypes

interface CloseFragmentInterface {
    fun closeFragmentListener(questionType: QuestionTypes)
}