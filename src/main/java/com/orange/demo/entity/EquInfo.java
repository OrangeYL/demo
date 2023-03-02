package com.orange.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/28 14:44
 * @description: 设备信息
 */
@Data
@TableName("equ_info")
public class EquInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    //设备名称
    @TableField("e_name")
    private String eName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;

    //设备详细数据
    @TableField(exist = false)
    private List<EquDetailsInfo> list = new ArrayList<>();

}
