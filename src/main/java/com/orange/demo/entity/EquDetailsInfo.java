package com.orange.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/28 14:45
 * @description: 设备详细信息
 */
@Data
public class EquDetailsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String eId;

    private String machineType;

    private String boardId;

    private String arrayId;

    private String padNo;

    private String inspStTime;

    private Double inspVol;

    private Double inspArea;

    private Double inspHei;

    private Double inspX;

    private Double inspY;

    private String itemNum;

    private String sn;

}
