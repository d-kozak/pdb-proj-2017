#!/usr/bin/env bash
rm -f xkozak15.zip
rm -f xplask00.zip
rm -f xvelec07.zip

zip -r -9 xkozak15.zip src body.txt build.sh clean.sh compile.sh distrib.sh init_db.sql clear_db.sql pom.xml README.md run.sh TODO.md
zip -r -9 xplask00.zip documents 
