package com.example.light_app_controles.A.Main  // FIX: lowercase 'a' and 'main'

import EntreApps.Shared.Modules.Base.StoragePermissionDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember               // FIX: use remember, not rememberSaveable (AppDatabase is not Parcelable)
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
// FIX: updated import to match the corrected lowercase package name
import com.example.light_app_controles.B.Screens.MainScreen
import com.example.light_app_controles.Modules.Base.AppDatabase
import com.example.light_app_controles.Modules.PermissionHandler
import com.example.light_app_controles.ui.theme.Light_App_ControlesTheme
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    lateinit var content: () -> Unit
    private val permissionHandler by lazy { PermissionHandler(this) }
    private var permissionsChecked by mutableStateOf(false)
    private var showStorageDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityContent()
    }


    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        val hasStorageAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }

        if (hasStorageAccess && showStorageDialog) {
            showStorageDialog = false
            permissionsChecked = true
        }

        if (!permissionsChecked && hasStorageAccess) {
            handlePermissions()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(KoinExperimentalAPI::class)
    private fun setupActivityContent() {
        runCatching {
            setContent {
                var initDone by rememberSaveable { mutableStateOf(true) }

                val context = LocalContext.current

                // Initialized eagerly via the singleton — non-null, so the type
                // mismatch with MainScreen(appDatabase: AppDatabase) is resolved.
                val appDatabase by remember {
                    mutableStateOf(AppDatabase.DatabaseModule.getDatabase(context))
                }

                Light_App_ControlesTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        if (permissionsChecked) {
                            if (!initDone) {
                                // loading / init screen placeholder
                            } else {
                                MainScreen(
                                    appDatabase = appDatabase,
                                    name = "cc",
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            if (showStorageDialog) {
                                StoragePermissionDialog(
                                    onOpenSettings = { permissionHandler.openStorageSettings() },
                                    onDismiss = {
                                        showStorageDialog = false
                                        permissionsChecked = true
                                        showPermissionDeniedMessage()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            handlePermissions()
        }.onFailure {
            Toast.makeText(
                this,
                "Une erreur s'est produite. Veuillez redémarrer l'application.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handlePermissions() {
        if (permissionHandler.arePermissionsGranted()) {
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object :
            PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsChecked = true
                showStorageDialog = false
                Toast.makeText(
                    this@MainActivity,
                    "✓ جميع الأذونات ممنوحة - الصور ستظهر الآن",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPermissionsDenied() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    showStorageDialog = true
                    return
                }
                showPermissionDeniedMessage()
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {}
        })
    }

    private fun showPermissionDeniedMessage() {
        val message =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                "⚠ الصور لن تظهر بدون إذن الوصول للملفات. يمكنك منح الإذن من الإعدادات لاحقاً"
            } else {
                "⚠ بعض الوظائف محدودة بدون الأذونات الكاملة"
            }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
