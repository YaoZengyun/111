# 🚀 快速开始 - 5分钟上手Android Studio

## 第一步：下载并安装（约10分钟）

1. 访问：https://developer.android.com/studio
2. 下载 Android Studio
3. 双击安装，选择"Standard"安装
4. 等待下载SDK组件（需要时间较长，喝杯咖啡☕）

---

## 第二步：打开项目（1分钟）

1. 启动 Android Studio
2. 点击 **"Open"**
3. 选择文件夹：
   ```
   C:\Users\24307\Desktop\anan\QQMessageImageApp
   ```
4. 点击 **"OK"**
5. 等待项目同步完成（首次约5-10分钟）

---

## 第三步：连接手机（2分钟）

### 手机操作：
1. 设置 → 关于手机 → 连续点击"版本号"7次
2. 设置 → 系统 → 开发者选项 → 开启"USB调试"
3. 用USB线连接电脑
4. 手机上点击"允许USB调试"

### 验证连接：
- 看窗口顶部，设备下拉框显示您的手机型号 ✓

---

## 第四步：运行应用（30秒）

1. 点击绿色的 **Run 按钮** (▶️)
2. 等待编译完成
3. 应用自动安装并在手机上启动 🎉

---

## 第五步：查看日志（实时）

1. 点击底部的 **"Logcat"** 标签
2. 搜索框输入：`QQAccessibilityService`
3. 在手机上打开QQ发送消息
4. 观察日志输出：
   ```
   D/QQAccessibilityService: 收到事件...
   D/QQAccessibilityService: 找到的消息文本: xxx
   ```

---

## ⚠️ 如果遇到问题

### 设备未显示？
```powershell
# 在PowerShell中运行：
cd c:\Users\24307\Desktop\anan\QQMessageImageApp
$env:Path = "$env:Path;$(Resolve-Path '.\platform-tools')"
adb devices
```
应该看到设备列表。如果显示"unauthorized"，重新授权手机。

### Gradle同步失败？
1. 点击 **File → Invalidate Caches → Invalidate and Restart**
2. 等待重启后重新同步

### 构建很慢？
- 首次构建需要下载依赖，耐心等待
- 后续构建会快很多（10-30秒）

---

## 💡 最常用的3个操作

1. **运行应用**: 点击 ▶️ 或按 `Shift + F10`
2. **查看日志**: 底部 Logcat 标签
3. **修改代码**: 左侧文件树 → 双击文件 → 编辑 → 点击 ▶️

---

## 📝 测试本项目功能的完整流程

```
1. Android Studio 中点击 Run (▶️)
2. 等待应用安装到手机
3. 手机上打开应用
4. 启用"自动处理"开关
5. 选择模板图片
6. 切换到 Android Studio 的 Logcat 窗口
7. 搜索：QQAccessibilityService
8. 回到手机，打开QQ
9. 输入消息并发送
10. 观察 Android Studio 的 Logcat 是否有日志输出
```

如果有日志输出，说明无障碍服务正在工作！🎊

---

**详细指南请查看：** `ANDROID_STUDIO_GUIDE.md`
