package com.example.math.wolfram.Fetchers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

class SimpleAPIFetcher: CoroutineScope {
    private val TAG = "SIMPLE API FETCHER"
    private val BASE_URL = "http://api.wolframalpha.com/v1/simple"
    private val APPID = "5668XV-H686HUWJH5" //YHURPP-9EH9LW9L3Q
    private val BACKGROUND = "F5F5F5"
    private val query: String? = null

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    @Throws(IOException::class)
    private fun getUrlBitmap(urlSpec: String): Bitmap {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        return try {
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            val `in` = connection.inputStream
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(
                    connection.responseMessage +
                            ": with " +
                            urlSpec
                )
            }
            BitmapFactory.decodeStream(`in`)
        } finally {
            connection.disconnect()
        }
    }

    fun fetchRequestOnIOThread(onPreExecute: () -> String?,onPostExecute: (Bitmap?) -> Unit) = launch(Dispatchers.Main) {
        val query = onPreExecute()
        val result: Bitmap? = withContext(Dispatchers.IO){
            fetchRequest(query)
        }
        onPostExecute(result)
    }

    private fun fetchRequest(query: String?): Bitmap? {
        try {
            val url = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("appid", APPID)
                .appendQueryParameter("i", query)
                .appendQueryParameter("background", BACKGROUND)
                .build().toString()
            Log.i(TAG, "String url: $url")
            return getUrlBitmap(url)
        } catch (ex: IOException) {
            Log.e(TAG, "failed to fetch items", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, ex.message, ex)
        }
        return null
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}