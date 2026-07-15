#!/bin/bash

sh $(cd $(dirname $0) && pwd -P)/change-central.sh
sh $(cd $(dirname $0) && pwd -P)/main.sh -central -g deploy
