package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.collections.ArrayList

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

    /// this will be used to store all paths the user makes
    /// this will be useful to undo operations
    private var allPaths = ArrayList<CustomPath>()

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
        /// no need to set the brushSize here, it will be set from the mainActivity
//        brushSize = 20.toFloat()
    }

    /// This is called during layout when the size of this view has changed.
    /// mainly, it is called at the start when the view is set up
    /// so, here we set our canvas
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        /// create the bitmap
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        /// set the canvas now using the bitmap
        canvas = Canvas(canvasBitmap!!)
    }

    /// called when the view should render its content
    /// it is called after we call invalidate(), which we call from onTouchEvent(), hence our drawPath gets painted
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /// to null check
        canvas?.let {
            /// even if we comment this drawBitmap code, nothing changes
            canvas!!.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)

            /// after the MotionEvent.ACTION_UP, even the current path is removed as the drawPath is reset
            /// the current path will also get repainted using the below for loop

            /// then we redraw all other paths back
            /// this way we can undo a path by removing it from the allPaths array
            for(_path in allPaths){
                drawPaint!!.strokeWidth  = _path!!.brushThickness
                drawPaint!!.color = _path!!.color

                /// draw on the canvas using the drawPaint on the path (drawPath)
                canvas!!.drawPath(_path!!, drawPaint!!)
            }
            ///// if we keep this for loop after the if statement then the new path first gets painted below all other paths
            ///// so, we move it up

            /// first we draw the motion as it happens,
            /// if we remove this, then the path will be painted after the path is completed, seems like a lag
            if(drawPath != null && drawPaint != null) {
                /// set drawPaint properties that we want to draw with
                drawPaint!!.strokeWidth  = drawPath!!.brushThickness
                drawPaint!!.color = drawPath!!.color

                /// draw on the canvas using the drawPaint on the path (drawPath)
                canvas!!.drawPath(drawPath!!, drawPaint!!)
            }
        }
    }

    /// to handle touch screen motion events, when ever user draws something on the screen, this method gets called
    /// we mainly store the users actions in our drawPath variable and later user it to paint on the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            /// when a user touches the screen , MotionEvent.ACTION_DOWN is called
            /// in this method we move our path to the x and y coordinate where the touch has happened
            MotionEvent.ACTION_DOWN -> { /// these are called lambda expressions
                /// might not be needed to set everytime
                drawPath!!.color = color
                drawPath!!.brushThickness = brushSize

                /// comment this to not clear the previous drawing
                drawPath!!.reset()
                if(touchX != null && touchY != null) {
                    drawPath!!.moveTo(touchX!!, touchY!!)
                }
            }

            /// when a user moved through the screen , MotionEvent.ACTION_MOVE is called
            /// in this method we create a line to our new x and y coordinate where the touch has happened
            MotionEvent.ACTION_MOVE -> {
                if(touchX != null && touchY != null) {
                    drawPath!!.lineTo(touchX!!, touchY!!)
                }
            }

            /// when a user lifts his finger from the screen , MotionEvent.ACTION_UP is called
            MotionEvent.ACTION_UP -> {
                /// we add the path here to allPaths,
                /// because when the user lifts his finger up, it marks completion of a path
                allPaths.add(drawPath!!)

                /// comment this to make the drawing stay, this resets the drawPath as soon as the user lifts his finger
                drawPath = CustomPath(color, brushSize)
            }

            else -> return false
        }

        /// invalidate the view
        /// this inturn calls the onDraw method where the actual drawing takes place
        invalidate()

        return true
    }

    fun undoLastDrawPath() {
        if(allPaths.isNotEmpty()){
            allPaths.removeLast()
            invalidate()
        }
    }

    /// this function will help us set the brush size, with respect to the screen size/dimension
    fun setBrushSize(newSize: Float) {
        /// TypedValue.applyDimension will convert the newSize to a floating point value
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )

        drawPath!!.brushThickness = brushSize
    }

    fun setBrushColor(newColor: String) {
        color = Color.parseColor(newColor)

        drawPaint!!.color = color
    }

    /// this is a class that overrides the Path method, where we store the movement of the user's drawing
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()
}