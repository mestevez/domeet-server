"#domeet-server" 

!!! GRADLE IS REQUIRED FOR STARTING SERVER

compile webapp Javascript application
!!! NPM IS REQUIRED FOR EXECUTING THIS TASK
 - gradlew.bat npm
 
create database
 - gradlew database -Pcreate
drop database
 - gradlew database -Pdrop
load database data
 - gradlew database -Pload
check database status
 - gradlew database -Pstatus

run server
 - gradlew appRun
