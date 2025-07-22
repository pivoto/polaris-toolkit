#!/bin/bash

sh $(cd $(dirname $0) && pwd -P)/main.sh -jcfc -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central-pivoto.sh
sh $(cd $(dirname $0) && pwd -P)/main.sh -jcfc -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central.sh
