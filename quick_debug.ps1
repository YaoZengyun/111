# 快速崩溃诊断
Write-Host "检查设备连接..." -ForegroundColor Yellow
adb devices

Write-Host "`n清空日志..." -ForegroundColor Yellow
adb logcat -c

Write-Host "`n请在手机上打开应用，等待崩溃发生...`n按任意键继续..." -ForegroundColor Cyan
`$null = `$Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')

Write-Host "`n获取崩溃日志..." -ForegroundColor Yellow
adb logcat -d *:E > crash.txt

Write-Host "`n 日志已保存到 crash.txt" -ForegroundColor Green
Write-Host "`n显示关键错误信息：" -ForegroundColor Cyan
Get-Content crash.txt | Select-String "AndroidRuntime|FATAL|Exception" -Context 2,5

Write-Host "`n完整日志请查看 crash.txt 文件" -ForegroundColor Yellow
