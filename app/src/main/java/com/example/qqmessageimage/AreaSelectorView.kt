package com.example.qqmessageimage

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 可框选区域的自定义View
 * 用户可以拖动四个角或整个框来调整文字显示区域
 */
class AreaSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4444")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#44FF4444")
        style = Paint.Style.FILL
    }
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4444")
        style = Paint.Style.FILL
    }
    
    // 框选区域 (相对于View的坐标)
    private var selectionRect = RectF(100f, 100f, 600f, 300f)
    
    // 拖动状态
    private enum class DragMode {
        NONE, MOVE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
    private var dragMode = DragMode.NONE
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    
    // 图片绘制区域
    private val imageRect = RectF()
    private val cornerRadius = 30f
    
    /**
     * 设置要显示的底图
     */
    fun setImage(bmp: Bitmap) {
        bitmap = bmp
        calculateImageRect()
        invalidate()
    }
    
    /**
     * 计算图片的绘制区域 (保持宽高比,居中显示)
     */
    private fun calculateImageRect() {
        val bmp = bitmap ?: return
        
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val bmpWidth = bmp.width.toFloat()
        val bmpHeight = bmp.height.toFloat()
        
        val scale = min(viewWidth / bmpWidth, viewHeight / bmpHeight)
        
        val scaledWidth = bmpWidth * scale
        val scaledHeight = bmpHeight * scale
        
        val left = (viewWidth - scaledWidth) / 2
        val top = (viewHeight - scaledHeight) / 2
        
        imageRect.set(left, top, left + scaledWidth, top + scaledHeight)
        
        // 初始化选择框为图片中心区域
        val padding = scaledWidth * 0.2f
        selectionRect.set(
            imageRect.left + padding,
            imageRect.top + padding,
            imageRect.right - padding,
            imageRect.bottom - padding
        )
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateImageRect()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制底图
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, paint)
        }
        
        // 绘制半透明遮罩 (选中区域外)
        val maskPaint = Paint().apply {
            color = Color.parseColor("#AA000000")
            style = Paint.Style.FILL
        }
        
        // 上
        canvas.drawRect(imageRect.left, imageRect.top, imageRect.right, selectionRect.top, maskPaint)
        // 下
        canvas.drawRect(imageRect.left, selectionRect.bottom, imageRect.right, imageRect.bottom, maskPaint)
        // 左
        canvas.drawRect(imageRect.left, selectionRect.top, selectionRect.left, selectionRect.bottom, maskPaint)
        // 右
        canvas.drawRect(selectionRect.right, selectionRect.top, imageRect.right, selectionRect.bottom, maskPaint)
        
        // 绘制选择框
        canvas.drawRect(selectionRect, fillPaint)
        canvas.drawRect(selectionRect, rectPaint)
        
        // 绘制四个角的控制点
        drawCorner(canvas, selectionRect.left, selectionRect.top)
        drawCorner(canvas, selectionRect.right, selectionRect.top)
        drawCorner(canvas, selectionRect.left, selectionRect.bottom)
        drawCorner(canvas, selectionRect.right, selectionRect.bottom)
        
        // 绘制尺寸信息
        val infoText = "区域: ${(selectionRect.width()).toInt()} x ${(selectionRect.height()).toInt()}"
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(infoText, width / 2f, selectionRect.top - 20, textPaint)
    }
    
    /**
     * 绘制角点
     */
    private fun drawCorner(canvas: Canvas, x: Float, y: Float) {
        canvas.drawCircle(x, y, cornerRadius, cornerPaint)
        canvas.drawCircle(x, y, cornerRadius - 4f, Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        })
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                dragMode = detectDragMode(event.x, event.y)
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (dragMode != DragMode.NONE) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    
                    when (dragMode) {
                        DragMode.MOVE -> moveRect(dx, dy)
                        DragMode.TOP_LEFT -> resizeRect(dx, dy, true, true)
                        DragMode.TOP_RIGHT -> resizeRect(-dx, dy, false, true)
                        DragMode.BOTTOM_LEFT -> resizeRect(dx, -dy, true, false)
                        DragMode.BOTTOM_RIGHT -> resizeRect(-dx, -dy, false, false)
                        else -> {}
                    }
                    
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                    return true
                }
            }
            
            MotionEvent.ACTION_UP -> {
                dragMode = DragMode.NONE
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    /**
     * 检测拖动模式
     */
    private fun detectDragMode(x: Float, y: Float): DragMode {
        // 检测是否点击了角点
        if (isNearPoint(x, y, selectionRect.left, selectionRect.top)) {
            return DragMode.TOP_LEFT
        }
        if (isNearPoint(x, y, selectionRect.right, selectionRect.top)) {
            return DragMode.TOP_RIGHT
        }
        if (isNearPoint(x, y, selectionRect.left, selectionRect.bottom)) {
            return DragMode.BOTTOM_LEFT
        }
        if (isNearPoint(x, y, selectionRect.right, selectionRect.bottom)) {
            return DragMode.BOTTOM_RIGHT
        }
        
        // 检测是否在选择框内 (移动模式)
        if (selectionRect.contains(x, y)) {
            return DragMode.MOVE
        }
        
        return DragMode.NONE
    }
    
    /**
     * 判断点是否接近某个坐标
     */
    private fun isNearPoint(x: Float, y: Float, px: Float, py: Float): Boolean {
        val distance = Math.sqrt(((x - px) * (x - px) + (y - py) * (y - py)).toDouble())
        return distance < cornerRadius * 1.5
    }
    
    /**
     * 移动整个选择框
     */
    private fun moveRect(dx: Float, dy: Float) {
        var newLeft = selectionRect.left + dx
        var newTop = selectionRect.top + dy
        var newRight = selectionRect.right + dx
        var newBottom = selectionRect.bottom + dy
        
        // 限制在图片范围内
        if (newLeft < imageRect.left) {
            val offset = imageRect.left - newLeft
            newLeft += offset
            newRight += offset
        }
        if (newRight > imageRect.right) {
            val offset = newRight - imageRect.right
            newLeft -= offset
            newRight -= offset
        }
        if (newTop < imageRect.top) {
            val offset = imageRect.top - newTop
            newTop += offset
            newBottom += offset
        }
        if (newBottom > imageRect.bottom) {
            val offset = newBottom - imageRect.bottom
            newTop -= offset
            newBottom -= offset
        }
        
        selectionRect.set(newLeft, newTop, newRight, newBottom)
    }
    
    /**
     * 调整选择框大小
     */
    private fun resizeRect(dx: Float, dy: Float, isLeft: Boolean, isTop: Boolean) {
        val minSize = 100f
        
        var newLeft = selectionRect.left
        var newTop = selectionRect.top
        var newRight = selectionRect.right
        var newBottom = selectionRect.bottom
        
        if (isLeft) {
            newLeft -= dx
            newLeft = max(imageRect.left, min(newLeft, newRight - minSize))
        } else {
            newRight -= dx
            newRight = min(imageRect.right, max(newRight, newLeft + minSize))
        }
        
        if (isTop) {
            newTop -= dy
            newTop = max(imageRect.top, min(newTop, newBottom - minSize))
        } else {
            newBottom -= dy
            newBottom = min(imageRect.bottom, max(newBottom, newTop + minSize))
        }
        
        selectionRect.set(newLeft, newTop, newRight, newBottom)
    }
    
    /**
     * 获取选中区域 (相对于原图的坐标)
     */
    fun getSelectionInImage(): RectF {
        val bmp = bitmap ?: return RectF()
        
        // 计算选择框相对于图片的比例
        val scaleX = bmp.width / imageRect.width()
        val scaleY = bmp.height / imageRect.height()
        
        return RectF(
            (selectionRect.left - imageRect.left) * scaleX,
            (selectionRect.top - imageRect.top) * scaleY,
            (selectionRect.right - imageRect.left) * scaleX,
            (selectionRect.bottom - imageRect.top) * scaleY
        )
    }
}
