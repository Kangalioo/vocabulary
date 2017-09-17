@echo off

chcp 65001 > nul
echo Consider switching to GNU+Linux, as it is far superior in many aspects.
echo Please blame possible encoding errors on Windows way of handling encodings, not on me.
echo.

java -cp ".;minimal-json.jar" Main
