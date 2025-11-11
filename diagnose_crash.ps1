# Android应用崩溃诊断脚本
# 使用方法：.\diagnose_crash.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Android应用崩溃诊断工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查ADB是否可用
Write-Host "[1/5] 检查ADB工具..." -ForegroundColor Yellow
$adbPath = Get-Command adb -ErrorAction SilentlyContinue

if (-not $adbPath) {
    Write-Host "❌ 未找到ADB工具" -ForegroundColor Red
    Write-Host ""
    Write-Host "解决方案：" -ForegroundColor Yellow
    Write-Host "1. 安装Android Studio（推荐）"
    Write-Host "   下载：https://developer.android.com/studio"
    Write-Host ""
    Write-Host "2. 或单独安装Platform Tools："
    Write-Host "   下载：https://developer.android.com/studio/releases/platform-tools"
    Write-Host "   解压后将路径添加到PATH环境变量"
    Write-Host ""
    Write-Host "3. 如果已安装，添加以下路径到PATH："
    Write-Host "   C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools"
    exit 1
}

Write-Host "✓ ADB工具已找到: $($adbPath.Source)" -ForegroundColor Green
Write-Host ""

# 检查设备连接
Write-Host "[2/5] 检查设备连接..." -ForegroundColor Yellow
$devices = adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "device$" }

if (-not $devices) {
    Write-Host "❌ 未检测到设备" -ForegroundColor Red
    Write-Host ""
    Write-Host "请确保：" -ForegroundColor Yellow
    Write-Host "1. 手机通过USB连接到电脑"
    Write-Host "2. 手机已开启开发者选项和USB调试"
    Write-Host "   设置 → 关于手机 → 连续点击版本号7次"
    Write-Host "   设置 → 系统 → 开发者选项 → USB调试"
    Write-Host "3. 手机上允许USB调试授权"
    Write-Host ""
    Write-Host "当前连接状态："
    adb devices
    Write-Host ""
    
    $retry = Read-Host "是否重试？(y/n)"
    if ($retry -eq 'y') {
        Write-Host "尝试重启ADB服务器..."
        adb kill-server
        Start-Sleep -Seconds 2
        adb start-server
        Start-Sleep -Seconds 2
        & $MyInvocation.MyCommand.Path
        exit
    }
    exit 1
}

Write-Host "✓ 已连接设备：" -ForegroundColor Green
adb devices | Select-Object -Skip 1 | ForEach-Object { Write-Host "  $_" -ForegroundColor Green }
Write-Host ""

# 检查应用是否已安装
Write-Host "[3/5] 检查应用安装状态..." -ForegroundColor Yellow
$appInstalled = adb shell pm list packages | Select-String "com.example.qqmessageimage"

if ($appInstalled) {
    Write-Host "✓ 应用已安装" -ForegroundColor Green
    
    # 获取应用信息
    Write-Host ""
    Write-Host "应用信息：" -ForegroundColor Cyan
    $versionInfo = adb shell dumpsys package com.example.qqmessageimage | Select-String "versionName|versionCode" | Select-Object -First 2
    $versionInfo | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
} else {
    Write-Host "⚠ 应用未安装" -ForegroundColor Yellow
    
    # 查找APK文件
    $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        $install = Read-Host "是否安装应用？(y/n)"
        if ($install -eq 'y') {
            Write-Host "正在安装..." -ForegroundColor Yellow
            adb install -r $apkPath
            Write-Host "✓ 安装完成" -ForegroundColor Green
        }
    } else {
        Write-Host "❌ 未找到APK文件：$apkPath" -ForegroundColor Red
        Write-Host "请先从GitHub Releases下载APK" -ForegroundColor Yellow
        exit 1
    }
}
Write-Host ""

# 清空日志
Write-Host "[4/5] 清空旧日志..." -ForegroundColor Yellow
adb logcat -c
Write-Host "✓ 日志已清空" -ForegroundColor Green
Write-Host ""

# 开始监控
Write-Host "[5/5] 开始监控崩溃日志..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "请在手机上打开应用" -ForegroundColor Yellow
Write-Host "日志将实时显示在下方" -ForegroundColor Yellow
Write-Host "按 Ctrl+C 停止监控" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 创建日志文件
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$logFile = "crash_log_$timestamp.txt"

# 同时输出到控制台和文件
$process = Start-Process adb -ArgumentList "logcat" -NoNewWindow -PassThru -RedirectStandardOutput $logFile

try {
    # 实时读取并过滤日志
    Get-Content $logFile -Wait | Where-Object {
        $_ -match "AndroidRuntime|FATAL|com.example.qqmessageimage|Exception|Error"
    } | ForEach-Object {
        # 根据内容着色
        if ($_ -match "FATAL|Exception") {
            Write-Host $_ -ForegroundColor Red
        } elseif ($_ -match "Error") {
            Write-Host $_ -ForegroundColor Yellow
        } else {
            Write-Host $_ -ForegroundColor White
        }
    }
} finally {
    # 清理
    Stop-Process -Id $process.Id -ErrorAction SilentlyContinue
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "日志已保存到：$logFile" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    
    # 分析崩溃
    $crashContent = Get-Content $logFile -Raw
    if ($crashContent -match "FATAL EXCEPTION") {
        Write-Host ""
        Write-Host "检测到崩溃！关键信息：" -ForegroundColor Red
        Write-Host ""
        
        # 提取崩溃摘要
        $crashLines = $crashContent -split "`n" | Where-Object {
            $_ -match "FATAL|Caused by|at com.example"
        }
        $crashLines | Select-Object -First 20 | ForEach-Object {
            Write-Host $_ -ForegroundColor Red
        }
        
        Write-Host ""
        Write-Host "完整日志请查看：$logFile" -ForegroundColor Yellow
        Write-Host "请将此日志发送给开发者以获取帮助" -ForegroundColor Yellow
    }
}
