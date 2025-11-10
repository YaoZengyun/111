# QQ消息图片助手 - GitHub推送脚本

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "QQ消息图片助手 - GitHub推送脚本" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# 检查Git状态
Write-Host "正在检查Git状态..." -ForegroundColor Yellow
git status
Write-Host ""

# 添加所有更改
Write-Host "正在添加所有更改..." -ForegroundColor Yellow
git add .
Write-Host ""

# 提交更改
$commitMsg = Read-Host "请输入提交信息（直接回车使用默认）"
if ([string]::IsNullOrWhiteSpace($commitMsg)) {
    $commitMsg = "更新代码"
}

Write-Host "正在提交更改..." -ForegroundColor Yellow
git commit -m $commitMsg
Write-Host ""

# 推送到GitHub
Write-Host "正在推送到GitHub..." -ForegroundColor Yellow
Write-Host "如果推送失败，请查看 GITHUB_UPLOAD_GUIDE.md 获取帮助" -ForegroundColor Gray
Write-Host ""

$pushResult = git push -u origin main 2>&1
$exitCode = $LASTEXITCODE

if ($exitCode -ne 0) {
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Red
    Write-Host "⚠️  推送失败！" -ForegroundColor Red
    Write-Host "====================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "可能的原因：" -ForegroundColor Yellow
    Write-Host "1. 网络连接问题（需要代理或VPN）"
    Write-Host "2. 身份验证失败（需要Personal Access Token）"
    Write-Host "3. 远程仓库不存在或权限不足"
    Write-Host ""
    Write-Host "解决方案：" -ForegroundColor Green
    Write-Host "1. 配置代理：git config --global http.proxy http://127.0.0.1:端口"
    Write-Host "2. 使用Token：git remote set-url origin https://TOKEN@github.com/YaoZengyun/111.git"
    Write-Host "3. 使用GitHub Desktop（推荐新手）"
    Write-Host "4. 通过网页上传（备用方案）"
    Write-Host ""
    Write-Host "详细说明请查看：GITHUB_UPLOAD_GUIDE.md" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "错误信息：" -ForegroundColor Red
    Write-Host $pushResult
} else {
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Green
    Write-Host "✅ 推送成功！" -ForegroundColor Green
    Write-Host "====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "下一步：" -ForegroundColor Yellow
    Write-Host "1. 查看构建状态：https://github.com/YaoZengyun/111/actions" -ForegroundColor Cyan
    Write-Host "2. 下载APK文件：https://github.com/YaoZengyun/111/releases" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "构建大约需要 5-10 分钟" -ForegroundColor Gray
    Write-Host ""
}

Write-Host "按任意键退出..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
