package com.orange.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/24 11:04
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equitment {

    private String eName;

    private String eType;

    private List<Student> list = new ArrayList<>();
}
