package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_6.Action

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID3.WhatsappSendFolder.Feature.b_FastAdd_FloatingSeparated_Button_1.Actions.But3.Action.Send_To_WB_Impl
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID3.WhatsappSendFolder.Feature.b_FastAdd_FloatingSeparated_Button_1.Actions.But3.Action.Send_To_WB_Int
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ButtonID_10_Imgs_Send_whatsappBuisness_By_Folder(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    central_storage: String = "/storage/emulated/0/Download/",
    parent_folder: File = File(central_storage, ""),
    child: String = "",
    extantion_files_a_find: String = "webp",
    num: String = "",
    folder_path: File = File(parent_folder, child),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sender: Send_To_WB_Int = remember { Send_To_WB_Impl() }
    var isSending by remember { mutableStateOf(false) }
    var imagesExist by remember { mutableStateOf(false) }

    LaunchedEffect(folder_path.absolutePath) {
        withContext(Dispatchers.IO) {
            imagesExist = folder_path.exists() &&
                    folder_path.listFiles { f ->
                        f.extension.equals(
                            extantion_files_a_find,
                            ignoreCase = true
                        )
                    }?.isNotEmpty() == true
        }
    }

    val btnColor = if (isSending || !imagesExist) Color(0xFF9E9E9E) else Color(0xFF43A047)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = {
                if (!isSending && imagesExist) {
                    isSending = true
                    scope.launch {
                        sender.send(context, folder_path, extantion_files_a_find,
                            num,
                            folder_path.name) { isSending = false }
                    }
                }
            },
            modifier = modifier.size(56.dp),
            containerColor = btnColor,
        ) { Icon(Icons.Default.Image, null, tint = Color.White) }

        if (showLabels) {
            Text(
                text = when { isSending -> "Envoi…"; !imagesExist -> "Aucune image"; else -> "📁 ${folder_path.name}  📱 $num" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.background(btnColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
