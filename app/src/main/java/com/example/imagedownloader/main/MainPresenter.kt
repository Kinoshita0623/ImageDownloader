package com.example.imagedownloader.main

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.imagedownloader.data.ImageData
import com.example.imagedownloader.data.RemoteImageDownloader

class MainPresenter(private val mainView: MainContract.View) :
    MainContract.Presenter {

    val handler = Handler()
    private var imageDataList: List<ImageData>? = null

    companion object{
        const val JAVASCRIPT_INTERFACE_NAME = "ImageLoader"
        const val LOAD_IMAGE_JS_CODE = "javascript:\n" +
                "   var urlString = \"\";\n" +
                "   var imgList = document.getElementsByTagName(\"img\");\n" +
                "   for(var i = 0; i< imgList.length; i++){\n" +
                "        urlString += imgList[i].getAttribute(\"src\") + \",\";\n" +
                "   }\n" +
                "   ImageLoader.getImgUrl(urlString);"
    }
    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImage(url: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun loadWebView(url: String, webView: WebView) {
        mainView.showWebView()

        webView.webViewClient = ImageLoadWebClient()
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(ImageLoaderJavaScriptInterface(),
            JAVASCRIPT_INTERFACE_NAME
        )

    }

    override fun setImageDataList(imgDataList: List<ImageData>) {
        imageDataList = imgDataList
    }

    //ImageDataListのチェックボックスから得たBoolean値からTrueのデータだけ保存する
    override fun saveImage() {
        if(imageDataList != null){
            imageDataList?.
                filter{it.isSave }?.
                forEach{
                    Log.d("SELECTED IMAGE", it.imageURL)
                }
        }

    }

    //ModelでBitmapImageを取得するCoroutinesのJob(Deferred)を取得しそれをshowImageListメソッドに渡す
    override fun getImageForList(urlList: List<String>) {
        val remoteImage = RemoteImageDownloader()
        val asyncBitmap = remoteImage.getAsyncImageFromList(urlList)
        mainView.showImageList(asyncBitmap)
    }

    inner class ImageLoadWebClient : WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            view?.loadUrl(LOAD_IMAGE_JS_CODE)
        }
    }

    inner class ImageLoaderJavaScriptInterface{
        @JavascriptInterface
        fun getImgUrl(imgUrl: String?){
            if(imgUrl.isNullOrBlank()){
                return
            }
            val urlList = imgUrl.split(",").filter{
                domainCheck(it)
            }
            if(urlList.isNotEmpty()){
                Log.d("ImageURL", urlList.toString())
                handler.post{

                    mainView.showMainView()

                    //Model.getBitmapImage(imgUrl)
                    //ModelでBitmapImageを取得するCoroutinesのJob(Deferred)を取得しそれをshowImageListメソッドに渡す
                    getImageForList(urlList)
                }

            }

        }

        private fun domainCheck(url: String?):Boolean{
            //Log.d("URL", url)
            return when{
                url == null -> false
                url.startsWith("https://pbs.twimg.com") && url.endsWith(".jpg") -> true
                url.startsWith("https://instagram") && url.endsWith(".net") -> true
                url.endsWith(".jpg") -> true
                url.endsWith(".png") -> true
                url.startsWith("https://encrypted") -> true
                url.startsWith("data:image") -> false
                url.startsWith("http://") || url.startsWith("https://") -> true
                else -> false
            }
        }

    }


}