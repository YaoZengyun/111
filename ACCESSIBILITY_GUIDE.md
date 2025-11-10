# 无障碍服务权限使用指南

## 📋 目录
1. [什么是无障碍服务](#什么是无障碍服务)
2. [为什么需要此权限](#为什么需要此权限)
3. [如何启用](#如何启用)
4. [权限范围](#权限范围)
5. [隐私保护](#隐私保护)
6. [常见问题](#常见问题)

---

## 什么是无障碍服务

无障碍服务（Accessibility Service）是 Android 系统提供的一项功能，最初设计目的是帮助有障碍的用户更好地使用手机。后来也被广泛应用于：
- 自动化工具
- 消息通知增强
- 屏幕内容读取
- 应用间交互

## 为什么需要此权限

本应用需要无障碍服务权限来实现以下功能：

### ✅ 核心功能
1. **监听QQ界面** - 检测你何时打开QQ聊天窗口
2. **读取输入内容** - 获取你在输入框中输入的文字
3. **检测发送操作** - 识别你点击了发送按钮
4. **处理消息** - 将文字叠加到预设图片上

### 🔒 使用范围
- **仅限QQ应用**：配置文件中已限制只监听 `com.tencent.mobileqq` 包名
- **不监听其他应用**：不会读取微信、短信等其他应用的内容
- **本地处理**：所有数据在本地处理，不上传到服务器

---

## 如何启用

### 方法一：应用内引导（推荐）

1. 打开「QQ消息图片助手」应用
2. 点击「去启用无障碍服务」按钮
3. 在弹出的对话框中查看详细说明
4. 点击「去设置」自动跳转到无障碍设置

### 方法二：手动设置

#### 步骤 1：打开无障碍设置
```
设置 → 辅助功能 → 无障碍 → 已下载的应用
```
或
```
设置 → 更多设置 → 无障碍 → 已安装的服务
```
（不同手机品牌路径可能略有不同）

#### 步骤 2：找到本应用
在列表中找到「QQ消息图片助手」

#### 步骤 3：启用服务
1. 点击进入设置页面
2. 打开右上角的开关
3. 阅读权限说明
4. 点击「允许」或「确定」

#### 步骤 4：确认启用
返回应用，查看服务状态应显示为「✓ 无障碍服务已启用」

---

## 权限范围

### 📱 AndroidManifest.xml 配置
```xml
<accessibility-service 
    android:packageNames="com.tencent.mobileqq"
    android:accessibilityEventTypes="typeNotificationStateChanged|typeWindowContentChanged|typeWindowStateChanged"
    android:canRetrieveWindowContent="true" />
```

### 解释
- **packageNames**: 仅监听QQ应用（com.tencent.mobileqq）
- **accessibilityEventTypes**: 
  - `typeWindowContentChanged` - 监听界面内容变化
  - `typeWindowStateChanged` - 监听窗口状态变化
  - `typeNotificationStateChanged` - 监听通知变化
- **canRetrieveWindowContent**: 允许读取窗口内容（用于获取输入框文字）

---

## 隐私保护

### 🛡️ 我们的承诺

#### ✓ 本地处理
- 所有数据在设备本地处理
- 不会上传到任何服务器
- 不会发送到第三方

#### ✓ 最小权限
- 仅请求必要的权限
- 仅在QQ应用中工作
- 不监听其他应用

#### ✓ 透明开放
- 源代码公开
- 可自行审查
- 无隐藏功能

#### ✓ 用户控制
- 可随时关闭功能
- 可随时撤销权限
- 所有设置本地存储

### 📊 数据收集说明

| 数据类型 | 是否收集 | 用途 | 存储位置 |
|---------|---------|------|---------|
| QQ消息内容 | ✓ | 生成图片 | 内存中临时存储，处理后立即清除 |
| 用户设置 | ✓ | 保存配置 | 应用私有目录 |
| 模板图片 | ✓ | 图片合成 | 应用私有目录 |
| 生成的图片 | ✓ | 供用户使用 | 应用私有目录 |
| 个人信息 | ✗ | - | - |
| 联系人 | ✗ | - | - |
| 位置信息 | ✗ | - | - |

---

## 常见问题

### Q1: 为什么无法启用无障碍服务？

**A**: 可能的原因：
1. **MIUI/ColorOS等系统限制**
   - 解决：在设置中找到「自启动管理」，允许本应用自启动
   - 进入「权限管理」，允许所有必要权限

2. **已安装太多无障碍服务**
   - 解决：关闭其他不使用的无障碍服务

3. **系统版本过低**
   - 要求：Android 7.0 (API 24) 或更高

### Q2: 启用后为什么不工作？

**A**: 检查清单：
- [ ] 确认服务状态显示「已启用」
- [ ] 确认应用内开关已打开
- [ ] 确认已选择模板图片
- [ ] 确认QQ版本兼容（建议使用最新版）
- [ ] 尝试重启应用或手机

### Q3: 会影响手机性能吗？

**A**: 影响极小
- 仅在QQ运行时工作
- 使用事件驱动机制，不持续轮询
- 内存占用小于10MB
- 不影响电池续航

### Q4: 会被QQ检测封号吗？

**A**: 风险说明
- 本应用仅读取界面内容，不修改QQ
- 不使用Hook或注入技术
- 理论上是安全的
- 但任何自动化工具都存在一定风险
- **建议仅个人使用，不要用于营销等用途**

### Q5: 如何撤销权限？

**A**: 随时可以关闭
1. **方法一**：在应用内关闭「启用自动处理」开关
2. **方法二**：在系统无障碍设置中关闭服务
3. **方法三**：直接卸载应用

### Q6: 不同手机品牌设置路径

| 品牌 | 路径 |
|-----|------|
| 小米 MIUI | 设置 → 更多设置 → 无障碍 → 已安装的服务 |
| 华为 EMUI | 设置 → 辅助功能 → 无障碍 → 服务 |
| OPPO ColorOS | 设置 → 其他设置 → 无障碍 → 已安装的服务 |
| vivo | 设置 → 更多设置 → 辅助功能 → 已安装的服务 |
| 三星 | 设置 → 辅助功能 → 已安装的服务 |
| 原生 Android | Settings → Accessibility → Downloaded apps |

### Q7: 无障碍服务自动关闭怎么办？

**A**: 部分手机会自动关闭无障碍服务
1. **小米**：设置 → 应用设置 → 应用管理 → QQ消息图片助手 → 省电策略 → 无限制
2. **华为**：设置 → 应用 → 应用启动管理 → QQ消息图片助手 → 手动管理 → 全部允许
3. **OPPO/vivo**：设置 → 电池 → 应用耗电管理 → QQ消息图片助手 → 允许后台运行

---

## 🔧 技术细节

### 代码示例：权限检查

```kotlin
// 检查无障碍服务是否启用
fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedComponentName = "${context.packageName}/.QQAccessibilityService"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(expectedComponentName) == true
}
```

### 代码示例：打开设置

```kotlin
// 打开无障碍设置页面
fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}
```

### 代码示例：监听QQ消息

```kotlin
override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    // 仅处理QQ应用的事件
    if (event?.packageName != "com.tencent.mobileqq") return
    
    // 检查是否启用
    val enabled = getSharedPreferences("settings", MODE_PRIVATE)
        .getBoolean("enabled", false)
    if (!enabled) return
    
    // 处理事件...
}
```

---

## 📞 获取帮助

如果遇到问题：
1. 查看应用内的「服务状态」
2. 检查系统日志
3. 尝试重启应用
4. 提交issue到GitHub（如果是开源项目）

---

## ⚖️ 法律声明

1. 本应用仅供学习和个人使用
2. 请遵守QQ用户协议和相关法律法规
3. 不得用于商业目的或大规模自动化操作
4. 使用本应用产生的任何后果由用户自行承担

---

## 📝 更新日志

### v1.0.0 (2025-11-10)
- ✨ 初始版本
- ✅ 支持无障碍服务监听QQ消息
- ✅ 支持文字叠加到图片
- ✅ 提供详细的权限说明和引导

---

**最后更新**: 2025年11月10日
