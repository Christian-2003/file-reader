package de.christian2003.filereader.view.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.filereader.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(
    viewModel: PdfViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewModel.fileName,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.openPage(viewModel.currentPage - 1)
                            },
                            enabled = viewModel.currentPage > 0
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                tint = if (viewModel.currentPage > 0) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(0.5f)
                                },
                                contentDescription = ""
                            )
                        }

                        Text(
                            text = stringResource(R.string.pdf_page)
                                .replace("{page}", "" + (viewModel.currentPage + 1))
                                .replace("{max}", "" + viewModel.pageCount),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        IconButton(
                            onClick = {
                                viewModel.openPage(viewModel.currentPage + 1)
                            },
                            enabled = viewModel.currentPage < viewModel.pageCount - 1
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_next),
                                tint = if (viewModel.currentPage < viewModel.pageCount - 1) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(0.5f)
                                },
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (viewModel.pageBitmap != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            viewModel.scale = (viewModel.scale * zoom).coerceIn(1f, 3f)
                            viewModel.offset = Offset(viewModel.offset.x + pan.x, viewModel.offset.y + pan.y)
                        }
                    }
                    .graphicsLayer(
                        scaleX = viewModel.scale,
                        scaleY = viewModel.scale,
                        translationX = viewModel.offset.x,
                        translationY = viewModel.offset.y
                    )
            ) {
                LaunchedEffect(viewModel.scale) {
                    viewModel.openPage(viewModel.currentPage)
                }
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.extraSmall)
                ) {
                    Image(
                        bitmap = viewModel.pageBitmap!!.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .background(Color.White)
                    )
                }
            }
        }
    }
}
