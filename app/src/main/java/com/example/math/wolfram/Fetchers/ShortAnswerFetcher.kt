package com.example.math.wolfram.Fetchers

import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ShortAnswerFetcher {
    private val TAG = "SHORT ANSWER"
    private val BASE_URL = "http://api.wolframalpha.com/v1/result"
    private val APPID = "5668XV-H686HUWJH5" //YHURPP-9EH9LW9L3Q
    private val OUTPUT = "json"

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    @Throws(IOException::class)
    private fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        return try {
            val out = ByteArrayOutputStream()
            val `in` = connection.inputStream
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(
                    connection.responseMessage +
                            ": with " +
                            urlSpec
                )
            }
            var bytesRead = 0
            val buffer = ByteArray(1024)
            while (`in`.read(buffer).also { bytesRead = it } > 0) {
                out.write(buffer, 0, bytesRead)
            }
            out.close()
            out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    fun fetchRequest(query: String?): String? {
        try {
            val url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("appid", APPID)
                .appendQueryParameter("i", query)
                .appendQueryParameter("output", OUTPUT)
                .build().toString()
            Log.i(TAG, "String JSON: $url")
            val response = getUrlString(url)
            Log.i(TAG, "Received String from URL: $response")
            return response
        } catch (ex: IOException) {
            Log.e(TAG, "failed to fetch items", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, ex.message, ex)
        }
        return null
    }
}