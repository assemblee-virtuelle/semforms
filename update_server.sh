#!/bin/bash

echo "update Play! server when code has changed"
echo maybe "cd ~/src/semantic_forms/scala/forms ; git pull --verbose ; sbt publishLocal" before ?
echo
SRC=$HOME/src/semforms
APP=semantic_forms_av
APPVERS=${APP}-1.0-SNAPSHOT
SBT=sbt

cd $SRC
git pull --verbose
$SBT dist
echo "sofware recompiled!"

cd ~/deploy
kill `cat ${APPVERS}/RUNNING_PID`

# pour garder les logs
rm -r ${APPVERS}_OLD
mv ${APPVERS} ${APPVERS}_OLD

unzip $SRC/target/universal/${APPVERS}.zip

cd ${APPVERS}
ln -s ../TDBsf TDB
nohup bin/${APP} -mem 300 -J-server -Dhttp.port=9000 &
