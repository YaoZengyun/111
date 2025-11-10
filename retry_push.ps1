# 自动重试推送脚本
param(
    [int]$MaxRetries = 10,
    [int]$DelaySeconds = 3
)

Write-Host "开始推送到GitHub..." -ForegroundColor Cyan

for ($i = 1; $i -le $MaxRetries; $i++) {
    Write-Host "`n尝试 $i/$MaxRetries..." -ForegroundColor Yellow
    
    $result = git push origin main 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ 推送成功！" -ForegroundColor Green
        Write-Host "查看构建: https://github.com/YaoZengyun/111/actions" -ForegroundColor Cyan
        exit 0
    }
    
    if ($i -lt $MaxRetries) {
        Write-Host "推送失败，$DelaySeconds 秒后重试..." -ForegroundColor Red
        Start-Sleep -Seconds $DelaySeconds
    }
}

Write-Host "`n❌ 推送失败！已尝试 $MaxRetries 次" -ForegroundColor Red
Write-Host "`n建议：" -ForegroundColor Yellow
Write-Host "1. 检查网络连接或使用VPN"
Write-Host "2. 使用GitHub Desktop推送"
Write-Host "3. 稍后再试"
exit 1
