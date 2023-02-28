package com.orange.demo.entity;

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
public class EquInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    //设备名称
    private String eName;

    //设备类型
    private String eType;

    //设备详细数据
    private List<EquDetailsInfo> list = new ArrayList<>();

}
