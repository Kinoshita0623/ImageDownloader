package com.example.imagedownloader.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
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


    suspend fun saveImage(asyncBitmap: Deferred<Bitmap>, fileName: String): Boolean {
        val mDCIMPath = getDCIMPathString()
        val file = checkIfNotDirectoryMakeIt(mDCIMPath, "ImageDownloader", fileName)
        val outputStream = FileOutputStream(file)
        return withContext(Dispatchers.Default) {
            val bitmap = asyncBitmap.await()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        }

    }


    fun saveImageForList(asyncBitmapList: List<Deferred<Bitmap>>): List<Deferred<Boolean>>{
        var nameCounter = 0
        return asyncBitmapList.map{
            GlobalScope.async{
                val fileName = "${nameMaker()}$nameCounter.jpg"

                nameCounter++
                saveImage(it,fileName)
            }

        }
    }

    private fun nameMaker(): String{
        val c = Calendar.getInstance()
        return "${c[Calendar.YEAR]}_${c[Calendar.MONTH] + 1}_${c[Calendar.DAY_OF_MONTH]}_${c[Calendar.HOUR_OF_DAY]}_${c[Calendar.MINUTE]}${c[Calendar.SECOND]}_${c[Calendar.MILLISECOND]}"
    }


    private fun checkIfNotDirectoryMakeIt(basePath: String, directory: String, fileName:String): File {
        val path = "$basePath/$directory"
        val file = File(path)
        if(!file.exists()){
            file.mkdir()
        }
        return File("$path/$fileName")
    }

    private fun getDCIMPathString(): String{
        val extDir = Environment.getExternalStorageDirectory()
        return "${extDir.absolutePath}/${Environment.DIRECTORY_DCIM}"
    }




}