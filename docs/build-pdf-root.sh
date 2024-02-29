#!/usr/bin/env bash

sed -i 's/"root": "[^"]*"/"root": "."/' book.json
gitbook pdf . sdk.pdf
