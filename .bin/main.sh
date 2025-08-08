#!/bin/bash

set -Ee
set -o pipefail
#set -u
#set -xv

## 脚本名称及路径
SHELL_NAME=$(basename $0)
SHELL_DIR=$(cd $(dirname $0) && pwd -P)
WORK_DIR=${PWD}
## 当日日期
export LANG=en_US.utf8
export DATE=$(date +%Y%m%d)
export TIME="$(date +'%b %d %H:%M:%S %Y %:z')"

## 全局变量
export SUCCESS=0
export FAIL=1

## 颜色变量
COLOR_ON=0
COLOR_END='\x1b[0m'
COLOR_RED='\x1b[1;31m'
COLOR_GREEN='\x1b[1;32m'
COLOR_ORANGE='\x1b[1;33m'
COLOR_BLUE='\x1b[1;34m'
COLOR_PURPLE='\x1b[1;35m'
COLOR_CYAN='\x1b[1;36m'

trap cleanup SIGINT SIGTERM ERR EXIT

cleanup() {
	trap - SIGINT SIGTERM ERR EXIT
}

setup_colors() {
	if [[ ${COLOR_ON} == "1" ]]; then
		COLOR_END='\x1b[0m'
		COLOR_RED='\x1b[1;31m'
		COLOR_GREEN='\x1b[1;32m'
		COLOR_ORANGE='\x1b[1;33m'
		COLOR_BLUE='\x1b[1;34m'
		COLOR_PURPLE='\x1b[1;35m'
		COLOR_CYAN='\x1b[1;36m'
	else
		COLOR_END=''
		COLOR_RED=''
		COLOR_GREEN=''
		COLOR_ORANGE=''
		COLOR_BLUE=''
		COLOR_PURPLE=''
		COLOR_CYAN=''
	fi
}
clear_msg_colors() {
	echo $(echo "$*" | sed -r -e 's/\x1b\[[0-9;]+m//g')
}
clear_file_colors() {
	sed -i -r -e 's/\x1b\[[0-9;]+m//g' $*
}

log() {
	echo -e "${COLOR_CYAN}[$(date '+%Y-%m-%d %H:%M:%S')]${COLOR_END} $*"
}
log_debug() {
	log "${COLOR_GREEN}[DEBUG]${COLOR_END} $*"
}
log_info() {
	log "${COLOR_BLUE}[INFO]${COLOR_END} $*"
}
log_warn() {
	log "${COLOR_ORANGE}[WARN]${COLOR_END} $*"
}
log_error() {
	log "${COLOR_RED}[ERROR]${COLOR_END} $*"
}

error() {
	log "${COLOR_RED}[ERROR]${COLOR_END} $*"
	exit ${FAIL}
}

declare -A ARGS=()
parse_args() {
	while [[ $# -gt 0 ]]; do
		if [[ "${1:0:1}" == "-" ]]; then
			key=${1:1}
			shift 1
			if [[ $# -eq 0 || -z "${1}" || "${1:0:1}" == "-" ]]; then
				ARGS["${key}"]=true
			else
				if [[ -z ${ARGS["${key}"]} ]];then
					ARGS["${key}"]=$1
				else
					ARGS["${key}"]="${ARGS["${key}"]} $1"
				fi
				shift 1
			fi
		else
			error "${COLOR_RED}Error! unsupported args: ${1} ${COLOR_END}"
		fi
	done
}

show_args() {
	log_debug "Args Count: ${#ARGS[*]}"
	for key in ${!ARGS[*]}; do
		log_debug "Arg: ${key} => ${ARGS[${key}]}"
	done
}

parse_args $@



filter_true(){
	echo "$1" | grep -E '^(true|on|yes|y|1)$'
}
filter_false(){
	echo "$1" | grep -E '^(false|off|no|n|0)$'
}
usage() {
	cat <<EOF
Usage: ${SHELL_DIR}/${SHELL_NAME} -h

	-h, -help)  显示帮助信息
	-c, -color) true|false  默认为true
	-g, -goal) install|deploy 默认为install
	-p, -profile) 启用的profile
	-central) true|false 是否启用dist-central,配置`-profile`时忽略
	-jcfc) true|false 是否启用dist-jcfc,配置`-profile`时忽略
	-e, -errors) Produce execution error messages
	-x, -debug) Produce execution debug output


