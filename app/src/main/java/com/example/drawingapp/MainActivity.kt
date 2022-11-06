package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        drawingView!!.setBrushSize(20.toFloat())

        var brushSizeSelector = findViewById<ImageButton>(R.id.brushSizeSelector)
        brushSizeSelector.setOnClickListener{
            openBrushSizeSelector()
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