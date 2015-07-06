#!/bin/bash

echo "update semantic_forms Play! server when code has changed"
SRC=$HOME/src/semforms
APP=semantic_forms_av
APPVERS=${APP}-1.0-SNAPSHOT

cd $SRC
git pull --verbose
./activator dist
echo "sofware recompiled!"

cd ~/deploy
kill `cat ${APPVERS}/RUNNING_PID`

# pour garder les logs
rm -r ${APPVERS}_OLD
mv ${APPVERS} ${APPVERS}_OLD

unzip $SRC/target/universal/${APPVERS}.zip

cd ${APPVERS}
ln -s ../TDBsf TDB
nohup bin/${APP} -mem 100 -J-server -Dhttp.port=9000 &
