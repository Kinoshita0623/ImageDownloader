package com.example.imagedownloader.main

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.example.imagedownloader.R
import com.example.imagedownloader.data.ImageData

class MainFragment : Fragment(), MainContract.View {

    private lateinit var mPresenter: MainContract.Presenter

    private lateinit var loadUrlButton: Button
    private lateinit var pasteButton: Button
    private lateinit var inputUrlBox: EditText
    private lateinit var imageViewList: ListView
    private lateinit var inputInterfaceLayout: LinearLayout
    private lateinit var buttonLayoutView: LinearLayout

    private lateinit var webView: WebView
    private lateinit var cancelButton: Button

    private lateinit var saveButton: Button

    override fun setPresenter(presenter: MainContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUrlButton = view.findViewById(R.id.loadURLButton)
        pasteButton = view.findViewById(R.id.pasteButton)
        inputUrlBox = view.findViewById(R.id.inputURL)
        imageViewList = view.findViewById(R.id.imageListView)
        inputInterfaceLayout = view.findViewById(R.id.inputInterfaceLayout)
        buttonLayoutView = view.findViewById(R.id.buttonLayoutView)
        webView = view.findViewById(R.id.webView)
        cancelButton = view.findViewById(R.id.cancelButton)

        saveButton = view.findViewById(R.id.saveButton)

        loadUrlButton.setOnClickListener{
            val inputUrl = inputUrlBox.text.toString()
            mPresenter.loadWebView(inputUrl, webView)
        }

        //WebViewをキャンセルする
        cancelButton.setOnClickListener{
            visibleMainView()
        }

        pasteButton.setOnClickListener{
            inputUrlBox.setText(getClipboardText())
        }

        saveButton.setOnClickListener{
            mPresenter.saveImage()
        }
    }

    private fun visibleWebView(){
        webView.visibility = View.VISIBLE
        imageViewList.visibility = View.GONE
        inputInterfaceLayout.visibility = View.GONE
        buttonLayoutView.visibility = View.GONE
        saveButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
    }

    private fun visibleMainView(){
        webView.visibility = View.GONE
        imageViewList.visibility = View.VISIBLE
        inputInterfaceLayout.visibility = View.VISIBLE
        buttonLayoutView.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
        cancelButton.visibility = View.GONE
    }

    private fun getClipboardText():String{
        val cm = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return cm.primaryClip.getItemAt(0).text.toString()
    }

    override fun showMainView() {
        visibleMainView()
    }

    //ListViewにBitmapを取得するJob（Deferred）なリストを渡す
    override fun showImageList(imgList: List<ImageData>) {
        imageViewList.adapter = ImageListAdapter(
            activity!!.applicationContext,
            R.layout.image_list_view,
            imgList
        )
        mPresenter.setImageDataList(imgList)
    }

    override fun showDetailedImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showWebView() {
        visibleWebView()

    }

    override fun showResultTransaction(msg: String) {
        Toast.makeText(activity!!.applicationContext, msg, Toast.LENGTH_LONG).show()
    }


}