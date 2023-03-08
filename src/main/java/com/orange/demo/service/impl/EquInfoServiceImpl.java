package com.orange.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.EquInfo;
import com.orange.demo.mapper.EquDetailsInfoMapper;
import com.orange.demo.mapper.EquInfoMapper;
import com.orange.demo.service.EquInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: Li ZhiCheng
 * @create: 2023-02-2023/2/28 15:26
 * @description:
 */
@Service
public class EquInfoServiceImpl extends ServiceImpl<EquInfoMapper, EquInfo> implements EquInfoService {

    @Autowired
    private EquDetailsInfoMapper equDetailsInfoMapper;

    @Override
    public void saveEntity(EquInfo equInfo,String eName) {
        LambdaQueryWrapper<EquInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquInfo::getEName, eName);
        EquInfo equInfo1 = this.getOne(wrapper);
        List<EquDetailsInfo> infos = equInfo.getList();
        if (equInfo1 == null) {
           this.save(equInfo);
            if (infos.size() > 0) {
                for (EquDetailsInfo equDetailsInfo : infos) {
                    equDetailsInfo.setEId(equInfo.getId());
                    equDetailsInfo.setCreateTime(new Date());
                    equDetailsInfoMapper.insert(equDetailsInfo);
                }
            }
        } else {
            if (infos.size() > 0) {
                for (EquDetailsInfo equDetailsInfo : infos) {
                    equDetailsInfo.setEId(equInfo1.getId());
                    equDetailsInfo.setCreateTime(new Date());
                    equDetailsInfoMapper.insert(equDetailsInfo);
                }
            }
        }

    }
}
