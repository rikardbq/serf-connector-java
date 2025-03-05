@echo off & setlocal

FOR /F "tokens=*" %%a in ('git rev-list HEAD --count') do SET build_number=%%a
mvn -U versions:set -DnewVersion=%build_number% \
&& mvn clean deploy

exit /b
