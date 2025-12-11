#!/usr/bin/env bash
set -euo pipefail

chapter="${1:-}"
if [[ -z "${chapter}" ]]; then
  echo "用法: $0 <chapter-xx-*> 或 final-project" >&2
  exit 1
fi

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
app_dir="${repo_root}/${chapter}/frontend"
if [[ ! -d "${app_dir}" ]]; then
  echo "未找到目录: ${app_dir}" >&2
  exit 1
fi

pid_dir="${repo_root}/scripts/pids"
log_dir="${pid_dir}"
mkdir -p "${pid_dir}" "${log_dir}"
pid_file="${pid_dir}/${chapter}-frontend.pid"
log_file="${log_dir}/${chapter}-frontend.log"

if [[ -f "${pid_file}" ]]; then
  old_pid="$(cat "${pid_file}")"
  if ps -p "${old_pid}" >/dev/null 2>&1; then
    echo "检测到已有前端进程 (PID=${old_pid})，请先 stop-frontend.sh ${chapter}" >&2
    exit 1
  else
    rm -f "${pid_file}"
  fi
fi

cd "${app_dir}"
if [[ ! -d node_modules ]]; then
  echo "首次运行，安装前端依赖..."
  npm install
fi

echo "启动前端: ${chapter} (日志: ${log_file})"
npm run dev -- --host > "${log_file}" 2>&1 &
echo $! > "${pid_file}"
echo "已在后台启动，PID=$(cat "${pid_file}")"

