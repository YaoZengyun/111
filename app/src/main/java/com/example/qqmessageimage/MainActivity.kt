package com.example.qqmessageimage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_AREA_SELECT = 1001
    }

    private lateinit var switchEnable: SwitchMaterial
    private lateinit var switchAutoSend: SwitchMaterial
    private lateinit var tvServiceStatus: TextView
    private lateinit var tvAccessibilityStatus: TextView
    private lateinit var tvStorageStatus: TextView
    private lateinit var tvTemplateStatus: TextView
    private lateinit var ivTemplatePreview: ImageView
    private lateinit var btnSelectTemplate: Button
    private lateinit var btnSelectArea: Button
    private lateinit var btnOpenAccessibilitySettings: Button
    private lateinit var btnSaveSettings: Button
    private lateinit var btnSelectColor: Button
    private lateinit var viewColorPreview: android.view.View
    private lateinit var seekBarX: SeekBar
    private lateinit var seekBarY: SeekBar
    private lateinit var seekBarWidth: SeekBar
    private lateinit var seekBarHeight: SeekBar
    private lateinit var seekBarTextSize: SeekBar
    private lateinit var seekBarLineSpacing: SeekBar
    private lateinit var tvXValue: TextView
    private lateinit var tvYValue: TextView
    private lateinit var tvWidthValue: TextView
    private lateinit var tvHeightValue: TextView
    private lateinit var tvTextSizeValue: TextView
    private lateinit var tvLineSpacingValue: TextView

    private var selectedColor = Color.BLACK
    private var selectedBracketColor = Color.parseColor("#800080")

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            saveTemplateImage(it)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadSettings()
        checkPermissions()
        updateServiceStatus()
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        checkAndPromptAccessibility()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_AREA_SELECT && resultCode == RESULT_OK) {
            // 重新加载设置以更新坐标值
            loadSettings()
            Toast.makeText(this, "文字区域已更新", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        switchEnable = findViewById(R.id.switchEnable)
        switchAutoSend = findViewById(R.id.switchAutoSend)
        tvServiceStatus = findViewById(R.id.tvServiceStatus)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)
        tvStorageStatus = findViewById(R.id.tvStorageStatus)
        tvTemplateStatus = findViewById(R.id.tvTemplateStatus)
        ivTemplatePreview = findViewById(R.id.ivTemplatePreview)
        btnSelectTemplate = findViewById(R.id.btnSelectTemplate)
        btnSelectArea = findViewById(R.id.btnSelectArea)
        btnOpenAccessibilitySettings = findViewById(R.id.btnOpenAccessibilitySettings)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        btnSelectColor = findViewById(R.id.btnSelectColor)
        viewColorPreview = findViewById(R.id.viewColorPreview)
        seekBarX = findViewById(R.id.seekBarX)
        seekBarY = findViewById(R.id.seekBarY)
        seekBarWidth = findViewById(R.id.seekBarWidth)
        seekBarHeight = findViewById(R.id.seekBarHeight)
        seekBarTextSize = findViewById(R.id.seekBarTextSize)
        seekBarLineSpacing = findViewById(R.id.seekBarLineSpacing)
        tvXValue = findViewById(R.id.tvXValue)
        tvYValue = findViewById(R.id.tvYValue)
        tvWidthValue = findViewById(R.id.tvWidthValue)
        tvHeightValue = findViewById(R.id.tvHeightValue)
        tvTextSizeValue = findViewById(R.id.tvTextSizeValue)
        tvLineSpacingValue = findViewById(R.id.tvLineSpacingValue)

        // 功能开关
        switchEnable.setOnCheckedChangeListener { _, isChecked ->
            savePreference("enabled", isChecked)
        }
        
        // 自动发送开关
        switchAutoSend.setOnCheckedChangeListener { _, isChecked ->
            savePreference("auto_send", isChecked)
            if (isChecked) {
                showAutoSendWarning()
            }
        }

        // 打开无障碍设置
        btnOpenAccessibilitySettings.setOnClickListener {
            showAccessibilityGuide()
        }
        
        // 长按显示详细说明
        btnOpenAccessibilitySettings.setOnLongClickListener {
            showAccessibilityGuide()
            true
        }

        // 选择模板图片
        btnSelectTemplate.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // 框选文字区域
        btnSelectArea.setOnClickListener {
            val templateFile = File(filesDir, "template.png")
            if (!templateFile.exists()) {
                Toast.makeText(this, "请先选择底图", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, AreaSelectorActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_AREA_SELECT)
        }

        // SeekBar监听
        setupSeekBar(seekBarX, tvXValue, "text_x")
        setupSeekBar(seekBarY, tvYValue, "text_y")
        setupSeekBar(seekBarWidth, tvWidthValue, "text_width")
        setupSeekBar(seekBarHeight, tvHeightValue, "text_height")
        setupSeekBar(seekBarTextSize, tvTextSizeValue, "text_size")
        setupSeekBarFloat(seekBarLineSpacing, tvLineSpacingValue, "line_spacing")

        // 颜色选择
        btnSelectColor.setOnClickListener {
            showColorPicker(isNormalColor = true)
        }
        
        // 中括号颜色选择（长按）
        btnSelectColor.setOnLongClickListener {
            showColorPicker(isNormalColor = false)
            true
        }

        // 保存设置
        btnSaveSettings.setOnClickListener {
            saveAllSettings()
        }
    }

    private fun setupSeekBar(seekBar: SeekBar, textView: TextView, prefKey: String) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    savePreference(prefKey, it.progress)
                }
            }
        })
    }
    
    private fun setupSeekBarFloat(seekBar: SeekBar, textView: TextView, prefKey: String) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress / 100f
                textView.text = String.format("%.2f", value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    val value = it.progress / 100f
                    savePreference(prefKey, value)
                }
            }
        })
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        switchEnable.isChecked = prefs.getBoolean("enabled", false)
        switchAutoSend.isChecked = prefs.getBoolean("auto_send", false)
        
        val x = prefs.getInt("text_x", 100)
        val y = prefs.getInt("text_y", 100)
        val width = prefs.getInt("text_width", 500)
        val height = prefs.getInt("text_height", 200)
        val textSize = prefs.getInt("text_size", 40)
        val lineSpacing = prefs.getFloat("line_spacing", 0.15f)
        selectedColor = prefs.getInt("text_color", Color.BLACK)
        selectedBracketColor = prefs.getInt("bracket_color", Color.parseColor("#800080"))

        seekBarX.progress = x
        seekBarY.progress = y
        seekBarWidth.progress = width
        seekBarHeight.progress = height
        seekBarTextSize.progress = textSize
        seekBarLineSpacing.progress = (lineSpacing * 100).toInt()
        
        tvXValue.text = x.toString()
        tvYValue.text = y.toString()
        tvWidthValue.text = width.toString()
        tvHeightValue.text = height.toString()
        tvTextSizeValue.text = textSize.toString()
        tvLineSpacingValue.text = String.format("%.2f", lineSpacing)
        
        viewColorPreview.setBackgroundColor(selectedColor)

        // 加载模板图片
        loadTemplateImage()
    }

    private fun saveAllSettings() {
        savePreference("text_color", selectedColor)
        savePreference("bracket_color", selectedBracketColor)
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
    }

    private fun savePreference(key: String, value: Any) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }

    private fun saveTemplateImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val file = File(filesDir, "template.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            ivTemplatePreview.setImageBitmap(bitmap)
            Toast.makeText(this, "模板图片已保存", Toast.LENGTH_SHORT).show()
            updatePermissionsChecklist()  // 更新状态
        } catch (e: Exception) {
            Toast.makeText(this, "保存图片失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTemplateImage() {
        val file = File(filesDir, "template.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            ivTemplatePreview.setImageBitmap(bitmap)
        }
    }

    private fun showColorPicker(isNormalColor: Boolean = true) {
        val colors = arrayOf(
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, 
            Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA,
            Color.parseColor("#800080"), Color.parseColor("#FFA500")
        )
        val colorNames = arrayOf(
            "黑色", "白色", "红色", "绿色", 
            "蓝色", "黄色", "青色", "品红",
            "紫色", "橙色"
        )

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(if (isNormalColor) "选择文字颜色" else "选择中括号颜色")
        builder.setItems(colorNames) { _, which ->
            if (isNormalColor) {
                selectedColor = colors[which]
                viewColorPreview.setBackgroundColor(selectedColor)
            } else {
                selectedBracketColor = colors[which]
                Toast.makeText(this, "中括号颜色已设置", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    private fun updateServiceStatus() {
        val isEnabled = AccessibilityHelper.isAccessibilityServiceEnabled(this)
        tvServiceStatus.text = AccessibilityHelper.getServiceStatusDescription(this)
        
        if (isEnabled) {
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            btnOpenAccessibilitySettings.text = "无障碍服务设置"
        } else {
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            btnOpenAccessibilitySettings.text = "去启用无障碍服务"
        }
        
        // 更新权限检查清单
        updatePermissionsChecklist()
    }
    
    private fun updatePermissionsChecklist() {
        val accessibilityEnabled = AccessibilityHelper.isAccessibilityServiceEnabled(this)
        val storageEnabled = PermissionManager.hasStoragePermission(this)
        val templateExists = File(filesDir, "template.png").exists()
        
        // 无障碍服务
        tvAccessibilityStatus.text = if (accessibilityEnabled) {
            "✓ 无障碍服务"
        } else {
            "✗ 无障碍服务（未启用）"
        }
        tvAccessibilityStatus.setTextColor(
            ContextCompat.getColor(
                this,
                if (accessibilityEnabled) android.R.color.holo_green_dark 
                else android.R.color.holo_red_dark
            )
        )
        
        // 存储权限
        tvStorageStatus.text = if (storageEnabled) {
            "✓ 存储权限"
        } else {
            "✗ 存储权限（未授予）"
        }
        tvStorageStatus.setTextColor(
            ContextCompat.getColor(
                this,
                if (storageEnabled) android.R.color.holo_green_dark 
                else android.R.color.holo_red_dark
            )
        )
        
        // 模板图片
        tvTemplateStatus.text = if (templateExists) {
            "✓ 已选择模板"
        } else {
            "✗ 未选择模板"
        }
        tvTemplateStatus.setTextColor(
            ContextCompat.getColor(
                this,
                if (templateExists) android.R.color.holo_green_dark 
                else android.R.color.holo_orange_dark
            )
        )
    }
    
    private fun checkAndPromptAccessibility() {
        // 如果服务未启用且用户首次打开应用，显示引导
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hasShownGuide = prefs.getBoolean("has_shown_accessibility_guide", false)
        
        if (!AccessibilityHelper.isAccessibilityServiceEnabled(this) && !hasShownGuide) {
            android.os.Handler(mainLooper).postDelayed({
                showAccessibilityGuide()
                prefs.edit().putBoolean("has_shown_accessibility_guide", true).apply()
            }, 500)
        }
    }
    
    private fun showAccessibilityGuide() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_accessibility_guide, null)
        val tvSteps = dialogView.findViewById<TextView>(R.id.tvSteps)
        val tvExplanation = dialogView.findViewById<TextView>(R.id.tvExplanation)
        
        // 设置步骤说明
        val steps = AccessibilityHelper.getSetupInstructions()
        tvSteps.text = steps.joinToString("\n\n")
        
        // 设置权限说明
        tvExplanation.text = AccessibilityHelper.getPermissionExplanation()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("去设置") { _, _ ->
                AccessibilityHelper.openAccessibilitySettings(this)
            }
            .setNegativeButton("稍后", null)
            .setCancelable(true)
            .show()
    }

    private fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        val needRequest = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needRequest) {
            permissionLauncher.launch(permissions)
        }
    }
    
    private fun showAutoSendWarning() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("自动发送说明")
            .setMessage("""
                自动发送功能使用无障碍服务模拟点击操作：
                
                1. 点击"相册"或"+"按钮
                2. 选择刚生成的图片
                3. 点击"发送"按钮
                
                ⚠️ 注意事项：
                • 依赖QQ界面结构，可能不稳定
                • 不同QQ版本界面不同，成功率不同
                • 如果失败，图片会保存到本地
                • 建议先测试，再正式使用
                
                是否继续启用？
            """.trimIndent())
            .setPositiveButton("继续启用") { _, _ -> }
            .setNegativeButton("暂不启用") { _, _ ->
                switchAutoSend.isChecked = false
            }
            .show()
    }
}
