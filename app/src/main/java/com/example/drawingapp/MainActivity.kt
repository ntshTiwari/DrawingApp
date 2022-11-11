package com.example.drawingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get

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
        galleryOpener.setOnClickListener{
            Permissions.getPermission(this, cameraResultLauncher)

            /// only this much code is enough to get permissions
//            cameraResultLauncher.launch(Manifest.permission.CAMERA)
        }

        var brushSizeSelector = findViewById<ImageButton>(R.id.brushSizeSelector)
        brushSizeSelector.setOnClickListener{
            openBrushSizeSelector()
        }
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
}