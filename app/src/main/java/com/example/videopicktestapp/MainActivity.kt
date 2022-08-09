package com.example.videopicktestapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.choose_video_btn).setOnClickListener {
            chooseVideo()
        }
    }

    private fun chooseVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 5)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.data != null) {
            data.data?.let { uri ->
                val fileType = getFileType(uri)
                val file = File.createTempFile("vid", fileType)
                copyInputStreamToFile(inputStream = contentResolver.openInputStream(uri)!!, file = file)
                findViewById<TextView>(R.id.result_textview).text = "Path: ${uri.path}\n\nFile: $uri \n\nExtension: $fileType\n\nName: ${file.name}\n\nSize: ${file.length() / 1024}\n\nFile exists: ${file.isFile}"
            }
        }
    }

    private fun getFileType(uri: Uri): String? {
        val r = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(uri))
    }

    @Throws(IOException::class)
    private fun copyInputStreamToFile(inputStream: InputStream, file: File) {
        try {
            FileOutputStream(file, false).use { outputStream ->
                var read: Int
                val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
                while (inputStream.read(bytes).also { read = it } != -1) {
                    outputStream.write(bytes, 0, read)
                }
            }
        }catch (e: IOException){
            Log.e("Failed to load file: ", e.message.toString())
        }
    }
}