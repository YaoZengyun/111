@echo off
chcp 65001 >nul
echo ====================================
echo QQ消息图片助手 - GitHub推送脚本
echo ====================================
echo.

echo 正在检查Git状态...
git status
echo.

echo 正在添加所有更改...
git add .
echo.

echo 正在提交更改...
set /p commit_msg="请输入提交信息（直接回车使用默认）: "
if "%commit_msg%"=="" set commit_msg=更新代码

git commit -m "%commit_msg%"
echo.

echo 正在推送到GitHub...
echo 如果推送失败，请查看 GITHUB_UPLOAD_GUIDE.md 获取帮助
echo.

git push -u origin main

if %errorlevel% neq 0 (
    echo.
    echo ====================================
    echo ⚠️ 推送失败！
    echo ====================================
    echo.
    echo 可能的原因：
    echo 1. 网络连接问题（需要代理或VPN）
    echo 2. 身份验证失败（需要Personal Access Token）
    echo 3. 远程仓库不存在
    echo.
    echo 解决方案：
    echo 1. 使用代理：git config --global http.proxy http://127.0.0.1:端口
    echo 2. 使用Token：git remote set-url origin https://TOKEN@github.com/YaoZengyun/111.git
    echo 3. 使用GitHub Desktop（推荐）
    echo.
    echo 详细说明请查看：GITHUB_UPLOAD_GUIDE.md
    echo.
) else (
    echo.
    echo ====================================
    echo ✅ 推送成功！
    echo ====================================
    echo.
    echo 查看构建状态：https://github.com/YaoZengyun/111/actions
    echo 下载APK：https://github.com/YaoZengyun/111/releases
    echo.
)

pause
