package com.orange.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Li ZhiCheng
 * @create: 2023-04-2023/4/13 14:48
 * @description:
 */
@Data
public class SpiSnData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ItemNum;

    private String sn;
}
