package com.example.qqmessageimage

import android.graphics.*
import android.text.TextPaint
import kotlin.math.max
import kotlin.math.min

/**
 * 高级文字绘制工具类
 * 参考 text_fit_draw.py 实现
 */
object TextDrawHelper {
    
    /**
     * 对齐方式
     */
    enum class Align {
        LEFT, CENTER, RIGHT
    }
    
    enum class VAlign {
        TOP, MIDDLE, BOTTOM
    }
    
    /**
     * 文本片段（用于多色绘制）
     */
    data class TextSegment(
        val text: String,
        val color: Int
    )
    
    /**
     * 在指定矩形区域内自适应绘制文本
     * 
     * @param canvas 画布
     * @param rect 绘制区域
     * @param text 要绘制的文本
     * @param normalColor 普通文字颜色
     * @param bracketColor 中括号及其内容颜色
     * @param maxFontSize 最大字体大小（可选）
     * @param align 水平对齐方式
     * @param valign 垂直对齐方式
     * @param lineSpacing 行间距比例（默认0.15）
     * @param typeface 字体（可选）
     */
    fun drawTextAuto(
        canvas: Canvas,
        rect: RectF,
        text: String,
        normalColor: Int = Color.BLACK,
        bracketColor: Int = Color.parseColor("#800080"), // 紫色
        maxFontSize: Int? = null,
        align: Align = Align.CENTER,
        valign: VAlign = VAlign.MIDDLE,
        lineSpacing: Float = 0.15f,
        typeface: Typeface = Typeface.DEFAULT
    ) {
        if (rect.width() <= 0 || rect.height() <= 0) {
            throw IllegalArgumentException("无效的文字区域")
        }
        
        val regionWidth = rect.width().toInt()
        val regionHeight = rect.height().toInt()
        
        // 查找最佳字号
        val result = findBestFontSize(
            text = text,
            regionWidth = regionWidth,
            regionHeight = regionHeight,
            maxFontSize = maxFontSize ?: regionHeight,
            lineSpacing = lineSpacing,
            typeface = typeface
        )
        
        if (result.fontSize == 0) {
            return // 无法绘制
        }
        
        // 创建画笔
        val paint = TextPaint().apply {
            this.typeface = typeface
            textSize = result.fontSize.toFloat()
            isAntiAlias = true
        }
        
        // 计算垂直起始位置
        val yStart = when (valign) {
            VAlign.TOP -> rect.top
            VAlign.MIDDLE -> rect.top + (regionHeight - result.totalHeight) / 2f
            VAlign.BOTTOM -> rect.bottom - result.totalHeight
        }
        
        // 绘制每一行
        var y = yStart
        var inBracket = false
        
        for (line in result.lines) {
            if (y - yStart > regionHeight) break
            
            // 测量行宽
            paint.color = normalColor
            val lineWidth = paint.measureText(line)
            
            // 计算水平起始位置
            val x = when (align) {
                Align.LEFT -> rect.left
                Align.CENTER -> rect.left + (regionWidth - lineWidth) / 2f
                Align.RIGHT -> rect.right - lineWidth
            }
            
            // 解析并绘制彩色片段
            val segments = parseColorSegments(line, normalColor, bracketColor, inBracket)
            var currentX = x
            
            for (segment in segments.first) {
                if (segment.text.isNotEmpty()) {
                    paint.color = segment.color
                    canvas.drawText(segment.text, currentX, y + paint.textSize, paint)
                    currentX += paint.measureText(segment.text)
                }
            }
            
            inBracket = segments.second
            y += result.lineHeight
        }
    }
    
