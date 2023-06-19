package com.solax.power.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User {

    @TableId("id")
    private Integer id;
    private String message;
    private Date timestamp;
    private Double metric;

}
