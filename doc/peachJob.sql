CREATE TABLE `lock_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `owner` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '持有者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_name` (`name`)
) ENGINE=InnoDB;


CREATE TABLE `service_registry` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(128) COLLATE utf8mb4_general_ci NOT NULL,
  `status` tinyint NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_ip` (`app_name`,`address`) USING BTREE
) ENGINE=InnoDB;


CREATE TABLE `task_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务更新时间',
  `valid` tinyint DEFAULT NULL COMMENT '是否有效',
  `estimated_execution_time` datetime NOT NULL COMMENT '预计执行时间',
  `actual_execution_time` datetime DEFAULT NULL COMMENT '实际执行时间',
  `task_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器名称',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
  `execute_params` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行参数(可以为空)',
  `max_retry_num` int DEFAULT '0' COMMENT '最大重试次数',
  `execution_times` int DEFAULT '0' COMMENT '执行次数',
  `status` tinyint NOT NULL COMMENT '执行状态',
  `execution_strategy` tinyint NOT NULL COMMENT '执行策略',
  `result` varchar(4096) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行结果',
  PRIMARY KEY (`id`),
  KEY `idx_status_estimated` (`status`,`estimated_execution_time`) USING BTREE
) ENGINE=InnoDB;


CREATE TABLE `task_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `status` tinyint NOT NULL COMMENT '执行结果',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行地址',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '执行结果',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

