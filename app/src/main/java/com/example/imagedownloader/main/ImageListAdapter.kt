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

    data class ViewHolder(val checkBox: CheckBox, val imageView: ImageView, val textView: TextView)

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
        var convertView = convertViewTmp
        val holder: ViewHolder

        if(convertView == null){
            convertView = inflater.inflate(layoutId, null)

            //FIXME 関係のないチェックボックスにチェックが入ってしまう不具合がある
            val ch: CheckBox = convertView!!.findViewById(R.id.checkBox)
            ch.setOnCheckedChangeListener { buttonView, isChecked ->
                //CheckListなどに保存する
                imageDataList[position].isSave = isChecked
                Log.d("SELECTED IMG", imageDataList[position].imageURL)
            }
            holder = ViewHolder(
                ch,
                convertView.findViewById(R.id.imageView),
                convertView.findViewById(R.id.imageDescription)
            )
            convertView.tag = holder
        }else{
            holder = convertViewTmp!!.tag as ViewHolder
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
        return convertView

    }

}