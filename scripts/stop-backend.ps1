Param(
  [Parameter(Mandatory = $true)]
  [string]$Chapter
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$pidFile = Join-Path $repoRoot "scripts/pids/$Chapter-backend.pid"

if (!(Test-Path $pidFile)) {
  Write-Error "未找到 PID 文件: $pidFile，可能未启动或已停止"
}

$pid = Get-Content $pidFile
$proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
if ($proc) {
  Write-Host "停止后端 $Chapter (PID=$pid)"
  Stop-Process -Id $pid
  Remove-Item $pidFile -ErrorAction SilentlyContinue
  Write-Host "已停止"
} else {
  Write-Warning "进程不存在，清理 PID 文件"
  Remove-Item $pidFile -ErrorAction SilentlyContinue
}

