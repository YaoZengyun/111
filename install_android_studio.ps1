# Android Studio 自动安装脚本
# 使用方法：.\install_android_studio.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Android Studio 安装助手" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查是否已安装
$androidStudioPath = "C:\Program Files\Android\Android Studio\bin\studio64.exe"
if (Test-Path $androidStudioPath) {
    Write-Host "✓ Android Studio 已安装！" -ForegroundColor Green
    Write-Host "位置: $androidStudioPath" -ForegroundColor Gray
    Write-Host ""
    $open = Read-Host "是否现在打开项目？(y/n)"
    if ($open -eq 'y') {
        Start-Process $androidStudioPath -ArgumentList $(Get-Location)
    }
    exit 0
}

Write-Host "Android Studio 未安装，开始安装流程...`n" -ForegroundColor Yellow

# 下载地址
$downloadUrl = "https://redirector.gvt1.com/edgedl/android/studio/install/2023.1.1.28/android-studio-2023.1.1.28-windows.exe"
$installerPath = "$env:TEMP\android-studio-installer.exe"

Write-Host "[1/4] 下载 Android Studio..." -ForegroundColor Yellow
Write-Host "下载地址: $downloadUrl" -ForegroundColor Gray
Write-Host "文件大小: 约 1GB，需要等待几分钟..." -ForegroundColor Gray
Write-Host ""

try {
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $downloadUrl -OutFile $installerPath -UseBasicParsing
    Write-Host "✓ 下载完成" -ForegroundColor Green
} catch {
    Write-Host "❌ 下载失败：$($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动下载：" -ForegroundColor Yellow
    Write-Host "1. 访问：https://developer.android.com/studio" -ForegroundColor Cyan
    Write-Host "2. 下载 Windows 版本" -ForegroundColor White
    Write-Host "3. 运行安装程序" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "[2/4] 启动安装程序..." -ForegroundColor Yellow
Write-Host "即将打开安装向导，请按照提示操作：" -ForegroundColor Cyan
Write-Host ""
Write-Host "安装建议：" -ForegroundColor Yellow
Write-Host "  ✓ 选择 'Standard' 标准安装" -ForegroundColor White
Write-Host "  ✓ 默认安装位置即可" -ForegroundColor White
Write-Host "  ✓ 全部组件都勾选（包括Android Virtual Device）" -ForegroundColor White
Write-Host ""

$install = Read-Host "准备好后按回车键开始安装"

# 运行安装程序
Start-Process -FilePath $installerPath -Wait

Write-Host ""
Write-Host "[3/4] 检查安装结果..." -ForegroundColor Yellow

if (Test-Path $androidStudioPath) {
    Write-Host "✓ 安装成功！" -ForegroundColor Green
} else {
    Write-Host "⚠ 未检测到安装，可能安装到其他位置" -ForegroundColor Yellow
    Write-Host "请手动启动 Android Studio" -ForegroundColor White
}

Write-Host ""
Write-Host "[4/4] 首次配置..." -ForegroundColor Yellow
Write-Host "首次启动 Android Studio 时，会自动下载：" -ForegroundColor Cyan
Write-Host "  • Android SDK" -ForegroundColor White
Write-Host "  • Android SDK Platform-Tools" -ForegroundColor White
Write-Host "  • Android Emulator" -ForegroundColor White
Write-Host ""
Write-Host "这个过程需要约 10-15 分钟，请耐心等待" -ForegroundColor Yellow

# 清理安装文件
Remove-Item $installerPath -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  下一步操作" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. 启动 Android Studio" -ForegroundColor Yellow
Write-Host "   双击桌面图标或开始菜单中的 Android Studio" -ForegroundColor White
Write-Host ""
Write-Host "2. 完成首次配置向导" -ForegroundColor Yellow
Write-Host "   选择 Standard 安装，等待SDK下载完成" -ForegroundColor White
Write-Host ""
Write-Host "3. 打开项目" -ForegroundColor Yellow
Write-Host "   Open → 选择文件夹：" -ForegroundColor White
Write-Host "   C:\Users\24307\Desktop\anan\QQMessageImageApp" -ForegroundColor Cyan
Write-Host ""
Write-Host "4. 等待项目加载和Gradle同步" -ForegroundColor Yellow
Write-Host "   首次打开需要下载依赖，约 5-10 分钟" -ForegroundColor White
Write-Host ""
Write-Host "5. 连接手机并运行" -ForegroundColor Yellow
Write-Host "   • 手机开启USB调试" -ForegroundColor White
Write-Host "   • USB连接电脑" -ForegroundColor White
Write-Host "   • 点击绿色 Run 按钮 ▶" -ForegroundColor White
Write-Host ""

$openNow = Read-Host "是否现在启动 Android Studio？(y/n)"
if ($openNow -eq 'y') {
    if (Test-Path $androidStudioPath) {
        Start-Process $androidStudioPath
        Write-Host ""
        Write-Host "✓ Android Studio 已启动" -ForegroundColor Green
        Write-Host "完成首次配置后，使用 File -> Open 打开项目" -ForegroundColor Yellow
    }
}
