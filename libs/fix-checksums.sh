#!/bin/sh

DIR=$(dirname "${0}")

for I in $(find "${DIR}" -type f -name '*.md5'); do
	echo "${I}"
	md5sum "${I%.md5}" | cut -d ' ' -f 1 > "${I}"
done

for I in $(find "${DIR}" -type f -name '*.sha1'); do
	echo "${I}"
	sha1sum "${I%.sha1}" | cut -d ' ' -f 1 > "${I}"
done
