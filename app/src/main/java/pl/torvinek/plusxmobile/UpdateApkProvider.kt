package pl.torvinek.plusxmobile

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException

class UpdateApkProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String = "application/vnd.android.package-archive"

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        if (mode != "r") {
            throw FileNotFoundException("Unsupported mode")
        }
        val name = uri.lastPathSegment.orEmpty()
        if (!name.endsWith(".apk", ignoreCase = true) || name.contains("/") || name.contains("\\")) {
            throw FileNotFoundException("Invalid APK name")
        }
        val baseDir = File(context?.getExternalFilesDir(null), "updates").canonicalFile
        val target = File(baseDir, name).canonicalFile
        if (!target.path.startsWith(baseDir.path) || !target.exists()) {
            throw FileNotFoundException("APK not found")
        }
        return ParcelFileDescriptor.open(target, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
