-- 猫眼演出练习库表结构。示例数据在 insert.sql。
-- 重复执行会保留 user、show_type、artist、admin 和 admin_session，并重建城市、场馆、演出、场次、票档、座位和票。
CREATE DATABASE IF NOT EXISTS maoyan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE maoyan;

CREATE TABLE IF NOT EXISTS `user` (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    UNIQUE KEY uk_user_phone (phone)
) ENGINE = InnoDB COMMENT = '用户表';

CREATE TABLE IF NOT EXISTS show_type (
    id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '演出类型编号',
    name VARCHAR(50)  NOT NULL COMMENT '演出类型名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_show_type_name (name)
) ENGINE = InnoDB COMMENT = '演出类型表';

CREATE TABLE IF NOT EXISTS artist (
    id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '艺人编号',
    name   VARCHAR(100) NOT NULL COMMENT '艺人名称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    intro  TEXT         DEFAULT NULL COMMENT '简介',
    PRIMARY KEY (id),
    UNIQUE KEY uk_artist_name (name)
) ENGINE = InnoDB COMMENT = '艺人表';

CREATE TABLE IF NOT EXISTS admin (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员编号',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码摘要',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_username (username)
) ENGINE = InnoDB COMMENT = '管理员表';

CREATE TABLE IF NOT EXISTS admin_session (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '会话编号',
    admin_id    BIGINT      NOT NULL COMMENT '管理员编号',
    token_hash  CHAR(64)    NOT NULL COMMENT '令牌摘要',
    expire_time DATETIME    NOT NULL COMMENT '过期时间',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签发时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_session_token (token_hash),
    CONSTRAINT fk_admin_session_admin FOREIGN KEY (admin_id) REFERENCES admin (id)
) ENGINE = InnoDB COMMENT = '管理员登录会话';

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS price_tier;
DROP TABLE IF EXISTS show_session;
DROP TABLE IF EXISTS `show`;
DROP TABLE IF EXISTS venue;
DROP TABLE IF EXISTS city;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE city (
    id   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '城市编号',
    name VARCHAR(20) NOT NULL COMMENT '城市名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_city_name (name)
) ENGINE = InnoDB COMMENT = '城市表';

CREATE TABLE venue (
    id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '场馆编号',
    city_id BIGINT       NOT NULL COMMENT '所在城市',
    name    VARCHAR(100) NOT NULL COMMENT '场馆名称',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    PRIMARY KEY (id),
    UNIQUE KEY uk_venue_city_name (city_id, name),
    CONSTRAINT fk_venue_city FOREIGN KEY (city_id) REFERENCES city (id)
) ENGINE = InnoDB COMMENT = '场馆表';

CREATE TABLE `show` (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '演出编号',
    show_type_id     BIGINT        NOT NULL COMMENT '演出类型编号',
    city_id          BIGINT        NOT NULL COMMENT '列表使用的城市',
    name             VARCHAR(100)  NOT NULL COMMENT '演出名称',
    artist           VARCHAR(100)  DEFAULT NULL COMMENT '艺人',
    poster           VARCHAR(255)  DEFAULT NULL COMMENT '海报地址',
    duration         INT           DEFAULT NULL COMMENT '时长，单位分钟',
    intro            TEXT          DEFAULT NULL COMMENT '演出介绍',
    notice           TEXT          DEFAULT NULL COMMENT '购票须知',
    venue_name       VARCHAR(100)  NOT NULL COMMENT '场馆',
    address          VARCHAR(200)  DEFAULT NULL COMMENT '地址',
    seat_enabled     TINYINT       NOT NULL DEFAULT 0 COMMENT '1可选座 0不可选座',
    refund_enabled   TINYINT       NOT NULL DEFAULT 0 COMMENT '1可退 0不支持退',
    pickup_type      VARCHAR(20)   NOT NULL DEFAULT '电子票' COMMENT '取票方式',
    max_per_order    INT           NOT NULL DEFAULT 6 COMMENT '每笔订单限购张数',
    sale_start_time  DATETIME      DEFAULT NULL COMMENT '开售时间，空表示立即开售',
    sale_end_time    DATETIME      DEFAULT NULL COMMENT '停售时间，空表示开演前可售',
    stopped          TINYINT       NOT NULL DEFAULT 0 COMMENT '1手动停售',
    PRIMARY KEY (id),
    KEY idx_show_city_type (city_id, show_type_id),
    CONSTRAINT fk_show_type FOREIGN KEY (show_type_id) REFERENCES show_type (id),
    CONSTRAINT fk_show_city FOREIGN KEY (city_id) REFERENCES city (id)
) ENGINE = InnoDB COMMENT = '演出表';

