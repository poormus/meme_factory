package muss.stein.memefactory.paint

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import muss.stein.memefactory.R
import toMutableBitmap
import java.util.*


class PaintView : View {

    private var bitmapFromRes:Bitmap?=null
    private var BRUSH_SIZE = 10
    private var mX = 0f
    private var mY = 0f
    private var mPath: Path? = null
    private var mPaint: Paint? = null
    private var currentColor = 0
    private val paths: ArrayList<FingerPath> = ArrayList<FingerPath>()
    private var mBitmap: Bitmap? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var mCanvas:Canvas?=null
    var newAdded = false
    private val bitmap: ArrayList<Bitmap> = ArrayList()
    private val undoBitmap: ArrayList<Bitmap> = ArrayList()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = COLOR_PEN
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = null
            alpha = 0xff
        }


    }


    fun init(resourceId:Int) {
//        mBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        bitmapFromRes=BitmapFactory.decodeResource(resources,resourceId)
        mCanvas= toMutableBitmap(bitmapFromRes!!)?.let { Canvas(it) }
        currentColor = COLOR_PEN
    }

    fun pen(color:Int) {
        currentColor = color
    }
    fun brushSize(size:Int){
        BRUSH_SIZE=size
    }

    fun eraser() {
       
        currentColor = COLOR_ERASER
    }

    fun clear() {
        paths.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.save()
        mCanvas=canvas
        mCanvas!!.drawBitmap(bitmapFromRes!!, 0f, 0f, mBitmapPaint)
        for (fp in paths) {
            mPaint!!.color = fp.color
            mPaint!!.strokeWidth = fp.strokeWidth.toFloat()
            mPaint!!.maskFilter = null
            mCanvas!!.drawPath(fp.path, mPaint!!)
        }
        mCanvas!!.restore()
    }



    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = FingerPath(currentColor, BRUSH_SIZE, mPath!!)
        paths.add(fp)
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
    }

    fun onUndoClick(){
        if(newAdded){
            bitmap.add(bitmapFromRes!!.copy(bitmapFromRes!!.config, bitmapFromRes!!.isMutable))
            newAdded=false
        }
        if(bitmap.size>1){
            undoBitmap.add(bitmap.removeAt(bitmap.size - 1))
            bitmapFromRes = bitmap[bitmap.size - 1].copy(bitmapFromRes!!.config, bitmapFromRes!!.isMutable)
            mCanvas=Canvas(bitmapFromRes!!)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                newAdded=true
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    companion object {
        const val COLOR_PEN = Color.RED
        const val COLOR_ERASER = Color.TRANSPARENT
        const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }
}