# Auto retry push script
param(
    [int]$MaxRetries = 10,
    [int]$DelaySeconds = 3
)

Write-Host "Starting push to GitHub..." -ForegroundColor Cyan

for ($i = 1; $i -le $MaxRetries; $i++) {
    Write-Host "`nAttempt $i/$MaxRetries..." -ForegroundColor Yellow
    
    git push origin main 2>&1 | Out-Null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`nPush successful!" -ForegroundColor Green
        Write-Host "View builds: https://github.com/YaoZengyun/111/actions" -ForegroundColor Cyan
        exit 0
    }
    
    if ($i -lt $MaxRetries) {
        Write-Host "Failed, retrying in $DelaySeconds seconds..." -ForegroundColor Red
        Start-Sleep -Seconds $DelaySeconds
    }
}

Write-Host "`nPush failed after $MaxRetries attempts" -ForegroundColor Red
exit 1
