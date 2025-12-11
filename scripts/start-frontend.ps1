Param(
  [Parameter(Mandatory = $true)]
  [string]$Chapter
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$appDir = Join-Path $repoRoot "$Chapter/frontend"
if (!(Test-Path $appDir)) {
  Write-Error "未找到目录: $appDir"
}

$pidDir = Join-Path $repoRoot "scripts/pids"
New-Item -ItemType Directory -Force -Path $pidDir | Out-Null
$pidFile = Join-Path $pidDir "$Chapter-frontend.pid"
$logFile = Join-Path $pidDir "$Chapter-frontend.log"

if (Test-Path $pidFile) {
  $oldPid = Get-Content $pidFile
  if (Get-Process -Id $oldPid -ErrorAction SilentlyContinue) {
    Write-Error "检测到已有前端进程 (PID=$oldPid)，请先 stop-frontend.ps1 $Chapter"
  } else {
    Remove-Item $pidFile -ErrorAction SilentlyContinue
  }
}

Set-Location $appDir
if (!(Test-Path "node_modules")) {
  Write-Host "首次运行，安装前端依赖..."
  npm install
}

Write-Host "启动前端: $Chapter (日志: $logFile)"
$proc = Start-Process -FilePath "npm" -ArgumentList "run","dev","--","--host" `
  -WorkingDirectory $appDir `
  -RedirectStandardOutput $logFile -RedirectStandardError $logFile `
  -PassThru -WindowStyle Hidden

$proc.Id | Out-File -Encoding ascii -FilePath $pidFile -Force
Write-Host "已在后台启动，PID=$($proc.Id)"