EOF
}

declare -l ARG_HELP=${ARGS[h]:-${ARGS[help]}}
declare -l ARG_COLOR=${ARGS[c]:-${ARGS[color]}}

if [[ -n "$(filter_true ${ARG_HELP})" ]]; then
	usage
	exit
fi

if [[ -n "$(filter_false ${ARG_COLOR})" ]]; then
	COLOR_ON=0
	setup_colors
else
	COLOR_ON=1
	setup_colors
fi

log_debug "脚本目录: ${SHELL_DIR}"
log_debug "工作目录: ${WORK_DIR}"
log_debug ""
log_debug "脚本参数如下："
show_args
log_debug ""

declare goal=${ARGS[g]:-${ARGS[goal]}}
declare profile=${ARGS[p]:-${ARGS[profile]}}
declare errors=${ARGS[e]:-${ARGS[errors]}}
declare debug=${ARGS[x]:-${ARGS[debug]}}

if [[ -z $goal ]];then
	goal="install"
fi
goal="clean ${goal}"
log_debug "goal: ${goal}"

if [[ -z ${profile} ]]; then
	if [[ -n "$(filter_true ${ARGS[central]})" ]]; then
		profile="dist,dist-central"
	elif [[ -n "$(filter_true ${ARGS[jcfc]})" ]];then
		profile="withDoc,dist-jcfc"
	fi
fi

cd ${SHELL_DIR}

GROUP_ID=$(grep -Eo '<groupId>([A-Za-z0-9.-]+)</groupId>' ../pom.xml --max-count=1)
GROUP_ID=${GROUP_ID/<groupId>/}
GROUP_ID=${GROUP_ID/<\/groupId>/}

ARTIFACT_ID=$(grep -Eo '<artifactId>([A-Za-z0-9.-]+)</artifactId>' ../pom.xml --max-count=1)
ARTIFACT_ID=${ARTIFACT_ID/<artifactId>/}
ARTIFACT_ID=${ARTIFACT_ID/<\/artifactId>/}

VERSION=$(grep -Eo '<version>([A-Za-z0-9.-]+)</version>' ../pom.xml --max-count=1)
VERSION=${VERSION/<version>/}
VERSION=${VERSION/<\/version>/}

# -Dmaven.test.skip=true
CMD="mvn -f ../pom.xml -DskipTests -Dfile.encoding=UTF-8"
if [[ -z ${profile} ]]; then
	log_warn "未配置profile参数"
else
	log_debug "profile: ${profile}"
	CMD="${CMD} -P ${profile}"
fi
if [[ -n ${errors} ]]; then
	CMD="${CMD} --errors"
fi
if [[ -n ${debug} ]]; then
	CMD="${CMD} --debug"
fi

CMD="${CMD} ${goal}"
export MAVEN_OPTS="-Dfile.encoding=UTF-8"
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
log_debug "当前目录: ${PWD}"
log_debug "执行命令: ${CMD}"

${CMD}
if [[ $? -eq 0 ]];then
	cd ${WORK_DIR}
	echo "$(date +'%Y-%m-%d %H:%M:%S') ${GROUP_ID}:${ARTIFACT_ID}:${VERSION} BUILD SUCCESS"  >> version.log
else
	cd ${WORK_DIR}
	echo "$(date +'%Y-%m-%d %H:%M:%S') ${GROUP_ID}:${ARTIFACT_ID}:${VERSION} BUILD FAILURE"  >> version.log
fi

cd ${WORK_DIR}



