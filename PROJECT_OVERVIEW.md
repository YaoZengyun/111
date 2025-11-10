# 📁 项目文件总览

## 项目结构

```
QQMessageImageApp/
│
├── 📄 README.md                          # 项目主文档
├── 📄 QUICK_START.md                     # 5分钟快速上手指南  
├── 📄 ACCESSIBILITY_GUIDE.md             # 无障碍权限详细指南
├── 📄 UPDATE_NOTES.md                    # 更新说明文档
├── 📄 PROJECT_OVERVIEW.md                # 本文件
│
├── 📄 build.gradle                       # 项目级Gradle配置
├── 📄 settings.gradle                    # Gradle设置
├── 📄 gradle.properties                  # Gradle属性
│
└── app/
    ├── 📄 build.gradle                   # 应用级Gradle配置
    ├── 📄 proguard-rules.pro             # 代码混淆规则
    │
    └── src/main/
        │
        ├── 📄 AndroidManifest.xml        # 应用清单（权限、组件）
        │
        ├── java/com/example/qqmessageimage/
        │   ├── 📱 MainActivity.kt                 # 主界面Activity
        │   ├── 🤖 QQAccessibilityService.kt       # 无障碍服务
        │   ├── 🔧 AccessibilityHelper.kt          # 无障碍辅助类
        │   └── 🔐 PermissionManager.kt            # 权限管理类
        │
        └── res/
            ├── layout/
            │   ├── 📐 activity_main.xml           # 主界面布局
            │   └── 📐 dialog_accessibility_guide.xml  # 引导对话框布局
            │
            ├── values/
            │   └── 📝 strings.xml                 # 字符串资源
            │
            └── xml/
                ├── ⚙️ accessibility_service_config.xml  # 无障碍服务配置
                └── ⚙️ file_paths.xml                   # 文件提供者路径
```

---

## 📚 文档说明

| 文件 | 用途 | 适合人群 |
|-----|------|---------|
| **README.md** | 项目完整文档，包含所有信息 | 所有人 |
| **QUICK_START.md** | 5分钟快速上手教程 | 新手用户 |
| **ACCESSIBILITY_GUIDE.md** | 无障碍权限详细说明，含FAQ | 想深入了解的用户 |
| **UPDATE_NOTES.md** | 功能更新说明，技术细节 | 开发者 |
| **PROJECT_OVERVIEW.md** | 项目文件总览（本文件） | 开发者 |

---

## 🎯 核心文件详解

### 1. MainActivity.kt (主界面)
**功能**：
- ✅ 显示应用主界面
- ✅ 管理用户设置（文字位置、大小、颜色等）
- ✅ 检查和显示权限状态
- ✅ 引导用户启用无障碍服务
- ✅ 选择和保存模板图片
- ✅ 实时更新状态显示

**关键方法**：
```kotlin
onCreate()                    // 初始化
onResume()                    // 更新状态
updateServiceStatus()         // 更新服务状态
updatePermissionsChecklist()  // 更新权限检查清单
showAccessibilityGuide()      // 显示引导对话框
saveTemplateImage()           // 保存模板图片
```

### 2. QQAccessibilityService.kt (核心服务)
**功能**：
- ✅ 监听QQ应用的界面事件
- ✅ 查找输入框中的文字
- ✅ 检测发送按钮点击
- ✅ 将文字绘制到模板图片上
- ✅ 保存处理后的图片

**关键方法**：
```kotlin
onAccessibilityEvent()   // 处理无障碍事件
processQQMessage()       // 处理QQ消息
findMessageText()        // 查找消息文字
findSendButton()         // 查找发送按钮
processAndSendMessage()  // 处理并保存图片
```

### 3. AccessibilityHelper.kt (辅助工具)
**功能**：
- ✅ 检查无障碍服务状态
- ✅ 打开系统无障碍设置
- ✅ 提供设置步骤说明
- ✅ 提供权限使用说明

**关键方法**：
```kotlin
isAccessibilityServiceEnabled()  // 检查是否启用
openAccessibilitySettings()      // 打开设置
getServiceStatusDescription()    // 获取状态描述
getSetupInstructions()           // 获取设置步骤
getPermissionExplanation()       // 获取权限说明
```

### 4. PermissionManager.kt (权限管理)
**功能**：
- ✅ 管理存储权限（兼容不同Android版本）
- ✅ 检查所有必要权限
- ✅ 提供权限说明

**关键方法**：
```kotlin
getRequiredPermissions()        // 获取所需权限列表
hasStoragePermission()          // 检查存储权限
hasAllRequiredPermissions()     // 检查所有权限
getPermissionsStatus()          // 获取所有权限状态
```

---

## 🎨 界面文件详解

### activity_main.xml (主界面)
**布局结构**：
```
ScrollView
└── LinearLayout
    ├── Card 1: 服务状态
    │   ├── 状态文字
    │   ├── 权限检查清单（3项）
    │   ├── 设置按钮
    │   └── 提示文字
    │
    ├── Card 2: 功能开关
    │   └── Switch开关
    │
    ├── Card 3: 模板图片
    │   ├── 图片预览
    │   └── 选择按钮
    │
    └── Card 4: 文字设置
        ├── X坐标调节
        ├── Y坐标调节
        ├── 文字大小调节
        ├── 最大宽度调节
        ├── 颜色选择
        └── 保存按钮
```

