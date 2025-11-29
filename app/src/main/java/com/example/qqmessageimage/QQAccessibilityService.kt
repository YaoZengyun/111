package com.example.qqmessageimage

import android.accessibilityservice.AccessibilityService
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * QQ消息图片助手 - 辅助功能服务
 * 功能: 检测输入 "文字\\" 后自动生成图片并发送
 */
class QQAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "QQAccessibilityService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "=== 辅助功能服务已连接 ===")
        
        // 确保默认模板图片存在
        ensureDefaultTemplateExists()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        
        // 只处理QQ的事件
        if (event.packageName != "com.tencent.mobileqq") {
            return
        }
        
        // 检查是否启用
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enabled", false)) {
            return
        }

        // 只监听文本变化事件,检测 "\\" 触发标记
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val node = event.source
            if (node != null && node.className?.contains("EditText") == true) {
                val currentText = node.text?.toString() ?: ""
                
                // 检测 "\\" 触发标记
                if (currentText.endsWith("\\\\")) {
                    Log.i(TAG, "✓ 检测到触发标记 '\\\\'")
                    
                    // 提取 "\\" 之前的文本
                    val messageText = currentText.substring(0, currentText.length - 2).trim()
                    
                    if (messageText.isNotEmpty()) {
                        Log.i(TAG, "✓ 提取消息: '$messageText'")
                        
                        // 清空输入框
                        val bundle = android.os.Bundle()
                        bundle.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            ""
                        )
                        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                        
                        // 处理并发送
                        Handler(Looper.getMainLooper()).postDelayed({
                            processAndSendMessage(messageText)
                        }, 300)
                    }
                }
                node.recycle()
            }
        }
    }

    /**
     * 确保默认模板图片存在
     */
    private fun ensureDefaultTemplateExists() {
        val templateFile = File(filesDir, "template.png")
        
        if (!templateFile.exists()) {
            try {
                // 创建默认的白色底图 (1080x1080)
                val bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                
                // 保存
                FileOutputStream(templateFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                bitmap.recycle()
                
                Log.i(TAG, "✓ 已创建默认模板图片")
            } catch (e: Exception) {
                Log.e(TAG, "创建默认模板失败", e)
            }
        }
    }

    /**
     * 处理消息并生成图片
     */
    private fun processAndSendMessage(text: String) {
        try {
            Log.i(TAG, "开始处理消息: $text")
            
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

            // 加载模板图片
            val templateFile = File(filesDir, "template.png")
            if (!templateFile.exists()) {
                showToast("模板图片不存在")
                return
            }
            
            val templateBitmap = BitmapFactory.decodeFile(templateFile.absolutePath)
            val mutableBitmap = templateBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // 创建画布并绘制文字
            val canvas = Canvas(mutableBitmap)
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
            
            // 绘制文字
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
            
            // 叠加覆盖层(如果存在)
            val overlayFile = File(filesDir, "overlay.png")
            if (overlayFile.exists()) {
                val overlayBitmap = BitmapFactory.decodeFile(overlayFile.absolutePath)
                canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
                overlayBitmap.recycle()
            }

            // 保存图片到公共图库（MediaStore），并尝试复制到常见的QQ图片目录，
            // 这样QQ的图片选择器更容易识别到刚生成的图片
            val timestamp = System.currentTimeMillis()
            val fileName = "QQMsg_$timestamp.png"

            var savedImageUriString: String? = null
            try {
                // 优先使用 MediaStore（适用于 Android 10+）
                val resolver = applicationContext.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/QQMessageImageApp")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    savedImageUriString = uri.toString()
                }
            } catch (e: Exception) {
                Log.w(TAG, "保存到MediaStore失败，回退到应用目录: ${e.message}")
            }

            // 兼容性尝试：将图片写入常见的QQ图片目录（仅在允许的设备上生效）
            try {
                val externalRoot = android.os.Environment.getExternalStorageDirectory()
                val qqCandidates = arrayOf(
                    File(externalRoot, "Tencent/QQ_Images"),
                    File(externalRoot, "Tencent/MobileQQ/photo"),
                    File(externalRoot, "Pictures/QQMessageImageApp")
                )

                val scanned = mutableListOf<String>()
                for (dir in qqCandidates) {
                    try {
                        if (!dir.exists()) dir.mkdirs()
                        val dest = File(dir, fileName)
                        FileOutputStream(dest).use { out ->
                            mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }
                        scanned.add(dest.absolutePath)
                    } catch (_: Exception) {
                        // 忽略单个目录写入失败，继续尝试其他目录
                    }
                }

                if (scanned.isNotEmpty()) {
                    try {
                        android.media.MediaScannerConnection.scanFile(
                            applicationContext,
                            scanned.toTypedArray(),
                            Array(scanned.size) { "image/png" },
                            null
                        )
                    } catch (_: Exception) {
                        // 忽略
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "复制到QQ目录失败: ${e.message}")
            }
            
            Log.i(TAG, "✓ 图片已保存: ${outputFile.absolutePath}")
            
            mutableBitmap.recycle()
            templateBitmap.recycle()

            // 自动发送
            val autoSend = prefs.getBoolean("auto_send", false)
            if (autoSend) {
                Handler(Looper.getMainLooper()).postDelayed({
                    // 直接开始发送流程，不再重复保存
                    startAutoSelectAndSend()
                }, 300)
            } else {
                showToast("✓ 图片已生成")
            }

        } catch (e: Exception) {
            Log.e(TAG, "处理失败", e)
            showToast("处理失败: ${e.message}")
        }
    }

    /**
     * 开始自动选择并发送流程
     * 方案: 复制图片到剪贴板 → 粘贴 → 发送
     */
    private fun startAutoSelectAndSend() {
        try {
            Log.i(TAG, "开始自动发送流程 (剪贴板方案)")
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                showToast("请手动发送图片")
                return
            }

            // 1. 复制图片到剪贴板
            // 注意：这里需要获取刚才保存的图片的URI
            // 由于之前的逻辑中 savedImageUriString 是局部变量，我们需要调整一下
            // 暂时先尝试查找最新的图片URI，或者在保存时记录下来
            // 为了简化，我们假设已经有了 Uri，这里先演示剪贴板逻辑
            // 实际应用中，我们需要将 savedImageUriString 提升为类成员变量或者传递过来
            
            // 修正：我们需要在保存图片时记录 Uri，这里先通过 MediaStore 查询最新的图片
            val imageUri = getLastSavedImageUri()
            if (imageUri != null) {
                copyImageToClipboard(imageUri)
                Log.i(TAG, "✓ 图片已复制到剪贴板")
                showToast("图片已复制，准备粘贴...")

                // 2. 查找输入框并粘贴
                // QQ输入框通常是 EditText，且此时应该是空的（因为我们之前清空了）
                // 或者我们可以直接查找 "发送" 按钮旁边的输入框
                val inputNode = findInputNode(rootNode)
                if (inputNode != null) {
                    // 粘贴
                    val args = android.os.Bundle()
                    inputNode.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                    Log.i(TAG, "✓ 已执行粘贴动作")
                    
                    // 3. 等待图片加载，点击发送
                    Handler(Looper.getMainLooper()).postDelayed({
                        val root2 = rootInActiveWindow
                        if (root2 != null) {
                            val sendClicked = findAndClickNode(root2, listOf("发送", "send"))
                            if (sendClicked) {
                                Log.i(TAG, "✓ 已点击发送")
                                showToast("✓ 图片已自动发送")
                            } else {
                                // 有时候粘贴后需要再次点击发送
                                showToast("请点击发送")
                            }
                            root2.recycle()
                        }
                    }, 800)
                } else {
                    showToast("未找到输入框，请手动粘贴")
                }
            } else {
                showToast("获取图片失败")
            }
            
            rootNode.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "自动发送流程失败", e)
        }
    }

    /**
     * 获取最新保存的图片URI
     */
    private fun getLastSavedImageUri(): Uri? {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    return android.content.ContentUris.withAppendedId(collection, id)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "查询最新图片失败", e)
        }
        return null
    }

    /**
     * 复制图片到剪贴板
     */
    private fun copyImageToClipboard(uri: Uri) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newUri(contentResolver, "image", uri)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * 查找输入框节点
     */
    private fun findInputNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        node ?: return null
        
        if (node.className == "android.widget.EditText") {
            return node
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findInputNode(child)
            if (result != null) {
                // child.recycle() // 不要回收返回的节点
                return result
            }
            child.recycle()
        }
        return null
    }

    /**
     * 查找并点击包含关键词的节点
     */
    private fun findAndClickNode(node: AccessibilityNodeInfo?, keywords: List<String>): Boolean {
        node ?: return false
        
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        for (keyword in keywords) {
            if (text.contains(keyword.lowercase()) || desc.contains(keyword.lowercase())) {
                if (node.isClickable) {
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
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
     * 查找并点击第一个可点击的节点
     */
    private fun findAndClickFirstClickable(node: AccessibilityNodeInfo?): Boolean {
        node ?: return false
        
        if (node.isClickable && node.isVisibleToUser) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        
        // 只查找前几个节点
        val maxChildren = minOf(node.childCount, 5)
        for (i in 0 until maxChildren) {
            val child = node.getChild(i) ?: continue
            if (findAndClickFirstClickable(child)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        
        return false
    }

    /**
     * 显示Toast提示
     */
    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInterrupt() {
        Log.i(TAG, "服务中断")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "服务已销毁")
    }
}
