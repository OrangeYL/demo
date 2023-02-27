package com.orange.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/22 10:06
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    //姓名
    private String name;
    //成绩
    private Integer grade;
    //记录时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recodeTime;
}
