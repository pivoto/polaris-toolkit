#!/bin/bash

sh $(cd $(dirname $0) && pwd -P)/change-central-fossc.sh
sh $(cd $(dirname $0) && pwd -P)/main.sh -central -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central.sh
