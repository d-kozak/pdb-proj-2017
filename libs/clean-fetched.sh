#!/bin/sh

DIR=$(dirname "${0}")

for I in $(find "${DIR}" -type f -name '*.pom'); do
	ARTPATH=`realpath --relative-to="${DIR}" $(dirname "${I}")`
	MVNPATH=~/.m2/repository/"${ARTPATH}"
	rm -vrf "${MVNPATH}"
done
