package com.example.demo_take_photo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.demo_take_photo.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var filename: String? = null

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initElement()
        initLauncher()
    }

    private fun initElement() {
        binding.buttonPrint.setOnClickListener {
            takePicture()
        }
    }

    private fun initLauncher() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                if (it != null && it.resultCode == Activity.RESULT_OK) {
                    caseCapture()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun caseCapture() {
        filename.let {
            if (it != null) {
                val tempFile = File(it)
                binding.image.setImageURI(tempFile.toUri())
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val image = createTempFile()

        val uri = FileProvider.getUriForFile(
            this,
            "com.example.demo_take_photo.provider",
            image
        )

        filename = image.absolutePath.replace("/storage/emulated/0", "sdcard")

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        launcher.launch(intent)
    }

    private fun createTempFile(): File {
        val tempDir = File("${externalMediaDirs.first()}/Pictures")

        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }

        return File.createTempFile("capture_", ".png", tempDir)
    }
}