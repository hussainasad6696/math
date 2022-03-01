package com.example.math.wolfram.Model

import java.io.Serializable

class FullResultsWrapper(val results: ArrayList<FullResult>) : Serializable {
    val size: Int
        get() = results.size

    companion object {
        private const val serialVersionUID = 1L
    }
}