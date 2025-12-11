Param(
  [Parameter(Mandatory = $true)]
  [string]$Chapter
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$appDir = Join-Path $repoRoot "$Chapter/backend"
if (!(Test-Path $appDir)) {
  Write-Error "未找到目录: $appDir"
}

$pidDir = Join-Path $repoRoot "scripts/pids"
New-Item -ItemType Directory -Force -Path $pidDir | Out-Null
$pidFile = Join-Path $pidDir "$Chapter-backend.pid"
$logFile = Join-Path $pidDir "$Chapter-backend.log"

if (Test-Path $pidFile) {
  $oldPid = Get-Content $pidFile
  if (Get-Process -Id $oldPid -ErrorAction SilentlyContinue) {
    Write-Error "检测到已有后台进程 (PID=$oldPid)，请先执行 stop-backend.ps1 $Chapter"
  } else {
    Remove-Item $pidFile -ErrorAction SilentlyContinue
  }
}

Write-Host "启动后端: $Chapter (日志: $logFile)"
$proc = Start-Process -FilePath "./mvnw.cmd" -ArgumentList "spring-boot:run" `
  -WorkingDirectory $appDir `
  -RedirectStandardOutput $logFile -RedirectStandardError $logFile `
  -PassThru -WindowStyle Hidden

$proc.Id | Out-File -Encoding ascii -FilePath $pidFile -Force
Write-Host "已在后台启动，PID=$($proc.Id)"

