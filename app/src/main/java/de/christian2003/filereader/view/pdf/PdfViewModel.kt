package de.christian2003.filereader.view.pdf

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.filereader.model.FileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var fileInfo: FileInfo

    var isLoading: Boolean by mutableStateOf(false)

    var fileName: String by mutableStateOf("")

    var currentPage: Int by mutableIntStateOf(0)

    var pageCount: Int by mutableStateOf(0)

    var pageBitmap: Bitmap? by mutableStateOf(null)

    var scale: Float by mutableFloatStateOf(1f)

    var offset: Offset by mutableStateOf(Offset(0f, 0f))


    fun init(fileInfo: FileInfo) {
        this.fileInfo = fileInfo
        loadInitialPage()
    }


    fun openPage(pageIndex: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (isLoading) {
            return@launch
        }
        isLoading = true
        if (pageIndex < 0 || pageIndex > pageCount - 1) {
            isLoading = false
            return@launch
        }

        val context: Context = getApplication<Application>().baseContext

        val pdfRenderer: PdfRenderer? = getPdfRenderer(context)
        if (pdfRenderer != null) {
            val page = pdfRenderer.openPage(pageIndex)

            val bitmap = Bitmap.createBitmap((page.width * scale).toInt(), (page.height * scale).toInt(), Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            currentPage = pageIndex
            this@PdfViewModel.pageBitmap = bitmap
            pdfRenderer.close()
        }

        isLoading = false
    }


    private fun loadInitialPage() = viewModelScope.launch(Dispatchers.IO) {
        if (isLoading) {
            return@launch;
        }
        isLoading = true

        val context: Context = getApplication<Application>().baseContext
        fileName = fileInfo.getFileName(context)

        val pdfRenderer: PdfRenderer? = getPdfRenderer(context)
        if (pdfRenderer != null) {
            pageCount = pdfRenderer.pageCount
            val page = pdfRenderer.openPage(0)

            val bitmap = Bitmap.createBitmap((page.width * scale).toInt(), (page.height * scale).toInt(), Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()

            currentPage = 0
            this@PdfViewModel.pageBitmap = bitmap
        }

        isLoading = false
    }


    private fun getPdfRenderer(context: Context): PdfRenderer? {
        val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(fileInfo.uri, "r")
        return if (parcelFileDescriptor != null) {
            PdfRenderer(parcelFileDescriptor)
        } else {
            null
        }
    }

}
