package com.example.qqmessageimage

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class QQAccessibilityService : AccessibilityService() {

    private var lastProcessedText: String? = null
    private var lastProcessedTime: Long = 0
    
    companion object {
        private const val TAG = "QQAccessibilityService"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        
        Log.d(TAG, "收到事件: ${event.eventType}, 包名: ${event.packageName}, 类名: ${event.className}")

        // 检查是否启用
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enabled", false)) {
            Log.d(TAG, "服务未启用，忽略事件")
            return
        }

        // 检查模板图片是否存在
        val templateFile = File(filesDir, "template.png")
        if (!templateFile.exists()) {
            Log.d(TAG, "模板图片不存在")
            return
        }

        // 只监听点击事件来触发处理
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                // 检查是否点击了发送按钮
                val clickedNode = event.source
                if (clickedNode != null) {
                    val isButton = clickedNode.className?.toString()?.contains("Button") == true ||
                                   clickedNode.className?.toString()?.contains("ImageView") == true
                    if (isButton) {
                        Log.d(TAG, "检测到按钮点击，开始处理")
                        processQQMessage()
                    }
                    clickedNode.recycle()
                }
            }
        }
    }

    private fun processQQMessage() {
        try {
            Log.d(TAG, "开始处理QQ消息")
            val rootNode = rootInActiveWindow ?: run {
                Log.d(TAG, "无法获取根节点")
                return
            }
            
            // 查找输入框和发送按钮
            val messageText = findMessageText(rootNode)
            Log.d(TAG, "找到的消息文本: $messageText")
            
            if (!messageText.isNullOrEmpty()) {
                // 防止极短时间内重复处理(500ms内的重复点击)
                val currentTime = System.currentTimeMillis()
                if (messageText == lastProcessedText && 
                    currentTime - lastProcessedTime < 500) {
                    Log.d(TAG, "重复消息（500ms内），忽略")
                    return
                }

                Log.d(TAG, "准备处理消息: $messageText")
                lastProcessedText = messageText
                lastProcessedTime = currentTime
                
                // 处理消息
                processAndSendMessage(messageText)
            }
            
            rootNode.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "处理消息时出错", e)
            e.printStackTrace()
        }
    }

    private fun findMessageText(node: AccessibilityNodeInfo?): String? {
        node ?: return null

        // 查找可能的输入框
        if (node.className?.contains("EditText") == true) {
            val text = node.text?.toString()
            if (!text.isNullOrEmpty()) {
                return text
            }
        }

        // 递归查找子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val text = findMessageText(child)
            if (text != null) {
                child.recycle()
                return text
            }
            child.recycle()
        }

        return null
    }

    private fun findSendButton(node: AccessibilityNodeInfo?): Boolean {
        node ?: return false

        // 查找发送按钮 (通常是"发送"文字或图标)
        if (node.className?.contains("Button") == true ||
            node.className?.contains("ImageView") == true) {
            val desc = node.contentDescription?.toString() ?: ""
            val text = node.text?.toString() ?: ""
            
            if (desc.contains("发送") || text.contains("发送")) {
                return true
            }
        }

        // 递归查找
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findSendButton(child)) {
                child.recycle()
                return true
            }
            child.recycle()
        }

        return false
    }

    private fun processAndSendMessage(text: String) {
        try {
            val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
            val x = prefs.getInt("text_x", 100)
            val y = prefs.getInt("text_y", 100)
            val width = prefs.getInt("text_width", 500)
            val height = prefs.getInt("text_height", 200)
            val maxFontSize = prefs.getInt("text_size", 40)
            val textColor = prefs.getInt("text_color", Color.BLACK)
            val bracketColor = prefs.getInt("bracket_color", Color.parseColor("#800080"))
            val alignValue = prefs.getString("text_align", "center") ?: "center"
            val valignValue = prefs.getString("text_valign", "middle") ?: "middle"
            val lineSpacing = prefs.getFloat("line_spacing", 0.15f)

            // 加载模板图片（base.png）
            val templateFile = File(filesDir, "template.png")
            if (!templateFile.exists()) {
                showToast("模板图片不存在")
                return
            }
            
            val templateBitmap = BitmapFactory.decodeFile(templateFile.absolutePath)
            val mutableBitmap = templateBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // 创建画布
            val canvas = Canvas(mutableBitmap)
            
            // 定义文字绘制区域
            val textRect = RectF(
                x.toFloat(),
                y.toFloat(),
                (x + width).toFloat(),
                (y + height).toFloat()
            )
            
            // 转换对齐方式
            val align = when (alignValue) {
                "left" -> TextDrawHelper.Align.LEFT
                "right" -> TextDrawHelper.Align.RIGHT
                else -> TextDrawHelper.Align.CENTER
            }
            
            val valign = when (valignValue) {
                "top" -> TextDrawHelper.VAlign.TOP
                "bottom" -> TextDrawHelper.VAlign.BOTTOM
                else -> TextDrawHelper.VAlign.MIDDLE
            }
            
            // 使用高级文字绘制功能
            TextDrawHelper.drawTextAuto(
                canvas = canvas,
                rect = textRect,
                text = text,
                normalColor = textColor,
                bracketColor = bracketColor,
                maxFontSize = maxFontSize,
                align = align,
                valign = valign,
                lineSpacing = lineSpacing,
                typeface = Typeface.DEFAULT_BOLD
            )
            
            // 如果有覆盖图层，加载并叠加
            val overlayFile = File(filesDir, "overlay.png")
            if (overlayFile.exists()) {
                val overlayBitmap = BitmapFactory.decodeFile(overlayFile.absolutePath)
                canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
                overlayBitmap.recycle()
            }

            // 保存处理后的图片
            val outputFile = File(getExternalFilesDir(null), "processed_${System.currentTimeMillis()}.png")
            FileOutputStream(outputFile).use { out ->
                mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            mutableBitmap.recycle()
            templateBitmap.recycle()

            // 尝试自动发送图片
            val autoSend = prefs.getBoolean("auto_send", false)
            if (autoSend) {
                android.os.Handler(mainLooper).postDelayed({
                    tryAutoSendImage(outputFile)
                }, 500) // 延迟500ms等待界面稳定
            } else {
                showToast("图片已处理: ${outputFile.name}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("处理失败: ${e.message}")
        }
    }
    
    /**
     * 尝试自动发送图片
     * 使用无障碍功能模拟用户操作
     */
    private fun tryAutoSendImage(imageFile: File) {
        try {
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                showToast("无法获取窗口，图片已保存: ${imageFile.name}")
                return
            }
            
            // 方法1: 查找并点击"相册"或"图片"按钮
            val albumClicked = findAndClickNode(rootNode, listOf("相册", "图片", "album", "photo"))
            
            if (albumClicked) {
                // 等待相册打开，然后选择刚生成的图片
                android.os.Handler(mainLooper).postDelayed({
                    selectImageFromGallery(imageFile)
                }, 1000)
            } else {
                // 方法2: 查找"+"按钮（QQ聊天输入框旁边的加号）
                val plusClicked = findAndClickPlusButton(rootNode)
                
                if (plusClicked) {
                    android.os.Handler(mainLooper).postDelayed({
                        // 再次查找相册按钮
                        val root2 = rootInActiveWindow
                        if (root2 != null) {
                            val albumClicked2 = findAndClickNode(root2, listOf("相册", "图片"))
                            if (albumClicked2) {
                                android.os.Handler(mainLooper).postDelayed({
                                    selectImageFromGallery(imageFile)
                                }, 1000)
                            } else {
                                showToast("未找到相册按钮，图片已保存")
                            }
                            root2.recycle()
                        }
                    }, 500)
                } else {
                    showToast("无法自动发送，图片已保存: ${imageFile.name}")
                }
            }
            
            rootNode.recycle()
            
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("自动发送失败: ${e.message}")
        }
    }
    
    /**
     * 查找并点击包含指定文字的节点
     */
    private fun findAndClickNode(node: AccessibilityNodeInfo?, keywords: List<String>): Boolean {
        node ?: return false
        
        // 检查当前节点
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        for (keyword in keywords) {
            if (text.contains(keyword.lowercase()) || desc.contains(keyword.lowercase())) {
                if (node.isClickable) {
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                } else {
                    // 尝试点击父节点
                    var parent = node.parent
                    while (parent != null) {
                        if (parent.isClickable) {
                            val result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            parent.recycle()
                            return result
                        }
                        val temp = parent
                        parent = parent.parent
                        temp.recycle()
                    }
                }
            }
        }
        
        // 递归查找子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndClickNode(child, keywords)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        
        return false
    }
    
    /**
     * 查找并点击"+"按钮
     */
    private fun findAndClickPlusButton(node: AccessibilityNodeInfo?): Boolean {
        node ?: return false
        
        // 查找"+"号或"更多"按钮
        val text = node.text?.toString() ?: ""
        val desc = node.contentDescription?.toString() ?: ""
        
        if ((text.contains("+") || text.contains("更多") || 
             desc.contains("+") || desc.contains("更多") ||
             desc.contains("plus") || desc.contains("more")) &&
            node.isClickable) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        
        // 递归查找
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndClickPlusButton(child)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        
        return false
    }
    
    /**
     * 从相册中选择图片
     * 注意：这个方法依赖于相册的界面结构，可能不稳定
     */
    private fun selectImageFromGallery(imageFile: File) {
        try {
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                showToast("无法打开相册")
                return
            }
            
            // 方法1: 尝试查找图片网格中的第一个图片
            val firstImageClicked = findAndClickFirstImage(rootNode)
            
            if (firstImageClicked) {
                // 等待预览界面，然后点击发送
                android.os.Handler(mainLooper).postDelayed({
                    clickSendButton()
                }, 800)
            } else {
                showToast("未找到图片，请手动选择")
            }
            
            rootNode.recycle()
            
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("选择图片失败: ${e.message}")
        }
    }
    
    /**
     * 查找并点击第一张图片
     */
    private fun findAndClickFirstImage(node: AccessibilityNodeInfo?): Boolean {
        node ?: return false
        
        // 查找ImageView或包含"图片"的节点
        if ((node.className?.contains("ImageView") == true ||
             node.className?.contains("GridView") == true ||
             node.className?.contains("RecyclerView") == true) &&
            node.isClickable) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        
        // 递归查找（只查找前几个节点，避免点击错误的图片）
        val maxChildren = minOf(node.childCount, 10)
        for (i in 0 until maxChildren) {
            val child = node.getChild(i) ?: continue
            if (findAndClickFirstImage(child)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        
        return false
    }
    
    /**
     * 点击发送按钮
     */
    private fun clickSendButton() {
        try {
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                showToast("无法点击发送")
                return
            }
            
            val sendClicked = findAndClickNode(rootNode, listOf("发送", "send", "确定", "完成"))
            
            if (sendClicked) {
                showToast("图片已自动发送")
            } else {
                showToast("未找到发送按钮，请手动点击")
            }
            
            rootNode.recycle()
            
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("点击发送失败: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        android.os.Handler(mainLooper).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInterrupt() {
        // 服务中断时调用
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        showToast("QQ消息监听服务已启动")
    }
}
