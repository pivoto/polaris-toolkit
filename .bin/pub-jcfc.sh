#!/bin/bash
# bashsupport disable=BP5006
SHELL_NAME=$(basename $0)
SHELL_DIR=$(cd "$(dirname $0)" && pwd -P)
WORK_DIR=${PWD}

nohup sh "${SHELL_DIR}"/deploy-jcfc-all.sh &> nohup.out &
tail -f nohup.out
