# 永久添加ADB到系统PATH
# 需要管理员权限运行

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  添加ADB到系统环境变量" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "⚠ 需要管理员权限才能修改系统环境变量" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "请按以下步骤操作：" -ForegroundColor Cyan
    Write-Host "1. 右键点击PowerShell图标" -ForegroundColor White
    Write-Host "2. 选择 '以管理员身份运行'" -ForegroundColor White
    Write-Host "3. 重新运行此脚本" -ForegroundColor White
    Write-Host ""
    Write-Host "或者手动添加：" -ForegroundColor Yellow
    Write-Host "1. 右键 '此电脑' → 属性 → 高级系统设置 → 环境变量" -ForegroundColor White
    Write-Host "2. 在 '系统变量' 中找到 Path → 编辑 → 新建" -ForegroundColor White
    Write-Host "3. 添加：$(Resolve-Path '.\platform-tools')" -ForegroundColor White
    Write-Host "4. 确定保存，重启PowerShell" -ForegroundColor White
    exit 1
}

# 查找ADB路径
$adbPath = ".\platform-tools"
if (-not (Test-Path $adbPath)) {
    Write-Host "❌ 未找到 platform-tools 文件夹" -ForegroundColor Red
    Write-Host "请先运行：.\install_adb.ps1" -ForegroundColor Yellow
    exit 1
}

$adbFullPath = Resolve-Path $adbPath
Write-Host "ADB路径：$adbFullPath" -ForegroundColor Gray
Write-Host ""

# 添加到系统PATH
Write-Host "正在添加到系统环境变量..." -ForegroundColor Yellow

$oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
if ($oldPath -like "*$adbFullPath*") {
    Write-Host "✓ PATH中已存在此路径，无需重复添加" -ForegroundColor Green
} else {
    $newPath = "$oldPath;$adbFullPath"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
    Write-Host "✓ 已添加到系统PATH" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  配置完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "请重启PowerShell后使用ADB工具" -ForegroundColor Yellow
Write-Host ""