### dialog_accessibility_guide.xml (引导对话框)
**内容**：
- 📱 设置步骤说明
- 🔒 权限用途说明
- 💡 使用提示

---

## ⚙️ 配置文件详解

### AndroidManifest.xml
**关键配置**：
```xml
<!-- 权限声明 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- 主Activity -->
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- 无障碍服务 -->
<service android:name=".QQAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>

<!-- 文件提供者 -->
<provider android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### accessibility_service_config.xml
**无障碍服务配置**：
```xml
<!-- 仅监听QQ -->
<accessibility-service 
    android:packageNames="com.tencent.mobileqq"
    
    <!-- 监听的事件类型 -->
    android:accessibilityEventTypes="
        typeNotificationStateChanged|
        typeWindowContentChanged|
        typeWindowStateChanged"
    
    <!-- 允许读取窗口内容 -->
    android:canRetrieveWindowContent="true" />
```

---

## 📦 依赖库

### app/build.gradle
```gradle
dependencies {
    // Android核心库
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // Material Design组件
    implementation 'com.google.android.material:material:1.11.0'
    
    // 约束布局
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // 偏好设置
    implementation 'androidx.preference:preference-ktx:1.2.1'
}
```

---

## 🔄 数据流程

### 用户操作流程
```
用户打开应用
    ↓
MainActivity.onCreate()
    ↓
检查权限状态
    ↓
[未启用] → 显示引导对话框 → 用户去设置 → 返回 → 更新状态
[已启用] → 直接显示主界面
    ↓
用户选择模板图片
    ↓
调整文字参数
    ↓
保存设置到SharedPreferences
    ↓
打开功能开关
    ↓
用户去QQ发送消息
    ↓
QQAccessibilityService接收事件
    ↓
读取输入内容
    ↓
加载模板图片
    ↓
绘制文字到图片
    ↓
保存到本地
    ↓
完成
```

### 无障碍服务流程
```
QQ界面变化
    ↓
触发 onAccessibilityEvent()
    ↓
检查包名 = "com.tencent.mobileqq"?
    ↓ 是
检查功能是否启用?
    ↓ 是
检查模板是否存在?
    ↓ 是
processQQMessage()
    ↓
找到输入框文字
    ↓
检测到发送按钮点击
    ↓
processAndSendMessage()
    ↓
绘制图片
    ↓
保存
```

---

## 💾 数据存储

### SharedPreferences (settings)
```kotlin
// 保存的设置
"enabled"          : Boolean  // 功能是否启用
"text_x"           : Int      // X坐标
"text_y"           : Int      // Y坐标
"text_size"        : Int      // 文字大小
"max_width"        : Int      // 最大宽度
"text_color"       : Int      // 文字颜色
"has_shown_accessibility_guide" : Boolean  // 是否已显示过引导
```

### 文件存储
```
/data/data/com.example.qqmessageimage/
├── files/
│   └── template.png              # 模板图片
├── shared_prefs/
│   └── settings.xml              # 设置数据
└── external_files/
    └── processed_*.png           # 处理后的图片
```

---

## 🔍 调试技巧

### 查看Log
```bash
# 过滤应用日志
adb logcat | grep "QQMessage"

# 查看无障碍服务日志
adb logcat | grep "Accessibility"
```

### 检查权限
```bash
# 查看无障碍服务状态
adb shell settings get secure enabled_accessibility_services

# 查看应用权限
adb shell dumpsys package com.example.qqmessageimage | grep permission
```

### 模拟事件
在Android Studio中使用 Layout Inspector 查看QQ界面结构。

---

## 🎓 学习要点

### 1. 无障碍服务
- `AccessibilityService` 基础
- `AccessibilityEvent` 事件处理
- `AccessibilityNodeInfo` 节点遍历
- 配置文件编写

### 2. 权限管理
- 运行时权限请求
- 不同Android版本适配
- 系统设置跳转

### 3. 图片处理
- `Bitmap` 操作
- `Canvas` 绘图
- `TextPaint` 文字渲染
- `StaticLayout` 多行文字

### 4. 用户体验
- Material Design
- 引导流程设计
- 状态反馈
- 错误处理

### 5. 数据持久化
- `SharedPreferences`
- 文件存储
- `FileProvider`

---

## 📊 技术栈

| 技术 | 用途 |
|-----|------|
| Kotlin | 开发语言 |
| Android SDK | 应用框架 |
| Material Components | UI组件 |
| Accessibility Service | 监听QQ |
| Canvas API | 图片处理 |
| SharedPreferences | 数据存储 |
| FileProvider | 文件分享 |

---

## 🚀 快速导航

- 想快速上手？→ [QUICK_START.md](QUICK_START.md)
- 想了解权限？→ [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md)
- 想看完整文档？→ [README.md](README.md)
- 想了解更新？→ [UPDATE_NOTES.md](UPDATE_NOTES.md)

---

## 📝 开发笔记

### 已实现
- ✅ 基础功能
- ✅ 权限管理
- ✅ 用户引导
- ✅ 状态监控
- ✅ 完整文档

### 待优化
- ⏳ 实时预览
- ⏳ 拖拽调整位置
- ⏳ 多模板管理
- ⏳ 配置导入导出
- ⏳ 自动发送（技术限制）

---

**最后更新**: 2025年11月10日
