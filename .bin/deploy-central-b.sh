#!/bin/bash

sh $(cd $(dirname $0) && pwd -P)/main.sh -central -g deploy 2>&1 | tee deploy-central.log
