#!/bin/bash

rm -rf target
mkdir target
cp -r shop/target/surefire-reports/* target
cp -r scenarioTest/target/surefire-reports/* target
cp -r payment/target/surefire-reports/* target
cp -r fulfillment/target/surefire-reports/* target
cp -r catalog/target/surefire-reports/* target
cp -r shopManager/target/surefire-reports/* target

/Applications/dev/sonar-scanner-3.0.3.778-macosx/bin/sonar-scanner