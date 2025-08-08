package com.concepts_and_quizzes.cds.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream

object ShareUtils {
    fun shareViewAsImage(context: Context, view: View, rect: Rect) {
        val fullBitmap = view.drawToBitmap()
        val pageBitmap = Bitmap.createBitmap(fullBitmap, rect.left, rect.top, rect.width(), rect.height())
        val dir = File(context.cacheDir, "composereports")
        dir.mkdirs()
        val file = File(dir, "report_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            pageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
