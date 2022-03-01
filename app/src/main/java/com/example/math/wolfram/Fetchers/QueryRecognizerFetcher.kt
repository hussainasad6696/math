package com.example.math.wolfram.Fetchers

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/** Public class QueryRecognizerFetcher
 * is used to check if query entered by the user may be recognized by
 * the Wolfram Alpha compute engine. Using API: (Fast Query Recognizer)
 * more info on this API: https://products.wolframalpha.com/query-recognizer/documentation/
 */
class QueryRecognizerFetcher {
    private val TAG = "QUERY RECOGNIZER"
    private val BASE_URL = "https://www.wolframalpha.com/queryrecognizer/query.jsp"
    private val APPID = "5668XV-H686HUWJH5" //YHURPP-9EH9LW9L3Q
    private val MODE = "Default"
    private val OUTPUT = "json"
    private val ARRAY = "query"
    private val VALUE = "accepted"

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    @Throws(IOException::class)
    private fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpsURLConnection
        return try {
            val out = ByteArrayOutputStream()
            val `in` = connection.inputStream
            if (connection.responseCode != HttpsURLConnection.HTTP_OK) {
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

    fun fetchRequest(query: String?): Boolean {
        try {
            val url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("mode", MODE)
                .appendQueryParameter("i", query)
                .appendQueryParameter("appid", APPID)
                .appendQueryParameter("output", OUTPUT)
                .build().toString()
            Log.i(TAG, "String JSON: $url")
            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: $jsonString")
            val jsonBody = JSONObject(jsonString)
            return parseJson(jsonBody)
        } catch (ex: IOException) {
            Log.e(TAG, "failed to fetch items", ex)
        } catch (ex: JSONException) {
            Log.e(TAG, "failed to parse JSON", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, ex.message, ex)
        }
        return false
    }

    @Throws(JSONException::class)
    private fun parseJson(jsonBody: JSONObject): Boolean {
        val queryArray = jsonBody.getJSONArray(ARRAY)
        require(queryArray.length() <= 1) { "too many objects retrieved" }
        val query = queryArray.getJSONObject(0)
        return query.getBoolean(VALUE)
    }
}