CREATE TABLE show_session (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '场次编号',
    show_id         BIGINT       NOT NULL COMMENT '演出编号',
    city_id         BIGINT       NOT NULL COMMENT '本场次所在城市',
    venue_name      VARCHAR(100) NOT NULL COMMENT '场馆',
    address         VARCHAR(200) DEFAULT NULL COMMENT '地址',
    show_time       DATETIME     NOT NULL COMMENT '开演时间',
    sale_start_time DATETIME     DEFAULT NULL COMMENT '开售时间，空则沿用演出',
    sale_end_time   DATETIME     DEFAULT NULL COMMENT '停售时间，空则沿用演出',
    stopped         TINYINT      NOT NULL DEFAULT 0 COMMENT '1停售',
    PRIMARY KEY (id),
    KEY idx_session_show (show_id),
    KEY idx_session_city_time (city_id, show_time),
    CONSTRAINT fk_session_show FOREIGN KEY (show_id) REFERENCES `show` (id),
    CONSTRAINT fk_session_city FOREIGN KEY (city_id) REFERENCES city (id)
) ENGINE = InnoDB COMMENT = '场次表';

CREATE TABLE price_tier (
    id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '票档编号',
    session_id     BIGINT        NOT NULL COMMENT '场次编号',
    name           VARCHAR(50)   NOT NULL COMMENT '票档名称',
    price          DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价，空表示无优惠',
    discount_text  VARCHAR(50)   DEFAULT NULL COMMENT '折扣或满减说明',
    stock          INT           NOT NULL DEFAULT 0 COMMENT '余票；可选座演出以座位表为准',
    PRIMARY KEY (id),
    KEY idx_tier_session (session_id),
    CONSTRAINT fk_tier_session FOREIGN KEY (session_id) REFERENCES show_session (id)
) ENGINE = InnoDB COMMENT = '票档表';

CREATE TABLE seat (
    id            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '座位编号',
    session_id    BIGINT      NOT NULL COMMENT '场次编号',
    price_tier_id BIGINT      NOT NULL COMMENT '票档编号',
    row_label     VARCHAR(10) NOT NULL COMMENT '排',
    seat_label    VARCHAR(10) NOT NULL COMMENT '座',
    status        TINYINT     NOT NULL DEFAULT 0 COMMENT '0可选 1已售',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seat_position (session_id, row_label, seat_label),
    KEY idx_seat_tier (price_tier_id),
    CONSTRAINT fk_seat_session FOREIGN KEY (session_id) REFERENCES show_session (id),
    CONSTRAINT fk_seat_tier FOREIGN KEY (price_tier_id) REFERENCES price_tier (id)
) ENGINE = InnoDB COMMENT = '座位表';

CREATE TABLE ticket (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '票编号',
    order_no      VARCHAR(32)   NOT NULL COMMENT '订单号',
    user_id       BIGINT        NOT NULL COMMENT '用户编号',
    show_id       BIGINT        NOT NULL COMMENT '演出编号',
    session_id    BIGINT        NOT NULL COMMENT '场次编号',
    price_tier_id BIGINT        NOT NULL COMMENT '票档编号',
    seat_id       BIGINT        DEFAULT NULL COMMENT '座位编号，不选座时为空',
    venue_name    VARCHAR(100)  NOT NULL COMMENT '场馆',
    show_time     DATETIME      NOT NULL COMMENT '开演时间',
    seat_no       VARCHAR(20)   DEFAULT NULL COMMENT '座位，不选座时为空',
    price         DECIMAL(10,2) NOT NULL COMMENT '单价',
    status        TINYINT       NOT NULL DEFAULT 1 COMMENT '0待支付 1已出票 2已退票',
    lock_seat_id  BIGINT        GENERATED ALWAYS AS (
                      CASE
                          WHEN status IN (0, 1) AND seat_id IS NOT NULL THEN seat_id
                          ELSE NULL
                      END
                  ) STORED COMMENT '待支付或已出票时占用的座位',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ticket_lock_seat (lock_seat_id),
    KEY idx_ticket_user_id (user_id),
    KEY idx_ticket_order_no (order_no),
    KEY idx_ticket_show_id (show_id),
    KEY idx_ticket_session_id (session_id),
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES `user` (id),
    CONSTRAINT fk_ticket_show FOREIGN KEY (show_id) REFERENCES `show` (id),
    CONSTRAINT fk_ticket_session FOREIGN KEY (session_id) REFERENCES show_session (id),
    CONSTRAINT fk_ticket_tier FOREIGN KEY (price_tier_id) REFERENCES price_tier (id),
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat (id)
) ENGINE = InnoDB COMMENT = '票表';
