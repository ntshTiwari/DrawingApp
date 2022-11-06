package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var currentPaintImageButton: ImageButton? = null

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