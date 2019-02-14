package com.example.imagedownloader.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RemoteImageDownloader{

    fun getAsyncImageFromList(urlList: List<String>): List<ImageData>{
        return urlList.map{
            ImageData(it, getAsyncImage(it), false)
        }
    }

    private fun getAsyncImage(url: String): Deferred<Bitmap> {
        return GlobalScope.async{
            try{
                val imgUrl = URL(url)
                val connection = if(url.startsWith("http://")){
                    imgUrl.openConnection() as HttpURLConnection
                }else{
                    imgUrl.openConnection() as HttpsURLConnection
                }

                connection.requestMethod = "GET"
                connection.connect()
                val stream = connection.inputStream
                BitmapFactory.decodeStream(stream)
            }catch(e: Exception){
                Log.e("IMG_DOWNLOAD", "ERROR発生", e)
                throw e
            }
        }
    }


}