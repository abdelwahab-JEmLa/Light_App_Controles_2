package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions.Buttons.Action

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions.normaliseToWaMeNumber

@Composable
fun DropdownItem_WhatsApp_Worker(
    currentNomWorker: String,
    currentNumWorker: String,
    isActive: Boolean,
    onActivate: () -> Unit,
    onWorkerSaved: (nomWorker: String, numWorker: String) -> Unit,
    onSend: (phoneNumber: String) -> Unit,
) {
    var editedNom by remember(currentNomWorker) { mutableStateOf(currentNomWorker) }
    var editedNum by remember(currentNumWorker) { mutableStateOf(currentNumWorker) }
    val focusRequesterNom = remember { FocusRequester() }
    val focusRequesterNum = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) focusRequesterNom.requestFocus()
        else {
            editedNom = currentNomWorker
            editedNum = currentNumWorker
        }
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color(0xFFFFA000), // amber — distinct from client items
                modifier = Modifier.Companion.size(22.dp),
            )
        },
        text = {
            if (isActive) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = editedNom,
                        onValueChange = { editedNom = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequesterNum.requestFocus() },
                        ),
                        label = {
                            Text("اسم العامل", style = MaterialTheme.typography.labelSmall)
                        },
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .focusRequester(focusRequesterNom),
                    )
                    OutlinedTextField(
                        value = editedNum,
                        onValueChange = { if (it.all { c -> c.isDigit() }) editedNum = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Companion.Phone,
                            imeAction = ImeAction.Companion.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onWorkerSaved(editedNom, editedNum)
                                onSend(normaliseToWaMeNumber(editedNum))
                            },
                        ),
                        label = {
                            Text("رقم العامل", style = MaterialTheme.typography.labelSmall)
                        },
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .focusRequester(focusRequesterNum),
                    )
                }
            } else {
                Column {
                    Text(
                        text = currentNomWorker.ifEmpty { "العامل" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Companion.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = currentNumWorker.ifEmpty { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = onActivate, modifier = Modifier.Companion.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "تعديل بيانات العامل",
                    modifier = Modifier.Companion.size(16.dp),
                )
            }
        },
        onClick = {
            if (currentNumWorker.isNotEmpty()) onSend(normaliseToWaMeNumber(currentNumWorker))
            else onActivate()
        },
    )
}
