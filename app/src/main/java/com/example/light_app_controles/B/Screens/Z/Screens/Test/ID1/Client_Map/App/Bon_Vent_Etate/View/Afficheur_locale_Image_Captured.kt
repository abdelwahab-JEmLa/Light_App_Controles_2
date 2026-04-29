package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun Afficheur_locale_Image_Captured(
    capturedBitmaps: List<Pair<ImageBitmap, String>>,
    onDismiss: () -> Unit,
    onSave: (List<Pair<Bitmap, String>>) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(0.98f),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الصور الملتقطة (${capturedBitmaps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (capturedBitmaps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "لا توجد صور",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(capturedBitmaps) { index, (bitmap, label) ->
                            var scale by remember { mutableFloatStateOf(1f) }
                            var offsetX by remember { mutableFloatStateOf(0f) }
                            var offsetY by remember { mutableFloatStateOf(0f) }
                            val transformState = rememberTransformableState { zoomChange, panChange, _ ->
                                scale = (scale * zoomChange).coerceIn(1f, 5f)
                                offsetX += panChange.x
                                offsetY += panChange.y
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                val displayLabel = label.substringAfterLast('_', label)
                                Text(
                                    text = "${index + 1}. $displayLabel",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )

                                Image(
                                    bitmap = bitmap,
                                    contentDescription = displayLabel,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .background(Color.Transparent)
                                        .transformable(transformState)
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                            translationX = offsetX
                                            translationY = offsetY
                                        },
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) { Text("إغلاق") }

                    Button(
                        onClick = {
                            val androidBitmaps = capturedBitmaps.map { (img, label) ->
                                img.asAndroidBitmap() to label
                            }
                            onSave(androidBitmaps)
                        }
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("حفظ الكل")
                    }
                }
            }
        }
    }
}
