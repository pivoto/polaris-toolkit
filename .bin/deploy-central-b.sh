#!/bin/bash

sh $(cd $(dirname $0) && pwd -P)/deploy-central.sh 2>&1 | tee deploy-central.log
