package com.orange.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Li ZhiCheng
 * @create: 2023-04-2023/4/7 10:45
 * @description:
 */
@Data
public class ViInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemNum;

    private String sn;

    private String machineType;

    private Double dX;

    private Double dY;

    private Double dTheta;

    private String errCode;
}
