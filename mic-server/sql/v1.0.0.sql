CREATE TABLE `mic_msg`
(
    `id`              bigint(20)  NOT NULL AUTO_INCREMENT,
    `content`         text COMMENT '消息文本',
    `msg_identify`    char(64)    NOT NULL COMMENT '消息标识',
    `mic_biz_type` varchar(20) DEFAULT 'UNKNOWN' COMMENT '业务类型 科室 病区 科室病区关系 医护 病人 医嘱 未知',
    `mic_biz_event`   varchar(20) null default null comment '业务事件',
    `is_consume`      tinyint(4)       DEFAULT '0' COMMENT '院内是否已消费  1 表示是，0 表示否\n',
    `retry_count`     int(11)     NOT NULL COMMENT '院内消费重试次数 默认 0',
    `max_retry_count` int(11)     NOT NULL COMMENT '最大重试次数',
    `gmt_create`      datetime(3)      DEFAULT CURRENT_TIMESTAMP(3),
    `gmt_modify`      datetime(3)      DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `msg_identify_UNIQUE` (`msg_identify`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='his消息表';

CREATE TABLE `mic_msg_history`
(
    `id`              bigint(20)  NOT NULL,
    `content`         text COMMENT '消息文本',
    `msg_identify`    char(64)    NOT NULL COMMENT '消息标识',
    `mic_biz_type` varchar(20) DEFAULT 'UNKNOWN' COMMENT '业务类型 科室 病区 科室病区关系 医护 病人 医嘱 未知',
    `mic_biz_event`   varchar(20) null default null comment '业务事件',
    `is_consume`      tinyint(4)       DEFAULT '0' COMMENT '院内是否已消费  1 表示是，0 表示否\n',
    `retry_count`     int(11)     NOT NULL COMMENT '院内消费重试次数 默认 0',
    `max_retry_count` int(11)     NOT NULL COMMENT '最大重试次数',
    `gmt_create`      datetime(3)      DEFAULT NULL,
    `gmt_modify`      datetime(3)      DEFAULT NULL,
    `archive_time`    datetime(3)      DEFAULT CURRENT_TIMESTAMP(3),
    INDEX `idx_ix` (id),
    INDEX `msg_identify` (msg_identify)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='his消息归档表';