# Android Studio 完整使用指南

## 📦 第一步：安装 Android Studio

### 1. 下载
- **官方网址**: https://developer.android.com/studio
- **文件大小**: 约 1GB
- **系统要求**: Windows 10/11, 64位

### 2. 安装步骤

1. **运行安装程序**
   - 双击下载的 `.exe` 文件
   - 如果弹出UAC提示，点击"是"

2. **选择安装选项**
   - ✅ Android Studio
   - ✅ Android Virtual Device (模拟器，可选)
   - 点击 Next

3. **选择安装位置**
   - 默认路径通常是: `C:\Program Files\Android\Android Studio`
   - 确保有至少 10GB 空间
   - 点击 Next

4. **开始安装**
   - 点击 Install
   - 等待安装完成（约5-10分钟）

5. **首次启动配置**
   - 选择 Standard 安装类型
   - 选择UI主题（浅色/深色）
   - 等待下载 Android SDK 组件（约 3-5GB，需要10-30分钟）

---

## 📂 第二步：打开项目

### 方式1：从欢迎界面打开

1. 启动 Android Studio
2. 在欢迎界面点击 **"Open"**
3. 导航到项目目录:
   ```
   C:\Users\24307\Desktop\anan\QQMessageImageApp
   ```
4. 点击 **"OK"**

### 方式2：从文件菜单打开

1. 如果已经打开了其他项目
2. 点击 **File → Open...**
3. 选择项目目录
4. 点击 **"OK"**

---

## ⏳ 第三步：等待项目同步

### 首次打开项目会自动执行：

1. **下载 Gradle** (构建工具)
   - 显示进度在窗口底部
   - 约 100-200MB

2. **同步项目依赖**
   - 下载必要的库文件
   - 显示 "Syncing..." 状态

3. **索引代码**
   - 建立代码索引以支持智能提示
   - 显示 "Indexing..." 状态

**⏱️ 总耗时**: 首次约 5-15分钟（取决于网络速度）

**如果同步失败**，可能需要配置代理或使用国内镜像源。

---

## 📱 第四步：连接手机

### 准备手机

1. **开启开发者选项**
   - 设置 → 关于手机
   - 连续点击"版本号" 7次
   - 提示"您已处于开发者模式"

2. **开启USB调试**
   - 设置 → 系统 → 开发者选项
   - 找到"USB调试"，打开开关

3. **连接手机到电脑**
   - 使用USB数据线连接
   - 手机会弹出"允许USB调试"对话框
   - 勾选"始终允许使用这台计算机进行调试"
   - 点击"允许"

### 在Android Studio中验证连接

1. 查看窗口顶部工具栏
2. 在设备选择下拉框中
3. 应该能看到您的手机型号
4. 如果没有显示，点击下拉框 → **Troubleshoot Device Connections**

---

## 🚀 第五步：运行应用

### 方式1：使用运行按钮

1. 确保设备选择框中已选择您的手机
2. 点击绿色的"Run"按钮（▶️）
3. 或按快捷键 **Shift + F10**

### 方式2：使用菜单

1. 点击 **Run → Run 'app'**
2. 选择您的设备
3. 点击 OK

### 构建和安装过程

```
1. Gradle 构建项目
   └─ 编译 Kotlin/Java 代码
   └─ 处理资源文件
   └─ 打包 APK

2. 签名 APK
   └─ 使用 debug 签名

3. 安装到设备
   └─ adb install app-debug.apk

4. 启动应用
   └─ 应用自动在手机上打开
```

**⏱️ 首次构建**: 约 1-3分钟  
**后续构建**: 约 10-30秒

---

## 📊 第六步：查看日志

### Logcat 窗口

1. **打开 Logcat**
   - 窗口底部找到 "Logcat" 标签
   - 如果没有显示: View → Tool Windows → Logcat

2. **过滤日志**
   - 在搜索框输入: `QQAccessibilityService`
   - 或选择应用包名: `com.example.qqmessageimage`

3. **日志级别**
   - Verbose (V) - 所有信息
   - Debug (D) - 调试信息 ⭐ 推荐
   - Info (I) - 一般信息
   - Warn (W) - 警告
   - Error (E) - 错误

### 实时查看日志

1. 运行应用后，Logcat 会自动显示日志
2. 在手机上操作应用
3. 观察 Logcat 中的输出

**预期看到的日志示例:**
```
D/QQAccessibilityService: 收到事件: 2048, 包名: com.tencent.mobileqq
D/QQAccessibilityService: 触发处理逻辑
D/QQAccessibilityService: 找到的消息文本: 测试消息
```

