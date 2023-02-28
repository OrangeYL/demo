package com.orange.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
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
    private String eName;

    //设备类型
    private String eType;

    //设备详细数据
    @TableField(exist = false)
    private List<EquDetailsInfo> list = new ArrayList<>();

}
