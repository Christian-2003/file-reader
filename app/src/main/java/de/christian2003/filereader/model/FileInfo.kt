package de.christian2003.filereader.model

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

class FileInfo(

    val uri: Uri

) {

    private var fileName: String? = null


    fun getFileName(context: Context): String {
        if (fileName == null) {
            var result: String? = null

            if (uri.scheme.equals("content")) {
                val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
                cursor.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        result = cursor.getString(index)
                    }
                }
            }

            if (result == null) {
                result = uri.path
                val cutIndex = result!!.lastIndexOf('/')
                if (cutIndex != -1) {
                    result = result!!.substring(cutIndex + 1)
                }
            }

            fileName = result
        }
        return fileName!!
    }

}
