# Android Studio 自动安装脚本
# 用于 QQMessageImageApp 项目

$ErrorActionPreference = "Stop"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Android Studio 安装程序" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 检查是否已安装
$androidStudioPath = "C:\Program Files\Android\Android Studio\bin\studio64.exe"
if (Test-Path $androidStudioPath) {
    Write-Host "检测到 Android Studio 已安装！" -ForegroundColor Green
    Write-Host "位置: $androidStudioPath`n" -ForegroundColor Gray
    
    $choice = Read-Host "是否直接打开 Android Studio? (y/n)"
    if ($choice -eq 'y') {
        Write-Host "`n启动 Android Studio..." -ForegroundColor Yellow
        Start-Process $androidStudioPath
        
        Write-Host "`n打开项目步骤：" -ForegroundColor Cyan
        Write-Host "1. 等待 Android Studio 启动" -ForegroundColor White
        Write-Host "2. 点击 File -> Open" -ForegroundColor White
        Write-Host "3. 选择文件夹：C:\Users\24307\Desktop\anan\QQMessageImageApp" -ForegroundColor Yellow
        Write-Host "4. 等待 Gradle 同步完成（约 5-10 分钟）" -ForegroundColor White
        Write-Host "5. 点击绿色 Run 按钮运行到手机" -ForegroundColor White
        exit 0
    }
    exit 0
}

Write-Host "未检测到 Android Studio，开始安装流程...`n" -ForegroundColor Yellow

# 下载链接
$downloadUrl = "https://redirector.gvt1.com/edgedl/android/studio/install/2023.1.1.28/android-studio-2023.1.1.28-windows.exe"
$installerPath = "$env:TEMP\android-studio-installer.exe"

Write-Host "步骤 1/3: 下载 Android Studio" -ForegroundColor Cyan
Write-Host "------------------------------------" -ForegroundColor Gray
Write-Host "下载地址: $downloadUrl" -ForegroundColor Gray
Write-Host "保存位置: $installerPath" -ForegroundColor Gray
Write-Host "文件大小: 约 1 GB" -ForegroundColor Yellow
Write-Host "预计时间: 根据网速而定（通常 5-20 分钟）`n" -ForegroundColor Gray

try {
    Write-Host "开始下载..." -ForegroundColor Yellow
    
    # 使用 WebClient 下载并显示进度
    $webClient = New-Object System.Net.WebClient
    
    # 注册进度事件
    Register-ObjectEvent -InputObject $webClient -EventName DownloadProgressChanged -SourceIdentifier WebClient.DownloadProgressChanged -Action {
        $percent = $EventArgs.ProgressPercentage
        $received = [math]::Round($EventArgs.BytesReceived / 1MB, 2)
        $total = [math]::Round($EventArgs.TotalBytesToReceive / 1MB, 2)
        Write-Progress -Activity "下载 Android Studio" -Status "$received MB / $total MB" -PercentComplete $percent
    } | Out-Null
    
    # 开始异步下载
    $webClient.DownloadFileAsync($downloadUrl, $installerPath)
    
    # 等待下载完成
    while ($webClient.IsBusy) {
        Start-Sleep -Milliseconds 500
    }
    
    # 清理事件
    Unregister-Event -SourceIdentifier WebClient.DownloadProgressChanged
    
    Write-Host "`n下载完成！" -ForegroundColor Green
}
catch {
    Write-Host "`n下载失败: $_" -ForegroundColor Red
    Write-Host "`n请手动下载：" -ForegroundColor Yellow
    Write-Host "访问: https://developer.android.com/studio" -ForegroundColor Cyan
    Write-Host "下载后双击运行安装程序" -ForegroundColor White
    exit 1
}

Write-Host "`n步骤 2/3: 运行安装程序" -ForegroundColor Cyan
Write-Host "------------------------------------" -ForegroundColor Gray
Write-Host "即将启动安装向导..." -ForegroundColor Yellow
Write-Host "`n安装建议：" -ForegroundColor White
Write-Host "  • 安装类型选择: Standard (标准)" -ForegroundColor Gray
Write-Host "  • 安装路径: 使用默认路径" -ForegroundColor Gray
Write-Host "  • 全部组件: 保持默认选中" -ForegroundColor Gray
Write-Host "`n按任意键开始安装..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

