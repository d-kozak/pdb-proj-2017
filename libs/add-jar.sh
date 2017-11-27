#!/bin/sh

RELDIR=$(dirname "${0}") DIR=$(realpath "${RELDIR}")
JARFILE=${1}
GROUPID=${2}
ARTIFACTID=${3}
VERSIONNO=${4:-0}

if [[ "${1}" == "--help" || ! -e "${JARFILE}" || -z "${GROUPID}" || -z "${ARTIFACTID}"} ]]; then
	echo "Usage: ${0} <library.jar> <group-ip> <artifact-id> [version-number]" >&2
	echo "Add JAR library into a local Maven repository with given Group ID, Artifact ID, and optionally also Version Number." >&2
	exit -1
fi

exec mvn deploy:deploy-file \
	-Durl=file://${DIR} \
	-Dfile=${JARFILE} -Dpackaging=jar \
	-DgroupId=${GROUPID} -DartifactId=${ARTIFACTID} -Dversion=${VERSIONNO}
