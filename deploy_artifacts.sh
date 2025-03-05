#!/usr/bin/env bash

build_version=$(git rev-list HEAD --count)
mvn -U versions:set -DnewVersion=${build_version}
mvn clean deploy

exit 0
