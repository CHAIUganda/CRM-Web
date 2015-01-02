rem grails war
copy target\chai-crm-0.1.war G:\professional\binaryrepos\chai-crm-0.1.war /y
G:
cd G:\professional\binaryrepos\
move chai-crm-0.1 chai-crm-0.1-old
rem winrar x chai-crm-0.1.war chai-crm-0.1\
unzip -o chai-crm-0.1.war -d chai-crm-0.1\
xcopy chai-crm-0.1-old\.git chai-crm-0.1\.git\ /s /y
cd chai-crm-0.1\
git add -A
git gui
start G:\professional\binaryrepos\