package com.orange.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Li ZhiCheng
 * @create: 2023-04-2023/4/17 11:35
 * @description:
 */
@Data
public class SpiVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String padNo;

    private String arrayId;
}