try {
    # 运行安装程序
    Start-Process -FilePath $installerPath -Wait
    
    Write-Host "`n安装程序已关闭" -ForegroundColor Green
}
catch {
    Write-Host "`n启动安装程序失败: $_" -ForegroundColor Red
    Write-Host "请手动双击运行: $installerPath" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n步骤 3/3: 首次配置" -ForegroundColor Cyan
Write-Host "------------------------------------" -ForegroundColor Gray
Write-Host "首次启动 Android Studio 会进行初始配置：`n" -ForegroundColor White
Write-Host "1. 选择配置选项" -ForegroundColor Yellow
Write-Host "   -> 如果没有旧配置，选择 'Do not import settings'" -ForegroundColor Gray
Write-Host "`n2. 选择UI主题" -ForegroundColor Yellow
Write-Host "   -> 根据个人喜好选择 Light 或 Dark" -ForegroundColor Gray
Write-Host "`n3. 安装SDK组件" -ForegroundColor Yellow
Write-Host "   -> 选择 Standard 标准安装" -ForegroundColor Gray
Write-Host "   -> 会自动下载 Android SDK、模拟器等（约 10-15 分钟）" -ForegroundColor Gray
Write-Host "`n4. 接受许可协议" -ForegroundColor Yellow
Write-Host "   -> 同意 Android SDK 许可" -ForegroundColor Gray
Write-Host "`n5. 等待下载完成" -ForegroundColor Yellow
Write-Host "   -> SDK 下载约 3-5 GB，耐心等待" -ForegroundColor Gray

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  配置完成后的操作步骤" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "1. 打开项目" -ForegroundColor Yellow
Write-Host "   File -> Open -> 选择文件夹：" -ForegroundColor White
Write-Host "   C:\Users\24307\Desktop\anan\QQMessageImageApp`n" -ForegroundColor Cyan

Write-Host "2. 等待 Gradle 同步" -ForegroundColor Yellow
Write-Host "   首次打开会下载项目依赖，约 5-10 分钟" -ForegroundColor Gray
Write-Host "   底部会显示 'Gradle Build Running...' 进度条`n" -ForegroundColor Gray

Write-Host "3. 连接测试手机" -ForegroundColor Yellow
Write-Host "   • 手机开启开发者选项和USB调试" -ForegroundColor White
Write-Host "   • USB连接到电脑" -ForegroundColor White
Write-Host "   • 手机上点击'允许USB调试'" -ForegroundColor White
Write-Host "   • 顶部工具栏会显示你的设备名称`n" -ForegroundColor White

Write-Host "4. 运行应用" -ForegroundColor Yellow
Write-Host "   点击顶部工具栏的绿色播放按钮 ▶`n" -ForegroundColor White

Write-Host "5. 查看运行日志" -ForegroundColor Yellow
Write-Host "   底部 Logcat 标签页 -> 搜索框输入: QQAccessibilityService" -ForegroundColor White
Write-Host "   可以看到应用的运行日志和调试信息`n" -ForegroundColor White

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试功能" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "运行应用后：" -ForegroundColor Yellow
Write-Host "1. 在应用中启用辅助功能服务" -ForegroundColor White
Write-Host "2. 选择一张图片作为底图" -ForegroundColor White
Write-Host "3. 打开 QQ，发送一条消息" -ForegroundColor White
Write-Host "4. 查看 Logcat 是否有日志输出：" -ForegroundColor White
Write-Host "   '收到事件: ...'" -ForegroundColor Gray
Write-Host "   '开始处理QQ消息'" -ForegroundColor Gray
Write-Host "   '找到的消息文本: ...'" -ForegroundColor Gray
Write-Host "`n如果看到这些日志，说明功能正常工作！`n" -ForegroundColor Green

# 询问是否立即打开 Android Studio
$openNow = Read-Host "是否现在打开 Android Studio 开始配置? (y/n)"
if ($openNow -eq 'y') {
    if (Test-Path $androidStudioPath) {
        Write-Host "`n启动 Android Studio..." -ForegroundColor Yellow
        Start-Process $androidStudioPath
        Write-Host "请按照上述步骤完成首次配置，然后打开项目。" -ForegroundColor Cyan
    }
    else {
        Write-Host "`n未找到 Android Studio，请手动启动" -ForegroundColor Red
        Write-Host "通常位置: C:\Program Files\Android\Android Studio\bin\studio64.exe" -ForegroundColor Yellow
    }
}

Write-Host "`n安装脚本执行完成！" -ForegroundColor Green
Write-Host "如有问题，请查看 ANDROID_STUDIO_GUIDE.md 详细文档`n" -ForegroundColor Cyan
