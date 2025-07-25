#!/bin/bash

grep -E '<version>' $(cd $(dirname $0) && pwd -P)/pom.xml  --max-count=1 >> $(cd $(dirname $0) && pwd -P)/version.log
sh $(cd $(dirname $0) && pwd -P)/main.sh -jcfc -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central-pivoto.sh
sh $(cd $(dirname $0) && pwd -P)/main.sh -jcfc -g deploy
sh $(cd $(dirname $0) && pwd -P)/change-central.sh
