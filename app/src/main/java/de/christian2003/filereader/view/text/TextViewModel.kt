package de.christian2003.filereader.view.text

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.filereader.model.FileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var fileInfo: FileInfo

    var isLoading: Boolean by mutableStateOf(true)

    var fileName: String by mutableStateOf("")

    var fileLines: List<String> by mutableStateOf(emptyList())


    fun init(fileInfo: FileInfo) {
        this.fileInfo = fileInfo
        loadFileContents()
    }


    private fun loadFileContents() = viewModelScope.launch(Dispatchers.IO) {
        isLoading = true
        val context = getApplication<Application>().baseContext
        val lines: MutableList<String> = mutableListOf()
        fileName = fileInfo.getFileName(context)
        context.contentResolver.openInputStream(fileInfo.uri)?.bufferedReader().use { reader ->
            reader?.forEachLine { line ->
                lines.add(line)
            }
            fileLines = lines.toList()
        }
        isLoading = false
    }

}
