package com.example.qqmessageimage

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * 文字区域框选Activity
 * 让用户在底图上直观地框选文字显示区域
 */
class AreaSelectorActivity : AppCompatActivity() {

    private lateinit var areaSelectorView: AreaSelectorView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_selector)
        
        areaSelectorView = findViewById(R.id.areaSelectorView)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        
        // 加载底图
        loadTemplateImage()
        
        // 取消按钮
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        
        // 确定按钮
        btnConfirm.setOnClickListener {
            saveSelection()
        }
    }
    
    /**
     * 加载模板图片
     */
    private fun loadTemplateImage() {
        try {
            val templateFile = File(filesDir, "template.png")
            
            if (templateFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(templateFile.absolutePath)
                areaSelectorView.setImage(bitmap)
            } else {
                Toast.makeText(this, "模板图片不存在，请先上传底图", Toast.LENGTH_LONG).show()
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "加载图片失败: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    /**
     * 保存用户选择的区域
     */
    private fun saveSelection() {
        try {
            val rect = areaSelectorView.getSelectionInImage()
            
            // 保存到SharedPreferences
            val prefs = getSharedPreferences("settings", MODE_PRIVATE)
            prefs.edit().apply {
                putInt("text_x", rect.left.toInt())
                putInt("text_y", rect.top.toInt())
                putInt("text_width", rect.width().toInt())
                putInt("text_height", rect.height().toInt())
                apply()
            }
            
            Toast.makeText(
                this,
                "已保存: 位置(${rect.left.toInt()}, ${rect.top.toInt()}) 大小${rect.width().toInt()}x${rect.height().toInt()}",
                Toast.LENGTH_SHORT
            ).show()
            
            setResult(Activity.RESULT_OK)
            finish()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
