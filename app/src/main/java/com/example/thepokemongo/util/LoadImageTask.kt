package com.example.thepokemongo.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.IOException

class LoadImageTask(val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {


    override fun doInBackground(vararg p0: String?): Bitmap {
        var urlLink = p0[0]
        var bitmap : Bitmap? = null

        try {
            var inputStream = java.net.URL(urlLink).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch ( e: IOException){
            e.printStackTrace()
        }


        return bitmap!!
    }

    override fun onPostExecute(result: Bitmap?) {
        imageView.setImageBitmap(result)
    }
}