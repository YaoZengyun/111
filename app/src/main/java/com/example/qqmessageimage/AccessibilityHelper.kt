package com.example.qqmessageimage

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils

/**
 * 辅助功能权限帮助类
 */
object AccessibilityHelper {
    
    /**
     * 检查无障碍服务是否已启用
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponentName = "${context.packageName}/${QQAccessibilityService::class.java.canonicalName}"
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        
        if (enabledServicesSetting.isNullOrEmpty()) {
            return false
        }
        
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }
        
        return false
    }
    
    /**
     * 打开无障碍设置页面
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果无法打开无障碍设置，尝试打开通用设置
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    
    /**
     * 获取无障碍服务状态描述
     */
    fun getServiceStatusDescription(context: Context): String {
        return if (isAccessibilityServiceEnabled(context)) {
            "✓ 无障碍服务已启用"
        } else {
            "✗ 无障碍服务未启用"
        }
    }
    
    /**
     * 获取设置步骤说明
     */
    fun getSetupInstructions(): List<String> {
        return listOf(
            "1. 点击下方按钮打开无障碍设置",
            "2. 找到并点击「QQ消息图片助手」",
            "3. 打开右上角的开关启用服务",
            "4. 在弹出的对话框中点击「允许」或「确定」",
            "5. 返回应用，确认服务状态为「已启用」"
        )
    }
    
    /**
     * 获取权限说明
     */
    fun getPermissionExplanation(): String {
        return """
            无障碍服务用途说明：
            
            • 监听QQ聊天界面的输入内容
            • 检测消息发送操作
            • 读取输入框中的文字
            
            我们承诺：
            ✓ 仅在QQ应用中工作
            ✓ 不会收集或上传任何数据
            ✓ 不会干扰其他应用
            ✓ 可随时在设置中关闭
        """.trimIndent()
    }
}
