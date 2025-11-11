# 自动重试推送脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  自动重试推送到GitHub" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$maxRetries = 100
$retryCount = 0
$success = $false

Write-Host "开始尝试推送，最多重试 $maxRetries 次..." -ForegroundColor Yellow
Write-Host "按 Ctrl+C 可随时停止" -ForegroundColor Gray
Write-Host ""

while ($retryCount -lt $maxRetries -and -not $success) {
    $retryCount++
    Write-Host "[$retryCount/$maxRetries] 尝试推送..." -ForegroundColor Cyan
    
    # 尝试推送
    git push origin main 2>&1 | Out-Null
    
    if ($LASTEXITCODE -eq 0) {
        $success = $true
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "  ✓ 推送成功！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "查看仓库: https://github.com/YaoZengyun/111" -ForegroundColor Cyan
        Write-Host "查看构建: https://github.com/YaoZengyun/111/actions" -ForegroundColor Cyan
        break
    } else {
        Write-Host "  ✗ 失败，等待5秒后重试..." -ForegroundColor Red
        Start-Sleep -Seconds 5
    }
}

if (-not $success) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  已达到最大重试次数" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "建议:" -ForegroundColor Yellow
    Write-Host "1. 检查网络连接" -ForegroundColor White
    Write-Host "2. 配置代理: git config --global http.proxy http://127.0.0.1:端口" -ForegroundColor White
    Write-Host "3. 使用 GitHub Desktop: https://desktop.github.com/" -ForegroundColor White
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
