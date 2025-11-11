# ADB工具自动安装脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ADB工具自动安装脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查是否已安装
`$adbExists = Get-Command adb -ErrorAction SilentlyContinue
if (`$adbExists) {
    Write-Host "ADB已安装：`$(`$adbExists.Source)" -ForegroundColor Green
    adb version
    Write-Host ""
    Write-Host "无需重复安装！可以直接运行诊断脚本" -ForegroundColor Yellow
    exit 0
}

Write-Host "未检测到ADB工具，开始自动安装..." -ForegroundColor Yellow
Write-Host ""

# 设置下载路径
`$downloadUrl = "https://dl.google.com/android/repository/platform-tools-latest-windows.zip"
`$zipFile = "platform-tools.zip"
`$extractPath = ".\platform-tools"

# 下载
Write-Host "[1/3] 下载 Android Platform Tools..." -ForegroundColor Yellow
Write-Host "下载地址：`$downloadUrl" -ForegroundColor Gray

try {
    `$ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri `$downloadUrl -OutFile `$zipFile -UseBasicParsing
    Write-Host "下载完成" -ForegroundColor Green
} catch {
    Write-Host "下载失败：`$(`$_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动下载：" -ForegroundColor Yellow
    Write-Host "https://developer.android.com/studio/releases/platform-tools"
    exit 1
}
Write-Host ""

# 解压
Write-Host "[2/3] 解压文件..." -ForegroundColor Yellow
try {
    Expand-Archive -Path `$zipFile -DestinationPath "." -Force
    Write-Host "解压完成" -ForegroundColor Green
} catch {
    Write-Host "解压失败：`$(`$_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 配置环境变量
Write-Host "[3/3] 配置环境变量..." -ForegroundColor Yellow
`$adbPath = Resolve-Path `$extractPath
Write-Host "ADB路径：`$adbPath" -ForegroundColor Gray

# 临时添加到当前会话
`$env:Path = "`$env:Path;`$adbPath"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  安装完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 测试
Write-Host "测试ADB工具：" -ForegroundColor Yellow
& "`$adbPath\adb.exe" version
Write-Host ""

Write-Host "注意：ADB已添加到当前PowerShell会话" -ForegroundColor Yellow
Write-Host "现在可以运行诊断脚本了！" -ForegroundColor Green
Write-Host ""

# 清理
Remove-Item `$zipFile -Force -ErrorAction SilentlyContinue
