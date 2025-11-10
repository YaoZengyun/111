# QQ消息图片助手

一个安卓应用，可以在使用QQ发送消息时自动将文字内容叠加到预设图片上。

> 📖 **新手？** 查看 [快速开始指南](QUICK_START.md) 5分钟快速上手！  
> 🎨 **高级功能？** 查看 [高级文字绘制说明](ADVANCED_TEXT_DRAWING.md) 了解强大特性！

## 功能特性

- ✅ 监听QQ消息发送
- ✅ 自动读取输入的文字内容
- ✅ 将文字叠加到自定义的模板图片上
- ✅ **自适应字号** - 自动计算最佳文字大小
- ✅ **智能换行** - 完美支持中英文混排
- ✅ **多色文字** - 中括号【】内容特殊颜色显示
- ✅ **灵活对齐** - 支持多种水平/垂直对齐方式
- ✅ 支持功能开关
- ✅ 支持自定义模板图片
- ✅ 可调节文字区域、大小、颜色、行间距
- ✅ 支持覆盖图层（水印、边框等）
- ✅ 友好的权限引导界面
- ✅ 实时权限状态检查
- ✅ 详细的使用说明

## 使用说明

### 1. 安装应用
使用Android Studio打开项目并编译安装到手机。

### 2. 授予权限

#### 📱 无障碍服务权限（必需）
这是应用的核心权限，用于监听QQ消息。

**自动引导方式：**
1. 打开应用后会自动弹出引导对话框
2. 阅读权限说明和设置步骤
3. 点击"去设置"按钮
4. 在无障碍设置中找到"QQ消息图片助手"
5. 打开开关启用服务

**手动设置方式：**
```
设置 → 辅助功能 → 无障碍 → 已下载的应用 → QQ消息图片助手 → 启用
```

**权限用途：**
- ✅ 监听QQ聊天界面
- ✅ 读取输入框文字内容
- ✅ 检测消息发送操作
- ❌ 不监听其他应用
- ❌ 不上传任何数据

**详细说明：** 查看 [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md)

#### 📁 存储权限（必需）
用于选择和保存模板图片。

应用会自动请求此权限，在弹出对话框时点击"允许"即可。

### 3. 配置模板
- 点击"选择模板图片"选择一张图片作为背景模板（建议命名为base.png）
- 调整文字区域 (X、Y坐标、宽度、高度)
- 调整文字样式（最大字号、行间距）
- 设置文字颜色（普通文字和中括号颜色）
- 点击"保存设置"

**高级功能**：
- 🎨 中括号特殊颜色：使用【】包裹的文字会显示为不同颜色
- 🖼️ 覆盖图层：可添加overlay.png作为水印或边框
- 📐 自适应字号：文字会自动调整到最佳大小适应区域

详见：[高级文字绘制说明](ADVANCED_TEXT_DRAWING.md)

### 4. 启用功能
- 打开应用中的"启用自动处理"开关
- 现在当你在QQ中输入并发送消息时，应用会自动处理

### 5. 使用
- 打开QQ
- 在聊天窗口输入文字
- 点击发送
- 应用会自动将文字叠加到模板图片上并保存

## 技术说明

### 核心技术
- **无障碍服务 (AccessibilityService)**: 用于监听QQ的界面变化和获取输入内容
- **高级文字绘制引擎**: 自适应字号、智能换行、多色文字（参考text_fit_draw.py）
- **二分查找算法**: 快速计算最佳字体大小
- **智能换行算法**: 完美处理中英文混排文本
- **Canvas绘图**: 用于将文字绘制到图片上
- **SharedPreferences**: 用于保存用户设置

### 项目结构
```
QQMessageImageApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/qqmessageimage/
│   │   │   ├── MainActivity.kt                  # 主界面
│   │   │   ├── QQAccessibilityService.kt        # 无障碍服务
│   │   │   ├── TextDrawHelper.kt                # 高级文字绘制引擎 ⭐
│   │   │   ├── AccessibilityHelper.kt           # 权限辅助类
│   │   │   └── PermissionManager.kt             # 权限管理
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml            # 主界面布局
│   │   │   │   └── dialog_accessibility_guide.xml  # 引导对话框
│   │   │   ├── values/
│   │   │   │   └── strings.xml                  # 字符串资源
│   │   │   └── xml/
│   │   │       ├── accessibility_service_config.xml
│   │   │       └── file_paths.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── README.md                                     # 项目说明
├── QUICK_START.md                                # 快速开始指南
├── ACCESSIBILITY_GUIDE.md                        # 权限详细指南
├── ADVANCED_TEXT_DRAWING.md                      # 高级文字绘制说明 ⭐
└── PROJECT_OVERVIEW.md                           # 项目总览
```

## 重要说明

### 权限说明
本应用需要以下权限：

1. **无障碍服务** ⭐ 核心权限
   - 用途：监听QQ界面、读取消息内容
   - 范围：仅限QQ应用（com.tencent.mobileqq）
   - 隐私：所有数据本地处理，不上传

2. **存储权限**
   - 用途：选择模板图片、保存处理后的图片
   - 范围：仅访问用户选择的图片
   - 隐私：图片存储在应用私有目录

详细说明请查看：[无障碍服务权限使用指南](ACCESSIBILITY_GUIDE.md)

### 限制
1. **自动发送**: 由于Android安全限制，应用无法完全自动发送图片。当前版本会将处理后的图片保存到应用目录，需要手动选择发送。

2. **QQ兼容性**: 不同版本的QQ界面结构可能不同，可能需要调整节点查找逻辑。

3. **无障碍权限**: 需要用户手动在系统设置中启用无障碍权限。

### 优化建议
1. 可以添加更多的文字样式选项（字体、粗体、斜体等）
2. 支持多个文字区域
3. 添加预览功能
4. 支持图片滤镜效果

## 开发环境
- Android Studio 2022.3 或更高版本
- Kotlin 1.8.0
- Android SDK 34
- 最低支持 Android 7.0 (API 24)

## 编译运行
1. 使用Android Studio打开项目
2. 同步Gradle依赖
3. 连接Android设备或启动模拟器
4. 点击运行按钮

## 常见问题

### Q: 无障碍服务无法启用？
A: 部分手机系统有限制，需要额外设置：
- 小米：允许自启动 + 无限制省电策略
- 华为：允许后台运行
- OPPO/vivo：关闭电池优化

详见 [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md) 的常见问题部分。

### Q: 会被QQ封号吗？
A: 本应用仅读取界面内容，不修改QQ，理论上是安全的。但建议：
- 仅个人使用
- 不要频繁使用
- 不要用于营销等商业用途

### Q: 如何撤销权限？
A: 
1. 应用内关闭功能开关
2. 系统设置中关闭无障碍服务
3. 直接卸载应用

## 文档
- 📘 [README.md](README.md) - 项目概述和完整文档
- 🚀 [QUICK_START.md](QUICK_START.md) - 5分钟快速上手指南
- 🎨 [ADVANCED_TEXT_DRAWING.md](ADVANCED_TEXT_DRAWING.md) - 高级文字绘制功能说明
- 🔐 [ACCESSIBILITY_GUIDE.md](ACCESSIBILITY_GUIDE.md) - 无障碍权限详细指南
- 📁 [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) - 项目技术总览

## 许可证
本项目仅供学习交流使用。

## 注意事项
- 请遵守相关法律法规
- 尊重他人隐私
- 合理使用无障碍服务权限
