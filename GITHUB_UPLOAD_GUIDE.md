# GitHub上传与自动构建指南

## 📦 项目已准备就绪

所有必要的文件已经配置完成，包括：
- ✅ `.gitignore` - Git忽略文件配置
- ✅ `.github/workflows/android-build.yml` - GitHub Actions自动构建配置
- ✅ `gradlew` 和 `gradlew.bat` - Gradle包装器脚本
- ✅ 完整的项目代码和文档

## 🚀 上传到GitHub的方法

### 方法一：使用Git命令行（推荐）

如果网络连接正常，在PowerShell中执行：

```powershell
cd c:\Users\24307\Desktop\anan\QQMessageImageApp

# 如果之前推送失败，先移除远程仓库
git remote remove origin

# 重新添加远程仓库
git remote add origin https://github.com/YaoZengyun/111.git

# 推送代码
git push -u origin main
```

**如果需要身份验证：**
- GitHub会打开浏览器进行OAuth认证
- 或者使用Personal Access Token（推荐）

**生成Personal Access Token：**
1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token" → "Generate new token (classic)"
3. 设置名称：如 "QQ消息图片助手"
4. 勾选权限：`repo` (完整仓库访问权限)
5. 点击 "Generate token"
6. **复制生成的token**（只显示一次！）

**使用Token推送：**
```powershell
# 设置远程仓库URL（使用token）
git remote set-url origin https://YOUR_TOKEN@github.com/YaoZengyun/111.git

# 推送代码
git push -u origin main
```

### 方法二：使用GitHub Desktop（简单）

1. **下载安装GitHub Desktop**
   - 访问：https://desktop.github.com/
   - 下载并安装

2. **登录GitHub账号**
   - 打开GitHub Desktop
   - File → Options → Accounts → Sign in

3. **添加本地仓库**
   - File → Add local repository
   - 选择：`c:\Users\24307\Desktop\anan\QQMessageImageApp`
   - 点击 Add repository

4. **推送到GitHub**
   - 点击 "Publish repository"
   - Repository name: 111
   - 取消勾选 "Keep this code private"（如果想公开）
   - 点击 "Publish repository"

### 方法三：通过GitHub网页上传（备用）

如果Git命令和GitHub Desktop都不行：

1. **访问你的仓库**
   - 打开 https://github.com/YaoZengyun/111

2. **上传文件**
   - 点击 "Add file" → "Upload files"
   - 拖拽整个项目文件夹（或选择文件）
   - 填写提交信息
   - 点击 "Commit changes"

**注意：** 这种方法可能无法保留文件权限（如gradlew的执行权限）

## 🔧 GitHub Actions自动构建

### 自动构建触发条件

上传成功后，GitHub Actions会自动触发构建：

- ✅ 推送到main分支时自动构建
- ✅ Pull Request时自动构建
- ✅ 手动触发构建

### 查看构建状态

1. 访问你的仓库：https://github.com/YaoZengyun/111
2. 点击 "Actions" 标签
3. 查看构建进度和日志

### 构建产物

构建成功后会生成：

**Artifacts（构建产物）：**
- `app-debug.apk` - 调试版本（可直接安装）
- `app-release-unsigned.apk` - 未签名的发布版本

**Release（发布版本）：**
- 自动创建版本标签：`v1.0.1`, `v1.0.2` ...
- 包含完整的发布说明
- 附带APK下载链接

### 下载APK

**方法1：从Actions下载**
```
仓库页面 → Actions → 选择最新的构建 → Artifacts → 下载APK
```

**方法2：从Releases下载**
```
仓库页面 → Releases → 选择最新版本 → Assets → 下载APK
```

## 📱 安装APK

1. 下载 `app-debug.apk` 到Android设备
2. 在设备上打开文件
3. 允许"安装未知应用"
4. 点击安装

## 🔍 构建失败排查

如果GitHub Actions构建失败：

### 1. 检查构建日志
```
Actions → 失败的构建 → 查看详细日志
```

### 2. 常见错误

**错误：Gradle下载失败**
```
解决：重新运行构建（网络问题）
Actions → Re-run jobs
```

**错误：Java版本不匹配**
```
解决：已配置JDK 17，一般不会出现
```

**错误：Gradle权限问题**
```
解决：确保gradlew有执行权限
本地执行：git update-index --chmod=+x gradlew
```

### 3. 本地测试构建

在推送前可以本地测试：

```powershell
# 测试Debug构建
.\gradlew.bat assembleDebug

# 测试Release构建
.\gradlew.bat assembleRelease

# 构建产物位置
app\build\outputs\apk\debug\app-debug.apk
app\build\outputs\apk\release\app-release-unsigned.apk
```

## 🎯 手动触发构建

如果想手动触发构建而不推送代码：

1. 访问 Actions 页面
2. 选择 "Android CI - Build APK" workflow
3. 点击 "Run workflow"
4. 选择分支：main
5. 点击绿色的 "Run workflow" 按钮

## 📝 更新代码后重新构建

当你修改代码后：

```powershell
cd c:\Users\24307\Desktop\anan\QQMessageImageApp

# 添加修改的文件
git add .

# 提交修改
git commit -m "更新说明"

# 推送到GitHub（会自动触发构建）
git push
```

## 🔐 配置签名（可选）

如果要发布正式版本，需要配置应用签名：

### 1. 生成密钥库

```powershell
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### 2. 配置GitHub Secrets

在仓库设置中添加密钥：
```
Settings → Secrets and variables → Actions → New repository secret
```

添加以下secrets：
- `KEYSTORE_FILE`: 密钥库文件（Base64编码）
- `KEYSTORE_PASSWORD`: 密钥库密码
- `KEY_ALIAS`: 密钥别名
- `KEY_PASSWORD`: 密钥密码

### 3. 修改GitHub Actions配置

更新 `.github/workflows/android-build.yml` 添加签名步骤

## 💡 提示

1. **首次构建时间较长**（5-10分钟）
   - 需要下载Gradle和依赖
   - 后续构建会更快（使用缓存）

2. **保持仓库整洁**
   - 不要提交 `build/` 目录
   - `.gitignore` 已配置忽略

3. **查看构建徽章**
   - 可以在README.md添加构建状态徽章
   ```markdown
   ![Android CI](https://github.com/YaoZengyun/111/workflows/Android%20CI%20-%20Build%20APK/badge.svg)
   ```

## 🆘 需要帮助？

- GitHub Actions文档：https://docs.github.com/actions
- Gradle文档：https://docs.gradle.org/
- Android开发文档：https://developer.android.com/

---

**祝构建顺利！🎉**
