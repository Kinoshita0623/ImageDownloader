package com.example.imagedownloader.data

import android.graphics.Bitmap
import kotlinx.coroutines.Deferred

data class ImageData(val imageURL: String, val asyncBitmap: Deferred<Bitmap>, var isSave:Boolean)