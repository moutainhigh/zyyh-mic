echo 当前盘符: %~d0
%~d0
echo %~dp0
cd %~dp0

D:\data\www\zyyh-mic\nssm.exe stop zyyh-mic
D:\data\www\zyyh-mic\nssm.exe remove zyyh-mic confirm

mkdir D:\data\www\zyyh-mic\

copy * D:\data\www\zyyh-mic\

D:\data\www\zyyh-mic\nssm.exe install zyyh-mic java.exe
D:\data\www\zyyh-mic\nssm.exe set zyyh-mic Application java.exe
D:\data\www\zyyh-mic\nssm.exe set zyyh-mic AppDirectory D:\data\www\zyyh-mic
D:\data\www\zyyh-mic\nssm.exe set zyyh-mic AppParameters -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -jar zyyh-mic.jar
D:\data\www\zyyh-mic\nssm.exe set zyyh-mic Description his消息中心
D:\data\www\zyyh-mic\nssm.exe set zyyh-mic Start SERVICE_DELAYED_START
D:\data\www\zyyh-mic\nssm.exe status zyyh-mic
D:\data\www\zyyh-mic\nssm.exe start zyyh-mic
pause