#! /bin/bash    

. /etc/profile 
argv="$1"
proc="$2"
dp="$3"

RUN_USER=www
SHUTDOWN_WAIT=10
if [ -z "$JAVA_OPT" ]; then
    echo "JAVA_OPT is empty"
    JAVA_OPT="-XX:+UseConcMarkSweepGC -Xms512m -Xmx512m"
fi

GetPid(){
    ps aux | grep "$proc" | egrep -v "grep|bash|api-jar" | awk '{print $2}'
}

if [ "$3" -gt 0 ] 2>/dev/null ;then
        pIDa=`/usr/sbin/lsof -i :$dp|grep -v "$(GetPid)"|grep -v "PID"|awk '{print $2}'`
	if [ "$pIDa" != "" ];
	then
  	    echo "Debug port $dp is using"
	else
	    JAVA_OPT="$JAVA_OPT -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$dp"
	fi
fi

Stop(){
   pid=$(GetPid)
   if [ -n "$pid" ];then
        echo -e "\e[00;31mStoping $proc\e[00m"
 	kill $pid
        let kwait=$SHUTDOWN_WAIT
    count=0;
    until [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
    do
      echo -n -e "\e[00;31mwaiting for processes to exit $count\e[00m\n";
      sleep 1
      let count=$count+1;
    done

    if [ $count -gt $kwait ];then
      echo  -e "\n\e[00;31mkilling processes which didn't stop after $SHUTDOWN_WAIT seconds\e[00m"
      kill -9 $pid
    fi
  else
    echo -e "\e[00;31m$proc is not running\e[00m"
  fi

  return 0
}

Start(){
    API_PATH=/data/www/zyyh-mic
    if [ "X`GetPid`" != "X" ]
    then
        echo -e "$proc is already running with Pid `GetPid`"
        exit 1
    fi

    if [ "$USER" = "root" ]
    then
        echo -e "Starting $proc ... "
        su - $RUN_USER -c "cd $API_PATH;nohup java $JAVA_OPT -jar $proc > /dev/null 2>&1 &"
        echo -e "done"
    elif [ "$USER" = "$RUN_USER" ]
    then
        echo -e "Starting $proc ... "
        cd $API_PATH;nohup java $JAVA_OPT -jar $proc > /dev/null 2>&1 &
        echo -e "done"
    else
        echo -e "Please run by root or $RUN_USER"
        exit 1
    fi
}

case "$argv" in
    restart)
        Stop
        Start  
        ;;

    start)
        Start
        ;;
    stop)
        Stop
        ;;
esac 

