package com.example.qqmessageimage

import android.content.Context
import android.os.Build

/**
 * 权限管理工具类
 */
object PermissionManager {
    
    /**
     * 获取所需权限列表
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    
    /**
     * 检查存储权限
     */
    fun hasStoragePermission(context: Context): Boolean {
        return getRequiredPermissions().all { permission ->
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 获取权限说明
     */
    fun getPermissionExplanation(permission: String): String {
        return when {
            permission.contains("STORAGE") -> "用于选择和保存模板图片"
            permission.contains("READ_MEDIA_IMAGES") -> "用于读取相册中的图片作为模板"
            else -> "应用正常运行所需"
        }
    }
    
    /**
     * 获取所有权限状态
     */
    fun getPermissionsStatus(context: Context): Map<String, Boolean> {
        return mapOf(
            "无障碍服务" to AccessibilityHelper.isAccessibilityServiceEnabled(context),
            "存储权限" to hasStoragePermission(context)
        )
    }
    
    /**
     * 检查是否所有必要权限都已授予
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return AccessibilityHelper.isAccessibilityServiceEnabled(context) &&
                hasStoragePermission(context)
    }
}
