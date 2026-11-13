#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 start <namespace> <service> <local:remote-port> <pid-file> [label]" >&2
  echo "       $0 stop <pid-file> [label]" >&2
}

stop_forward() {
  local pid_file="$1"
  local label="${2:-port-forward}"

  echo "Stopping existing ${label}..."
  if [[ -f "${pid_file}" ]]; then
    local pid
    pid="$(cat "${pid_file}" 2>/dev/null || true)"
    if [[ -n "${pid}" ]] && ps -p "${pid}" >/dev/null 2>&1; then
      kill "${pid}" 2>/dev/null || true
      echo "Stopped ${label} process (PID: ${pid})"
    else
      echo "No running ${label} process found"
    fi
    rm -f "${pid_file}" 2>/dev/null || true
  else
    echo "No previous ${label} PID file found"
  fi
}

if [[ $# -lt 2 ]]; then
  usage
  exit 2
fi

action="$1"
case "${action}" in
  stop)
    if [[ $# -lt 2 || $# -gt 3 ]]; then
      usage
      exit 2
    fi
    stop_forward "$2" "${3:-port-forward}"
    ;;
  start)
    if [[ $# -lt 5 || $# -gt 6 ]]; then
      usage
      exit 2
    fi
    namespace="$2"
    service="$3"
    port_mapping="$4"
    pid_file="$5"
    label="${6:-${namespace}/${service}}"

    stop_forward "${pid_file}" "${label} port-forward"
    mkdir -p "$(dirname "${pid_file}")"
    kubectl port-forward -n "${namespace}" "${service}" "${port_mapping}" \
      >/dev/null 2>&1 &
    port_forward_pid=$!
    echo "${port_forward_pid}" > "${pid_file}"
    echo "Started ${label} port-forward (PID: ${port_forward_pid})"
    ;;
  *)
    usage
    exit 2
    ;;
esac
