#!/usr/bin/env bash
rm -f xkozak15.zip
rm -f xplask07.zip
rm -f xvelec07.zip

zip -r -9 xkozak15.zip documents/user_documentation/documentation.pdf src body.txt build.sh clean.sh compile.sh distrib.sh init_db.sql clear_db.sql pom.xml README.md run.sh TODO.md
zip -r -9 xplask07.zip documents/temporal_documentation documents/user_documentation
zip -r -9 xvelec07.zip documents/documentation
