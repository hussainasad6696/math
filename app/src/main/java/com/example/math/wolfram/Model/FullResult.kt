package com.example.math.wolfram.Model

import java.io.Serializable

class FullResult(var title: String, var value: String, var image_src: String) : Serializable {
    var src_width_px = 0.0
    var src_height_px = 0.0
    override fun toString(): String {
        return """
               ${title}: ${value}.
               
               """.trimIndent()
    }
}