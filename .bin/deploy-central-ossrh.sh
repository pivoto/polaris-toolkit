#!/bin/bash

set -Ee
set -uo pipefail

sh $(cd $(dirname $0) && pwd -P)/main.sh -central -g deploy 2>&1 | tee deploy-central.log
sh $(cd $(dirname $0) && pwd -P)/change-ossrh.sh 2>&1 | tee -a deploy-central.log
sh $(cd $(dirname $0) && pwd -P)/main.sh -ossrh -g deploy 2>&1 | tee -a deploy-central.log
sh $(cd $(dirname $0) && pwd -P)/change-central.sh 2>&1 | tee -a deploy-central.log

