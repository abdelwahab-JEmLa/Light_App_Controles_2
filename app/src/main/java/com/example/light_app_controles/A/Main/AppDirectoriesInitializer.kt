package com.example.light_app_controles.A.Main

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

object AppDirectoriesInitializer {
    private const val TAG = "AppDirectories"
    const val ROOT_DIR_NAME = "Abdelwahab_Tashil_Tahfid_Quran"
    const val VIDEOS_DIR_NAME = "Vidos_Tashile_Istikhdam"
    const val QR_IMAGES_DIR_NAME = "WhatsApp Qr Images"

    fun getAppRootDir(): File {
        val sdcard = File("/sdcard/$ROOT_DIR_NAME")
        return if (sdcard.exists() || sdcard.mkdirs()) {
            sdcard
        } else {
            File(Environment.getExternalStorageDirectory(), ROOT_DIR_NAME).apply { mkdirs() }
        }
    }

    fun getVideosDir(): File {
        return File(getAppRootDir(), VIDEOS_DIR_NAME).apply { if (!exists()) mkdirs() }
    }

    fun getQrImagesDir(): File {
        return File(getAppRootDir(), QR_IMAGES_DIR_NAME).apply { if (!exists()) mkdirs() }
    }

    fun ensureDirectoriesExist(context: Context? = null) {
        try {
            val root = getAppRootDir()
            val videos = getVideosDir()
            val qr = getQrImagesDir()

            Log.d(TAG, "Dossiers vérifiés/créés : root=${root.exists()}, videos=${videos.exists()}, qr=${qr.exists()}")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur création dossiers de l'application: ${e.message}", e)
        }
    }
}
