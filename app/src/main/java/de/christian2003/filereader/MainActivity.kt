package de.christian2003.filereader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.christian2003.filereader.model.FileInfo
import de.christian2003.filereader.ui.theme.FileReaderTheme
import de.christian2003.filereader.view.main.MainScreen
import de.christian2003.filereader.view.main.MainViewModel
import de.christian2003.filereader.view.text.TextScreen
import de.christian2003.filereader.view.text.TextViewModel
import de.christian2003.filereader.view.pdf.PdfScreen
import de.christian2003.filereader.view.pdf.PdfViewModel
import androidx.navigation.compose.composable
import de.christian2003.filereader.view.settings.SettingsScreen
import de.christian2003.filereader.view.settings.SettingsViewModel


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
                uri = uri
            )
        }
    }
}


@Composable
fun FileReader(
    uri: Uri?
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val startDestination: String = if (uri == null) {
        "main"
    }
    else {
        if (context.contentResolver.getType(uri) == "application/pdf") {
            "pdf"
        }
        else {
            "text"
        }
    }

    FileReaderTheme {
        NavHost(
            startDestination = startDestination,
            navController = navController
        ) {
            composable("main") {
                val mainViewModel: MainViewModel = viewModel()
                mainViewModel.init()

                MainScreen(
                    viewModel = mainViewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("pdf") {
                val fileInfo = FileInfo(uri!!)
                val pdfViewModel: PdfViewModel = viewModel()
                pdfViewModel.init(fileInfo)

                PdfScreen(
                    viewModel = pdfViewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("text") {
                val fileInfo = FileInfo(uri!!)
                val textViewModel: TextViewModel = viewModel()
                textViewModel.init(fileInfo)

                TextScreen(
                    viewModel = textViewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("settings") {
                val settingsViewModel: SettingsViewModel = viewModel()
                settingsViewModel.init();

                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
