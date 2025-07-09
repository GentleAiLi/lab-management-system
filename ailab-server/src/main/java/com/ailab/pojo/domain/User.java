package com.ailab.pojo.domain;

import com.ailab.common.enums.UserRoleEnum;
import com.ailab.common.enums.UserStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user")
public class User {

/* -- 用户
CREATE TABLE IF NOT EXISTS user
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
    account_name VARCHAR(11)  NOT NULL UNIQUE COMMENT '账户名',
    password     VARCHAR(100) NOT NULL COMMENT '密码，加密存储',
    name         VARCHAR(11)  NOT NULL COMMENT '用户姓名',
    phone        CHAR(11) COMMENT '用户电话',
    role         TINYINT      NOT NULL COMMENT '身份权限，1管理员，0普通用户',
    sno          CHAR(11) UNIQUE COMMENT '学号',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '账号状态，0禁用，1启用',
    create_time  DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';*/

    @TableId(type = IdType.AUTO)
    private Long id; // 用户id
    private String accountName; // 账户名
    private String password; // 密码，加密存储
    private String name; // 用户姓名
    private String phone; // 用户电话
    private UserRoleEnum role; // 身份权限，1管理员，0普通用户
    private String sno; // 学号
    private UserStatusEnum status; // 账号状态，0禁用，1启用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    @Override
    public String toString() {
        return "UserDO{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", sno='" + sno + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
