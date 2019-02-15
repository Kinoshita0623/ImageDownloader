package com.example.imagedownloader.main

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.imagedownloader.R
import com.example.imagedownloader.data.ImageData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageListAdapter(private val context: Context, private val layoutId: Int, private val imageDataList: List<ImageData>) : BaseAdapter() {

    data class ViewHolder(val imageView: ImageView, val textView: TextView)

    private var inflater = LayoutInflater.from(context)


    override fun getCount(): Int {
        return imageDataList.size
    }

    override fun getItem(position: Int): Any {
        return imageDataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertViewTmp: View?, parent: ViewGroup?): View {
        var view = convertViewTmp
        val holder: ViewHolder

        if(view == null){
            view = inflater.inflate(layoutId, null)

            holder = ViewHolder(
                view.findViewById(R.id.imageView),
                view.findViewById(R.id.imageDescription)
            )
            view.tag = holder
        }else{
            holder = view.tag as ViewHolder
        }

        val handler = Handler()

        try{
            GlobalScope.launch {
                val bitmap = imageDataList[position].asyncBitmap.await()
                val height = bitmap.height
                val width = bitmap.width
                val byte = bitmap.byteCount

                val viewText = "height:$height, width:$width, byteSize:$byte"

                handler.post{
                    holder.imageView.setImageBitmap(bitmap)
                    holder.textView.text = viewText
                }

            }

        }catch(e: Exception){
            throw e
        }

        val ch: CheckBox = view!!.findViewById(R.id.checkBox)
        ch.setOnCheckedChangeListener { _, isChecked ->
            //CheckListなどに保存する
            imageDataList[position].isSave = isChecked
        }
        ch.isChecked = imageDataList[position].isSave

        return view

    }

}