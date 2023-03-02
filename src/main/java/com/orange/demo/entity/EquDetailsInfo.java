package com.orange.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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

    @TableField("e_type")
    private String eType;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;
}
