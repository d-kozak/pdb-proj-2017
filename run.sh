#!/bin/sh

exec mvn exec:java -Dexec.mainClass="cz.vutbr.fit.pdb.App" -Dexec.args="$*"