---

## 🔧 常见问题解决

### Q1: Gradle 同步失败

**现象**: 显示 "Gradle sync failed"

**解决方案:**
1. 检查网络连接
2. File → Invalidate Caches → Invalidate and Restart
3. 重新同步: File → Sync Project with Gradle Files

### Q2: 设备未显示

**现象**: 设备下拉框中没有手机

**解决方案:**
1. 确认手机已开启USB调试
2. 重新插拔USB线
3. 运行命令检查:
   ```powershell
   adb devices
   ```
4. 如果显示 "unauthorized"，重新授权

### Q3: 构建很慢

**原因**: 首次构建需要下载依赖

**优化方法:**
1. 确保网络稳定
2. 可以配置国内镜像源加速
3. 后续构建会快很多

### Q4: 应用安装失败

**错误**: "Installation failed with message..."

**解决方案:**
1. 卸载手机上的旧版本
2. Build → Clean Project
3. Build → Rebuild Project
4. 重新 Run

---

## 💡 实用技巧

### 快捷键

| 功能 | Windows快捷键 |
|------|---------------|
| 运行应用 | Shift + F10 |
| 调试应用 | Shift + F9 |
| 查找文件 | Ctrl + Shift + N |
| 查找代码 | Ctrl + F |
| 全局搜索 | Ctrl + Shift + F |
| 重命名 | Shift + F6 |
| 格式化代码 | Ctrl + Alt + L |

### 代码编辑

1. **自动导入**
   - 写代码时如果类名显示红色
   - 将光标移到红色处
   - 按 Alt + Enter
   - 选择 "Import..."

2. **代码补全**
   - 输入几个字母
   - 按 Ctrl + Space
   - 选择提示项

3. **查看定义**
   - Ctrl + 点击 类名/方法名
   - 跳转到定义位置

### 调试技巧

1. **设置断点**
   - 在代码行号左侧点击
   - 出现红点表示断点

2. **调试运行**
   - 点击调试按钮（🐞）
   - 或按 Shift + F9

3. **查看变量值**
   - 程序暂停在断点时
   - 鼠标悬停在变量上
   - 或查看 Variables 窗口

---

## 🎯 针对本项目的具体操作

### 测试消息处理功能

1. **运行应用**
   ```
   点击 Run 按钮 (▶️)
   ```

2. **打开 Logcat**
   ```
   搜索框输入: QQAccessibilityService
   ```

3. **在手机上测试**
   ```
   - 打开QQ消息图片助手
   - 启用自动处理
   - 选择模板图片
   - 打开QQ聊天窗口
   - 输入消息并发送
   ```

4. **观察日志**
   ```
   查看 Logcat 中是否有:
   D/QQAccessibilityService: 收到事件...
   D/QQAccessibilityService: 找到的消息文本...
   ```

### 修改代码后测试

1. **编辑代码**
   - 在左侧文件树找到文件
   - 双击打开
   - 修改代码

2. **重新运行**
   - 直接点击 Run 按钮
   - Android Studio 会自动重新编译
   - 自动安装新版本到手机

3. **验证修改**
   - 观察 Logcat 日志
   - 在手机上测试功能

---

## 📚 学习资源

### 官方文档
- Android Studio 官方指南: https://developer.android.com/studio/intro
- Kotlin 语言文档: https://kotlinlang.org/docs/home.html

### 视频教程
- 搜索: "Android Studio 入门教程"
- 推荐观看官方入门视频

### 常用网站
- Stack Overflow: 遇到错误时搜索解决方案
- GitHub: 查看开源项目学习

---

## ✅ 检查清单

安装和使用Android Studio之前，确保：

- [ ] Windows 10/11 64位系统
- [ ] 至少 10GB 可用磁盘空间
- [ ] 稳定的网络连接
- [ ] 手机已开启USB调试
- [ ] USB数据线连接正常

开始使用时：

- [ ] Android Studio 已成功安装
- [ ] 项目已打开且同步完成
- [ ] 手机已连接并显示在设备列表
- [ ] 可以成功运行应用
- [ ] Logcat 可以看到日志输出

---

## 🆘 需要帮助？

如果遇到问题：

1. **查看错误信息**
   - 仔细阅读错误提示
   - 复制错误信息到搜索引擎

2. **重启大法**
   - File → Invalidate Caches → Invalidate and Restart
   - 重启 Android Studio
   - 重启电脑

3. **寻求帮助**
   - 截图错误信息
   - 描述具体操作步骤
   - 询问 AI 助手或技术社区

---

**祝您使用愉快！** 🎉
