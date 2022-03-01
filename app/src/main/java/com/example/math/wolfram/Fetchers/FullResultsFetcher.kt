package com.example.math.wolfram.Fetchers

import android.net.Uri
import android.util.Log
import com.example.math.wolfram.Model.FullResult
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class FullResultsFetcher {
    private val TAG = "FULL RESULTS"
    private val BASE_URL = "http://api.wolframalpha.com/v2/query"
    private val FORMAT = "plaintext"
    private val APPID = "5668XV-H686HUWJH5"
    private val OUTPUT = "json"
    private val QUERY_RESULT = "queryresult"
    private val ARRAY = "pods"
    private val SUBARRAY = "subpods"
    private val TITLE = "title"
    private val VALUE = "plaintext"
    private val IMAGE = "img"
    private val SRC = "src"
    private val HEIGHT = "height"
    private val WIDTH = "width"

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

    fun fetchRequest(query: String?): ArrayList<FullResult>? {
        try {
            val url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("appid", APPID)
                .appendQueryParameter("input", query) //.appendQueryParameter("format", FORMAT)
                .appendQueryParameter("output", OUTPUT)
                .build().toString()
            Log.i(TAG, "String JSON: $url")
            val jsonString = getUrlString(url)
            val jsonBody = JSONObject(jsonString)
            //JSONArray array = jsonBody.getJSONArray("pods");
            Log.i(TAG, "Received String from URL: $jsonString")
            return parseJson(jsonBody)
        } catch (ex: IOException) {
            Log.e(TAG, "failed to fetch items", ex)
        } catch (ex: JSONException) {
            Log.e(TAG, "failed to parse json", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, ex.message, ex)
        }
        return null
    }

    @Throws(JSONException::class, IOException::class)
    private fun parseJson(jsonBody: JSONObject): ArrayList<FullResult> {
        val results =
            ArrayList<FullResult>() // Here you create array of Strings or other objects in which u save data
        val queryResultJsonObject = jsonBody.getJSONObject(QUERY_RESULT) //"queryresult"
        val queryArray = queryResultJsonObject.getJSONArray(ARRAY) // "pods"
        for (i in 0 until queryArray.length()) {
            val jsonObject = queryArray.getJSONObject(i)
            val title =
                jsonObject.getString(TITLE) // thats how u can read a title of the info (Input Interpretation, Result, Location, etc.)
            if (title == "") // skips empty titles
                continue
            val jsonSubArray = jsonObject.getJSONArray(SUBARRAY) // "subpods"
            val jsonSubObject =
                jsonSubArray.getJSONObject(0) //the one and only jsonobject in subpods element
            val value = jsonSubObject.getString(VALUE) // "plaintext"    // you have the result here
            if (value == "") // skips empty results
                continue
            /** All of this is for image */
            val imageJsonObject = jsonSubObject.getJSONObject(IMAGE)
            val imageSource = imageJsonObject.getString(SRC)
            val imageHeightPX = imageJsonObject.getDouble(HEIGHT)
            val imageWidthPX = imageJsonObject.getDouble(WIDTH)
            /** This is my way of storing items  */
            val result = FullResult(title, value, imageSource)
            result.src_height_px = imageHeightPX
            result.src_width_px = imageWidthPX
            Log.i(TAG, result.image_src)
            results.add(result)
        }
        return results // here return the result (I recommend you create your own class Result which has String title and String result)
    }
}