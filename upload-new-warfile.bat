rem grails war

IF NOT EXIST "target\chai-crm-0.1.war" GOTO WAR_FILE_NOT_EXISTS
IF EXIST "G:\professional\binaryrepos\chai-crm-0.1-old\" GOTO DELETE_OLD

copy target\chai-crm-0.1.war G:\professional\binaryrepos\chai-crm-0.1.war /y

G:

cd G:\professional\binaryrepos\

move chai-crm-0.1 chai-crm-0.1-old

unzip -o chai-crm-0.1.war -d chai-crm-0.1\

xcopy chai-crm-0.1-old\.git chai-crm-0.1\.git\ /s /y

cd chai-crm-0.1\

git add -A

git gui

start G:\professional\binaryrepos\


:DELETE_OLD
echo "chai-crm-old EXISTS please delete it !!"
GOTO END

:WAR_FILE_NOT_EXISTS
echo "war file not EXISTS please build one!!"
GOTO END

:END