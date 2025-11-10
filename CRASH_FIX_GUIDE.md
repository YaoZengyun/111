# 应用闪退问题排查与修复指南

## 🐛 已知问题和修复

### 问题1：安装后点击闪退

#### 可能原因：
1. ✅ **缺少主题资源** - 已修复
2. ✅ **缺少颜色资源** - 已修复  
3. ⚠️ **权限问题** - 需要用户授权
4. ⚠️ **Android版本兼容性** - 检查系统版本

#### 已完成的修复：
本次更新添加了：
- `res/values/themes.xml` - 应用主题定义
- `res/values/colors.xml` - 颜色资源定义

这两个文件之前缺失，会导致应用启动时找不到主题而崩溃。

---

## 📊 排查步骤

### 步骤1：查看崩溃日志

通过ADB查看详细错误信息：

```bash
# 连接手机后执行
adb logcat | findstr "AndroidRuntime"

# 或查看完整日志
adb logcat -v time > crash_log.txt
```

**常见错误信息：**

1. **ResourceNotFoundException**
   ```
   android.content.res.Resources$NotFoundException: 
   Resource ID #0x... type #0x... is not valid
   ```
   **原因**：缺少资源文件（主题、颜色、图标等）
   **状态**：✅ 已修复

2. **SecurityException**
   ```
   java.lang.SecurityException: Permission denied
   ```
   **原因**：权限未授予
   **解决**：安装后在设置中授予权限

3. **ClassNotFoundException**
   ```
   java.lang.ClassNotFoundException
   ```
   **原因**：代码混淆或依赖缺失
   **状态**：不太可能（Debug版本不混淆）

### 步骤2：检查Android版本

**最低要求**：Android 7.0 (API 24)

```bash
# 查看设备Android版本
adb shell getprop ro.build.version.release
```

如果版本低于7.0，应用无法安装或运行。

### 步骤3：检查权限

应用需要以下权限：

#### 运行时权限（需要手动授予）：
- ✅ 存储权限（READ_EXTERNAL_STORAGE / READ_MEDIA_IMAGES）
- ✅ 无障碍服务权限

#### 检查权限状态：
```bash
# 查看应用权限
adb shell dumpsys package com.example.qqmessageimage | findstr permission
```

### 步骤4：检查安装包完整性

```bash
# 验证APK签名
adb shell pm list packages | findstr qqmessage

# 卸载后重新安装
adb uninstall com.example.qqmessageimage
adb install app-debug.apk
```

---

## 🔧 修复方案

### 方案A：等待GitHub Actions重新构建（推荐）

1. 访问：https://github.com/YaoZengyun/111/actions
2. 等待最新构建完成（已添加主题资源）
3. 从Releases下载新的APK
4. 重新安装

### 方案B：手动修复当前代码

如果你想本地构建，确保以下文件存在：

**1. app/src/main/res/values/themes.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.QQMessageImage" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="colorPrimary">#2196F3</item>
        <item name="colorPrimaryDark">#1976D2</item>
        <item name="colorAccent">#FF4081</item>
    </style>
</resources>
```

**2. app/src/main/res/values/colors.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#2196F3</color>
    <color name="colorPrimaryDark">#1976D2</color>
    <color name="colorAccent">#FF4081</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

**3. 本地构建**
```bash
.\gradlew.bat assembleDebug
```

---

## 📱 首次启动指南

即使修复了闪退，首次启动可能需要：

### 1. 授予存储权限

**Android 13+：**
- 安装后首次打开
- 点击"允许"授予照片和媒体权限

**Android 12及以下：**
- 设置 → 应用 → QQ消息图片助手 → 权限
- 开启"存储"权限

### 2. 启用无障碍服务

这是应用核心功能，必须启用：

**路径：**
```
设置 → 辅助功能 → 无障碍 → 已下载的应用 → QQ消息图片助手 → 开启
```

**或通过应用内引导：**
1. 打开应用
2. 点击"去设置"按钮
3. 在无障碍设置中找到应用并启用

### 3. 配置功能

首次使用建议：
1. ✅ 选择模板图片（base.png）
2. ✅ 调整文字区域
3. ✅ 开启"启用自动处理"
4. ⚠️ 谨慎开启"自动发送"（实验性功能）

---

## 🧪 测试建议

### 基础测试：
```
1. 安装APK
2. 打开应用（不闪退）✓
3. 查看主界面（正常显示）✓
4. 授予权限 ✓
5. 选择模板图片 ✓
6. 测试QQ消息处理 ✓
```

### 完整测试：
```
1. 基础功能测试 ✓
2. 文字叠加测试 ✓
3. 图片保存测试 ✓
4. 自动发送测试（可选）⚠️
```

---

## 📋 提交Bug报告

如果仍然闪退，请提供以下信息：

### 必需信息：
- [ ] Android版本（设置 → 关于手机 → Android版本）
- [ ] 设备型号
- [ ] 崩溃日志（adb logcat）
- [ ] APK来源（GitHub Actions哪次构建）

### 获取崩溃日志：
```bash
# Windows PowerShell
adb logcat -v time *:E > crash_log.txt

# 重现崩溃后，按Ctrl+C停止
# 查看 crash_log.txt 文件
```

### 日志示例：
```
01-01 12:00:00.123  1234  1234 E AndroidRuntime: FATAL EXCEPTION: main
01-01 12:00:00.123  1234  1234 E AndroidRuntime: Process: com.example.qqmessageimage
01-01 12:00:00.123  1234  1234 E AndroidRuntime: java.lang.RuntimeException: ...
```

---

## ✅ 修复进度

| 问题 | 状态 | 说明 |
|------|------|------|
| 缺少应用图标 | ✅ 已修复 | 创建drawable XML图标 |
| XML语法错误 | ✅ 已修复 | 添加结束标签 |
| Kotlin导入缺失 | ✅ 已修复 | 添加RectF导入 |
| Actions权限不足 | ✅ 已修复 | 添加permissions配置 |
| **缺少主题资源** | ✅ **刚修复** | 添加themes.xml |
| **缺少颜色资源** | ✅ **刚修复** | 添加colors.xml |

---

## 🎯 下一步

1. **等待GitHub Actions完成构建**（约8-10分钟）
2. **从Releases下载新APK**
3. **卸载旧版本**（如已安装）
4. **安装新版本**
5. **测试启动**

**构建状态：** https://github.com/YaoZengyun/111/actions

---

## 💡 预防措施

为避免类似问题，已完成：
- ✅ 完善资源文件检查
- ✅ 添加必需的Android资源
- ✅ 本地构建测试流程

**建议：**
- 每次下载最新的GitHub Actions构建
- 不要使用失败构建的APK
- 查看Actions日志确认构建成功

---

**更新时间：** 2025-01-11  
**当前版本：** v1.0.6（待构建）  
**修复重点：** 添加主题和颜色资源，解决启动闪退
