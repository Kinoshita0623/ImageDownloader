package com.example.imagedownloader.main

import android.webkit.WebView
import com.example.imagedownloader.BasePresenter
import com.example.imagedownloader.BaseView
import com.example.imagedownloader.data.ImageData

interface MainContract {
    interface View : BaseView<Presenter> {
        fun showWebView()
        fun showImageList(imgList: List<ImageData>)
        fun showDetailedImage()
        fun whenSavedImage()
        fun showMainView()


    }

    interface Presenter : BasePresenter {
        fun loadWebView(url: String, webView: WebView)
        fun getImage(url: String)
        fun saveImage()
        fun getImageForList(urlList: List<String>)
        fun setImageDataList(imgDataList: List<ImageData>)

    }
}