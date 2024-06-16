#!/bin/bash

set -Ee
set -uo pipefail

sh $(cd $(dirname $0) && pwd -P)/main.sh -central -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-ossrh.sh
sh $(cd $(dirname $0) && pwd -P)/main.sh -ossrh -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central.sh

