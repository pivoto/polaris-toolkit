#!/bin/bash

WORK_DIR=${PWD}
SHELL_DIR=$(cd "$(dirname "$0")" && pwd -P)
cd "${SHELL_DIR}"/ || exit

mvn -f ../pom.xml -DskipTests help:active-profiles
