package com.example.drawingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var currentPaintImageButton: ImageButton? = null

    /// this is a generic function to get any permission
    /// which permission to ask for will be decided by the String passed to it, in this case is `Manifest.permission.CAMERA`
    private val cameraResultLauncher: ActivityResultLauncher<String>
            = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted for Storage.", Toast.LENGTH_LONG).show()
                openImageSelectorIntent()
            } else {
                Toast.makeText(this, "Permission denied for Storage.", Toast.LENGTH_LONG).show()
            }
    }

    /// this is a generic function to get any permission
    /// which permission to ask for will be decided by the String passed to it, in this case is `Manifest.permission.CAMERA`
    private val writeStorageResultLauncher: ActivityResultLauncher<String>
            = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
            isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted for WRITE Storage.", Toast.LENGTH_LONG).show()
            saveImage()
        } else {
            Toast.makeText(this, "Permission denied for WRITE Storage.", Toast.LENGTH_LONG).show()
        }
    }

    /// opens an activity and uses its result to set the bgImageView
    private val openGalleryLauncher: ActivityResultLauncher<Intent>
        = registerForActivityResult((ActivityResultContracts.StartActivityForResult()))
    {
        result ->
            if(result.resultCode == RESULT_OK &&
                    result.data != null){
                val bgImageView = findViewById<ImageView>(R.id.bgImageView)
                bgImageView.setImageURI(result.data!!.data)
            }
    }

    private fun openImageSelectorIntent() {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        openGalleryLauncher.launch(pickIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        drawingView!!.setBrushSize(20.toFloat())

        var colorPalletLinearLayout = findViewById<LinearLayout>(R.id.colorPalletLinearLayout)
        currentPaintImageButton = colorPalletLinearLayout[1] as ImageButton
        currentPaintImageButton!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )
        drawingView!!.setBrushColor(currentPaintImageButton!!.tag.toString())

        var galleryOpener = findViewById<ImageButton>(R.id.galleryOpener)
        galleryOpener.setOnClickListener {
            if(checkIfReadStorageAllowed()){
                openImageSelectorIntent()
            } else {
                Permissions.getPermissionReadStorage(this, cameraResultLauncher)
            }

            /// only this much code is enough to get permissions
//            cameraResultLauncher.launch(Manifest.permission.CAMERA)
        }

        var saveButton = findViewById<ImageButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            if(checkIfWriteStorageAllowed()){
                saveImage()
            } else {
                Permissions.getPermissionWriteStorage(this, writeStorageResultLauncher)
            }
        }

        var undoButton = findViewById<ImageButton>(R.id.undoButton)
        undoButton.setOnClickListener{
            drawingView!!.undoLastDrawPath()
        }

        var brushSizeSelector = findViewById<ImageButton>(R.id.brushSizeSelector)
        brushSizeSelector.setOnClickListener{
            openBrushSizeSelector()
        }
    }

    private fun saveImage() {
        lifecycleScope.launch{
            val flDrawingView: FrameLayout = findViewById(R.id.topLayerFrameLayout)
            //Save the image to the device
            saveBitmapFileCoroutine(getBitmapFromView(flDrawingView))
        }
    }

    private suspend fun saveBitmapFileCoroutine(bitmapFile: Bitmap): String {
        var result = ""

        withContext(Dispatchers.IO){
            try {
                val bytes = ByteArrayOutputStream()

                /// compress the file, and it gets stores in the outputStream => here bytes
                bitmapFile.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                /// create a file with a unique name
                val createdFile = File(
                    externalCacheDir?.absoluteFile.toString()
                    + File.separator + "DrawingAPP_" + System.currentTimeMillis() / 1000 + ".png"
                )

                val fileOutputStream = FileOutputStream(createdFile)
                fileOutputStream.write(bytes.toByteArray())
                fileOutputStream.close()

                result = createdFile.absolutePath

                /// then show success or failure msg on UI thread
                runOnUiThread{
                    if(!result.isEmpty()){
                        showToast("File saved successfully :$result")
                    } else {
                        showToast("Something went wrong while saving the file")
                    }
                }
            } catch (e: Exception) {
                result = ""
                e.printStackTrace()
            }
        }

        return result
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        /// we first create an empty canvas with width and height of the view
        val canvas = Canvas(returnedBitmap)

        /// we then take the background image selected
        val bgDrawable = view.background

        if(bgDrawable != null){
            /// if we have an image selected then we draw it
            bgDrawable.draw(canvas)
        } else {
            /// if we dont have an image selected then we draw white color on it
            canvas.drawColor(Color.WHITE)
        }

        /// we then draw the view on the canvas
        view.draw(canvas)

        return returnedBitmap
    }



    private fun checkIfReadStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfWriteStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    fun paintColorChanged(view: View){
        if(view != currentPaintImageButton){
            /// unselect
            currentPaintImageButton!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )

            currentPaintImageButton = view as ImageButton

            val colorTag = currentPaintImageButton!!.tag.toString()
            drawingView!!.setBrushColor(colorTag)

            /// select
            currentPaintImageButton!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
        }
    }

    private fun openBrushSizeSelector() {
        val brushSizeDialog = Dialog(this)
        brushSizeDialog.setContentView(R.layout.dialog_brush_size)
        brushSizeDialog.show()

        val smallBtn = brushSizeDialog.findViewById<ImageButton>(R.id.small_size_brush)
        smallBtn.setOnClickListener{
            drawingView!!.setBrushSize(10.toFloat())
            brushSizeDialog.dismiss()
        }

        val mediumBtn = brushSizeDialog.findViewById<ImageButton>(R.id.medium_size_brush)
        mediumBtn.setOnClickListener{
            drawingView!!.setBrushSize(20.toFloat())
            brushSizeDialog.dismiss()
        }

        val largeBtn = brushSizeDialog.findViewById<ImageButton>(R.id.large_size_brush)
        largeBtn.setOnClickListener{
            drawingView!!.setBrushSize(30.toFloat())
            brushSizeDialog.dismiss()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this@MainActivity,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}