package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/// this is a view (UI) not an Activity
/// this will extends the View class
class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    /// dataTypes that will be used to draw
    private var drawPath: CustomPath? = null
    private var canvasBitmap: Bitmap? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var brushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushSize)

        /// set properties needed to paint
        drawPaint!!.color = color
        drawPaint!!.style = Paint.Style.STROKE
        /// as we are using STROKE style, so we set STROKE properties
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        /// create the bitmap
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        /// set the canvas now using the bitmap
        canvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /// to null check
        canvas?.let {
            println("on draw no null")

            canvas!!.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)

            if(drawPath != null && drawPaint != null) {
                /// set drawPaint properties that we want to draw with
                drawPaint!!.strokeWidth  = drawPath!!.brushThickness
                drawPaint!!.color = drawPath!!.color

                /// draw on the canvas using the drawPaint on the path (drawPath)
                canvas!!.drawPath(drawPath!!, drawPaint!!)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> { /// these are called lambda expressions
                /// might not be needed to set everytime
                drawPath!!.color = color
                drawPath!!.brushThickness = brushSize

                drawPath!!.reset()
                if(touchX != null && touchY != null) {
                    drawPath!!.moveTo(touchX!!, touchY!!)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if(touchX != null && touchY != null) {
                    drawPath!!.lineTo(touchX!!, touchY!!)
                }
            }

            MotionEvent.ACTION_UP -> {
                println("press up")

                drawPath = CustomPath(color, brushSize)
            }

            else -> return false
        }

        /// invalidate the view
        invalidate()

        return true
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()
}