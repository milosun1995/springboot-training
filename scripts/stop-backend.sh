#!/usr/bin/env bash
set -euo pipefail

chapter="${1:-}"
if [[ -z "${chapter}" ]]; then
  echo "用法: $0 <chapter-xx-*> 或 final-project" >&2
  exit 1
fi

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
pid_file="${repo_root}/scripts/pids/${chapter}-backend.pid"

if [[ ! -f "${pid_file}" ]]; then
  echo "未找到 PID 文件: ${pid_file}，可能未启动或已停止" >&2
  exit 1
fi

pid="$(cat "${pid_file}")"
if ps -p "${pid}" >/dev/null 2>&1; then
  echo "停止后端 ${chapter} (PID=${pid})"
  kill "${pid}"
  rm -f "${pid_file}"
  echo "已停止"
else
  echo "进程不存在，清理 PID 文件" >&2
  rm -f "${pid_file}"
fi

