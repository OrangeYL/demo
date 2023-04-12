package com.orange.demo.utils;

import com.alibaba.fastjson.JSONObject;
import com.orange.demo.entity.EquDetailsInfo;
import com.orange.demo.entity.ViInfo;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Li ZhiCheng
 * 2023-03-2023/3/8 15:45
 */
public class JsonUtils {

    public static JSONObject convertToJsonForSpi(List<EquDetailsInfo> list,String eName){
        Map<String, Object> map = generalConvert(eName);
        if(list.size() > 0){
            for(int i = 0;i < list.size();i++){
                EquDetailsInfo info = list.get(i);
                if(i == 0){
                    map.put("machine_type",info.getMachineType());
                    map.put("board_id",info.getBoardId());
                    map.put("pad_no",info.getPadNo());
                    map.put("insp_st_time",info.getInspStTime());
                    map.put("insp_vol_step","insp_vol");
                    map.put("insp_area_step","insp_area");
                    map.put("insp_hei_step","insp_hei");
                    map.put("insp_x_step","insp_x");
                    map.put("insp_y_step","insp_y");
                    map.put("insp_vol_data",info.getInspVol().toString());
                    map.put("insp_area_data",info.getInspArea().toString());
                    map.put("insp_hei_data",info.getInspHei().toString());
                    map.put("insp_x_data",info.getInspX().toString());
                    map.put("insp_y_data",info.getInspY().toString());
                    map.put("sn",info.getMachineType().toString());
                    map.put("item_num",info.getMachineType().toString());
                }
                if(i > 0){
                    map.put("machine_type"+ "_"+ i,info.getMachineType());
                    map.put("board_id"+"_"+ i,info.getBoardId());
                    map.put("pad_no"+"_"+ i,info.getPadNo());
                    map.put("insp_st_time"+"_"+ i,info.getInspStTime());
                    map.put("insp_vol_step"+"_"+ i,"insp_vol_"+ i);
                    map.put("insp_area_step"+"_"+ i,"insp_area_"+i);
                    map.put("insp_hei_step"+"_"+ i,"insp_hei_"+i);
                    map.put("insp_x_step"+"_"+ i,"insp_x_"+i);
                    map.put("insp_y_step"+"_"+ i,"insp_y_"+i);
                    map.put("insp_vol_data"+"_"+ i,info.getInspVol().toString());
                    map.put("insp_area_data"+"_"+ i,info.getInspArea().toString());
                    map.put("insp_hei_data"+"_"+ i,info.getInspHei().toString());
                    map.put("insp_x_data"+"_"+ i,info.getInspX().toString());
                    map.put("insp_y_data"+"_"+ i,info.getInspY().toString());
                }
            }
        }
        return new JSONObject(map);
    }
    public static JSONObject convertToJsonForVi(List<ViInfo> list,String equName){
        Map<String, Object> map = generalConvert(equName);
        if(list.size() > 0){
            for(int i = 0;i < list.size();i++){
                ViInfo info = list.get(i);
                if(i == 0){
                    map.put("dx",info.getDX());
                    map.put("dy",info.getDY());
                    map.put("dTheta",info.getDTheta());
                    map.put("errCode",info.getErrCode());
                }
                if(i > 0){
                    map.put("dx"+ "_"+ i,info.getDY());
                    map.put("dy"+"_"+ i,info.getDY());
                    map.put("dTheta"+"_"+ i,info.getDTheta());
                    map.put("errCode"+"_"+ i,info.getErrCode());
                }
            }
        }
        return new JSONObject(map);
    }

    public static Map<String,Object> generalConvert(String equName){
        Map<String, Object> map = new HashMap<>();
        SnowflakeIdWorker worker = new SnowflakeIdWorker(0L, 0L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        map.put("id", worker.nextId().toString());
        map.put("station",equName);
        map.put("model",equName);
        map.put("start_datetime",format.format(new Date()));
        map.put("year",String.valueOf(calendar.get(Calendar.YEAR)));
        map.put("month",String.valueOf(calendar.get(Calendar.MONTH)+1));
        map.put("day",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        map.put("hour",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        map.put("minute",String.valueOf(calendar.get(Calendar.MINUTE)));
        return map;
    }
}
