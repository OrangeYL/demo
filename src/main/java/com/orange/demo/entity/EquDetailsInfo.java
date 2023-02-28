package com.orange.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/28 14:45
 * @description: 设备详细信息
 */
@Data
@TableName("equ_details_info")
public class EquDetailsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private String eId;

    private String boardId;

    private String padNo;

    private String inspStTime;

    private Double inspVol;

    private Double inspArea;

    private Double inspHei;

    private Double inspX;

    private Double inspY;
}
