package com.orange.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
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

    @TableField("e_id")
    private String eId;

    @TableField("machine_type")
    private String machineType;

    @TableField("board_id")
    private String boardId;

    @TableField("pad_no")
    private String padNo;

    @TableField("insp_st_time")
    private String inspStTime;

    @TableField("insp_vol")
    private Double inspVol;

    @TableField("insp_area")
    private Double inspArea;

    @TableField("insp_hei")
    private Double inspHei;

    @TableField("insp_x")
    private Double inspX;

    @TableField("insp_y")
    private Double inspY;

}
