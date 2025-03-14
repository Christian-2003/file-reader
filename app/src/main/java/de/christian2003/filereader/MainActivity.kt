package de.christian2003.filereader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christian2003.filereader.model.FileInfo
import de.christian2003.filereader.ui.theme.FileReaderTheme
import de.christian2003.filereader.view.main.MainScreen
import de.christian2003.filereader.view.main.MainViewModel
import de.christian2003.filereader.view.text.TextScreen
import de.christian2003.filereader.view.text.TextViewModel
import de.christian2003.filereader.view.pdf.PdfScreen
import de.christian2003.filereader.view.pdf.PdfViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uri: Uri? = if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            intent.data
        } else {
            null
        }

        setContent {
            FileReader(
                uri = uri,
                finish = {
                    finish()
                }
            )
        }
    }
}

@Composable
fun FileReader(
    uri: Uri?,
    finish: () -> Unit
) {
    val context = LocalContext.current

    FileReaderTheme {
        if (uri == null) {
            val mainViewModel: MainViewModel = viewModel()
            mainViewModel.init()
            MainScreen(
                viewModel = mainViewModel,
                onNavigateUp = {
                    finish()
                }
            )
        }
        else {
            val fileInfo = FileInfo(uri)
            if (context.contentResolver.getType(uri) == "application/pdf") {
                val pdfViewModel: PdfViewModel = viewModel()
                pdfViewModel.init(fileInfo)
                PdfScreen(
                    viewModel = pdfViewModel,
                    onNavigateUp = {
                        finish()
                    }
                )
            }
            else {
                val textViewModel: TextViewModel = viewModel()
                textViewModel.init(fileInfo)
                TextScreen(
                    viewModel = textViewModel,
                    onNavigateUp = {
                        finish()
                    }
                )
            }
        }
    }
}
