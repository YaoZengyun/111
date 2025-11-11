# 检查QQ消息图片处理应用状态

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  QQ消息图片处理 - 状态检查" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. 检查应用是否安装
Write-Host "1. 应用安装状态" -ForegroundColor Yellow
$package = adb shell pm list packages | Select-String "qqmessageimage"
if ($package) {
    Write-Host "   ✓ 应用已安装" -ForegroundColor Green
} else {
    Write-Host "   ✗ 应用未安装" -ForegroundColor Red
    Write-Host "`n请先安装APK！" -ForegroundColor Yellow
    exit 1
}

# 2. 检查功能开关
Write-Host "`n2. 功能开关状态" -ForegroundColor Yellow
$settings = adb shell "run-as com.example.qqmessageimage cat /data/data/com.example.qqmessageimage/shared_prefs/settings.xml 2>/dev/null"
if ($settings) {
    if ($settings -match 'name="enabled".*value="true"') {
        Write-Host "   ✓ 功能已启用" -ForegroundColor Green
    } else {
        Write-Host "   ✗ 功能未启用" -ForegroundColor Red
        Write-Host "   → 请在应用中打开功能开关" -ForegroundColor Yellow
    }
} else {
    Write-Host "   ✗ 应用未配置（可能从未打开过）" -ForegroundColor Red
    Write-Host "   → 请先打开应用并进行配置" -ForegroundColor Yellow
}

# 3. 检查模板图片
Write-Host "`n3. 模板图片状态" -ForegroundColor Yellow
$template = adb shell "run-as com.example.qqmessageimage ls -lh /data/data/com.example.qqmessageimage/files/template.png 2>/dev/null"
if ($template -and $template -notmatch "No such file") {
    Write-Host "   ✓ 模板图片已设置" -ForegroundColor Green
    Write-Host "   $template" -ForegroundColor Gray
} else {
    Write-Host "   ✗ 模板图片未设置" -ForegroundColor Red
    Write-Host "   → 请在应用中选择一张图片作为模板" -ForegroundColor Yellow
}

# 4. 检查辅助功能服务
Write-Host "`n4. 辅助功能服务状态" -ForegroundColor Yellow
$accessibility = adb shell "settings get secure enabled_accessibility_services"
if ($accessibility -match "qqmessageimage") {
    Write-Host "   ✓ 辅助功能服务已启用" -ForegroundColor Green
} else {
    Write-Host "   ✗ 辅助功能服务未启用" -ForegroundColor Red
    Write-Host "   → 请在 设置->辅助功能 中启用" -ForegroundColor Yellow
}

# 总结
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  配置指南" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "如果有任何项显示 ✗，请按以下步骤操作：`n" -ForegroundColor Yellow

Write-Host "步骤1: 打开手机上的应用" -ForegroundColor White
Write-Host "步骤2: 打开功能开关" -ForegroundColor White
Write-Host "步骤3: 点击'选择模板图片'，从相册选择一张图片" -ForegroundColor White
Write-Host "步骤4: 点击'打开辅助功能设置'，启用服务" -ForegroundColor White
Write-Host "步骤5: 返回应用，确认所有状态都显示✓`n" -ForegroundColor White

Write-Host "配置完成后，可以运行: " -ForegroundColor Cyan
Write-Host "  .\test_app.ps1" -ForegroundColor Yellow
Write-Host "来测试功能是否正常工作。`n" -ForegroundColor White
