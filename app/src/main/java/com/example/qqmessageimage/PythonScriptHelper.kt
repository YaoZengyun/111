package com.example.qqmessageimage

import android.content.Context
import android.util.Base64
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Python脚本调用工具
 * 用于调用text_fit_draw.py处理图片
 */
object PythonScriptHelper {
    
    /**
     * 调用Python脚本绘制文字到图片
     * 
     * @param context Context
     * @param text 要绘制的文字
     * @param baseImagePath base.png的路径
     * @param topLeft 文字区域左上角坐标 [x, y]
     * @param bottomRight 文字区域右下角坐标 [x, y]
     * @param color 文字颜色 [r, g, b]
     * @param align 水平对齐 "left", "center", "right"
     * @param valign 垂直对齐 "top", "middle", "bottom"
     * @param lineSpacing 行间距
     * @param bracketColor 括号颜色 [r, g, b]
     * @param overlayPath 覆盖图层路径（可选）
     * @param fontPath 字体文件路径（可选）
     * @param maxFontHeight 最大字体高度（可选）
     * 
     * @return 处理后的图片字节数组，失败返回null
     */
    fun drawTextWithPython(
        context: Context,
        text: String,
        baseImagePath: String,
        topLeft: IntArray = intArrayOf(0, 0),
        bottomRight: IntArray = intArrayOf(800, 600),
        color: IntArray = intArrayOf(0, 0, 0),
        align: String = "center",
        valign: String = "middle",
        lineSpacing: Float = 0.15f,
        bracketColor: IntArray = intArrayOf(128, 0, 128),
        overlayPath: String? = null,
        fontPath: String? = null,
        maxFontHeight: Int? = null
    ): ByteArray? {
        return try {
            // 检查Python脚本是否存在
            val scriptFile = File(context.filesDir.parentFile, "text_fit_draw.py")
            if (!scriptFile.exists()) {
                // 尝试从assets复制
                copyScriptFromAssets(context, scriptFile)
            }
            
            // 构建JSON参数
            val params = JSONObject().apply {
                put("image_source", baseImagePath)
                put("top_left", org.json.JSONArray(topLeft.toList()))
                put("bottom_right", org.json.JSONArray(bottomRight.toList()))
                put("text", text)
                put("color", org.json.JSONArray(color.toList()))
                put("align", align)
                put("valign", valign)
                put("line_spacing", lineSpacing)
                put("bracket_color", org.json.JSONArray(bracketColor.toList()))
                overlayPath?.let { put("image_overlay", it) }
                fontPath?.let { put("font_path", it) }
                maxFontHeight?.let { put("max_font_height", it) }
            }
            
            // 调用Python脚本
            val process = Runtime.getRuntime().exec(arrayOf(
                "python3",
                scriptFile.absolutePath,
                params.toString()
            ))
            
            // 读取输出
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine() ?: return null
            reader.close()
            
            // 等待进程结束
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                // 读取错误信息
                val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                val error = errorReader.readText()
                errorReader.close()
                android.util.Log.e("PythonScript", "Error: $error")
                return null
            }
            
            // 解码base64结果
            Base64.decode(output, Base64.DEFAULT)
        } catch (e: Exception) {
            android.util.Log.e("PythonScript", "Failed to execute Python script", e)
            null
        }
    }
    
    /**
     * 从assets复制Python脚本到文件系统
     */
    private fun copyScriptFromAssets(context: Context, targetFile: File) {
        try {
            val inputStream = context.assets.open("text_fit_draw.py")
            targetFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
        } catch (e: Exception) {
            android.util.Log.e("PythonScript", "Failed to copy script from assets", e)
        }
    }
    
    /**
     * 检查Python环境是否可用
     */
    fun isPythonAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("python3", "--version"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 检查PIL库是否已安装
     */
    fun isPillowInstalled(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("python3", "-c", "import PIL"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }
}
