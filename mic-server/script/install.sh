#! /bin/bash

#新建/data/www/zyyh-mic
#复制 zyyh-mic.jar zyyh-mic.properties api-jar.sh
#修改hospital owner
#修改web owner
#修改

# 路径
zyyh_mic_dir=/data/www/zyyh-mic

is_empty_dir(){
    return `ls -A $1|wc -w`
}

# 执行脚本 停止进程
bash api-jar.sh stop zyyh-mic.jar

# 如果目录不存在，则创建目录
if is_empty_dir $zyyh_mic_dir
	then
	    echo "mkdir -p $zyyh_mic_dir"
        mkdir -p $zyyh_mic_dir
fi

# 拷贝当前目录的全部脚本到 工作目录，并添加执行权限
cp * $zyyh_mic_dir
chmod +x $zyyh_mic_dir/api-jar.sh

chown www.www  ${zyyh_mic_dir} -R

chmod 755 -R ${zyyh_mic_dir}

# 开机启动
grep -r "api-jar" /etc/rc.d/rc.local > /dev/null
if [[ $? -ne 0 ]];then
echo "/data/www/zyyh-mic/api-jar.sh start zyyh-mic.jar" >> /etc/rc.d/rc.local
fi

# 启动进程
/data/www/zyyh-mic/api-jar.sh restart zyyh-mic.jar

echo "完成"


