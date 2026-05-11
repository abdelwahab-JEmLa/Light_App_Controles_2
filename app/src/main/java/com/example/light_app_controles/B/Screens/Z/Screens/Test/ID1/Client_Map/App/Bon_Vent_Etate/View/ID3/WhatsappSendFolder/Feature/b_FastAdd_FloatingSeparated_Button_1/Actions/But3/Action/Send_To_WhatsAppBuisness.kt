package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID3.WhatsappSendFolder.Feature.b_FastAdd_FloatingSeparated_Button_1.Actions.But3.Action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

interface Send_To_WB_Int {
    fun getFileUris(context: Context, folder: File, extension: String): List<Uri>
    fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit)
    fun formatPhoneForWhatsApp(raw: String): String
}

class Send_To_WB_Impl : Send_To_WB_Int {

    override fun getFileUris(context: Context, folder: File, extension: String): List<Uri> {
        if (!folder.exists() || !folder.isDirectory) return emptyList()
        return (folder.listFiles()
            ?.filter { it.extension.equals(extension, ignoreCase = true) }
            ?.sortedBy { it.name }
            ?: emptyList())
            .map { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }
    }

    override fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit) {
        sendImgsViaWhatsAppBusiness(context, phoneNumber, getFileUris(context, folder, extension), clientName, onResult)
    }

    fun sendImgsViaWhatsAppBusiness(context: Context, phoneNumber: String, imageUris: List<Uri>, clientName: String, onResult: () -> Unit) {
        try {
            if (imageUris.isEmpty()) {
                Toast.makeText(context, "Aucune image à envoyer", Toast.LENGTH_SHORT).show()
                onResult(); return
            }
            val jid = "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net"
            val intent = if (imageUris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b")
                    putExtra(Intent.EXTRA_STREAM, imageUris.first()); putExtra("jid", jid)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b")
                    putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        kotlin.collections.ArrayList(imageUris)
                    ); putExtra("jid", jid)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(intent)
            Toast.makeText(context, "Ouverture WhatsApp Business pour $clientName (${imageUris.size} image${if (imageUris.size > 1) "s" else ""})", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp Business non installé ou erreur: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            onResult()
        }
    }

    override fun formatPhoneForWhatsApp(raw: String): String {
        var c = raw.replace(Regex("[^0-9]"), "")
        if (!c.startsWith("213")) { if (c.startsWith("0")) c = c.drop(1); c = " 213$c" }
        return c
    }
}