    /**
     * 查找最佳字号
     */
    private fun findBestFontSize(
        text: String,
        regionWidth: Int,
        regionHeight: Int,
        maxFontSize: Int,
        lineSpacing: Float,
        typeface: Typeface
    ): FontResult {
        var lo = 1
        var hi = min(regionHeight, maxFontSize)
        var bestSize = 0
        var bestLines = emptyList<String>()
        var bestLineHeight = 0f
        var bestTotalHeight = 0f
        
        while (lo <= hi) {
            val mid = (lo + hi) / 2
            val paint = createPaint(mid.toFloat(), typeface)
            val lines = wrapLines(text, paint, regionWidth.toFloat())
            val metrics = measureBlock(lines, paint, lineSpacing)
            
            if (metrics.maxWidth <= regionWidth && metrics.totalHeight <= regionHeight) {
                bestSize = mid
                bestLines = lines
                bestLineHeight = metrics.lineHeight
                bestTotalHeight = metrics.totalHeight.toFloat()
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
        
        return FontResult(bestSize, bestLines, bestLineHeight, bestTotalHeight)
    }
    
    /**
     * 文本换行
     */
    private fun wrapLines(text: String, paint: TextPaint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        
        // 按段落分割
        val paragraphs = text.split("\n")
        
        for (para in paragraphs) {
            if (para.isEmpty()) {
                lines.add("")
                continue
            }
            
            // 判断是否包含空格（英文）
            val hasSpace = para.contains(" ")
            val units = if (hasSpace) para.split(" ") else para.toList().map { it.toString() }
            
            var buffer = ""
            
            for (unit in units) {
                val trial = if (buffer.isEmpty()) {
                    unit
                } else {
                    if (hasSpace) "$buffer $unit" else "$buffer$unit"
                }
                
                val width = paint.measureText(trial)
                
                if (width <= maxWidth) {
                    buffer = trial
                } else {
                    // 当前buffer已满，需要换行
                    if (buffer.isNotEmpty()) {
                        lines.add(buffer)
                    }
                    
                    // 处理超长单词/字符
                    if (hasSpace && unit.length > 1) {
                        // 英文单词拆分
                        var temp = ""
                        for (ch in unit) {
                            if (paint.measureText(temp + ch) <= maxWidth) {
                                temp += ch
                            } else {
                                if (temp.isNotEmpty()) {
                                    lines.add(temp)
                                }
                                temp = ch.toString()
                            }
                        }
                        buffer = temp
                    } else {
                        // 中文或单个字符
                        if (paint.measureText(unit) <= maxWidth) {
                            buffer = unit
                        } else {
                            // 超宽单个单元，强制添加
                            lines.add(unit)
                            buffer = ""
                        }
                    }
                }
            }
            
            if (buffer.isNotEmpty()) {
                lines.add(buffer)
            }
        }
        
        return lines
    }
    
    /**
     * 测量文本块
     */
    private fun measureBlock(
        lines: List<String>,
        paint: TextPaint,
        lineSpacing: Float
    ): BlockMetrics {
        val fontMetrics = paint.fontMetrics
        val lineHeight = (fontMetrics.descent - fontMetrics.ascent) * (1 + lineSpacing)
        
        var maxWidth = 0f
        for (line in lines) {
            maxWidth = max(maxWidth, paint.measureText(line))
        }
        
        val totalHeight = lineHeight * max(1, lines.size)
        
        return BlockMetrics(maxWidth.toInt(), totalHeight.toInt(), lineHeight)
    }
    
    /**
     * 解析带颜色的文本片段
     * 中括号【】及其内容使用特殊颜色
     */
    private fun parseColorSegments(
        line: String,
        normalColor: Int,
        bracketColor: Int,
        initialInBracket: Boolean
    ): Pair<List<TextSegment>, Boolean> {
        val segments = mutableListOf<TextSegment>()
        var buffer = ""
        var inBracket = initialInBracket
        
        for (ch in line) {
            when {
                ch == '[' || ch == '【' -> {
                    if (buffer.isNotEmpty()) {
                        segments.add(TextSegment(buffer, if (inBracket) bracketColor else normalColor))
                        buffer = ""
                    }
                    segments.add(TextSegment(ch.toString(), bracketColor))
                    inBracket = true
                }
                ch == ']' || ch == '】' -> {
                    if (buffer.isNotEmpty()) {
                        segments.add(TextSegment(buffer, bracketColor))
                        buffer = ""
                    }
                    segments.add(TextSegment(ch.toString(), bracketColor))
                    inBracket = false
                }
                else -> {
                    buffer += ch
                }
            }
        }
        
        if (buffer.isNotEmpty()) {
            segments.add(TextSegment(buffer, if (inBracket) bracketColor else normalColor))
        }
        
        return Pair(segments, inBracket)
    }
    
    /**
     * 创建画笔
     */
    private fun createPaint(textSize: Float, typeface: Typeface): TextPaint {
        return TextPaint().apply {
            this.textSize = textSize
            this.typeface = typeface
            isAntiAlias = true
        }
    }
    
    /**
     * 字体搜索结果
     */
    data class FontResult(
        val fontSize: Int,
        val lines: List<String>,
        val lineHeight: Float,
        val totalHeight: Float
    )
    
    /**
     * 文本块测量结果
     */
    data class BlockMetrics(
        val maxWidth: Int,
        val totalHeight: Int,
        val lineHeight: Float
    )
}
