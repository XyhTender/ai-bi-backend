package com.yu.bi.utils;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * Excel 转 CSV
     */
    public static String excelToCsv(MultipartFile multipartFile)  {

//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())//输入流
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误",e);
            e.printStackTrace();
        }

        if (CollUtil.isEmpty(list)) {
            return "";
        }
        //转换为CSV
        StringBuilder stringBuilder = new StringBuilder(); //线程不安全但是性能高

        //读取表头
        LinkedHashMap<Integer,String> headerMap = (LinkedHashMap<Integer, String>) list.get(0); //LinkedHashMap读取数据是线性的，连续的
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()); //过滤为null的数据 过滤后存入list中
        stringBuilder.append(StringUtils.join(headerList,",")).append("\n"); // 拼接数组

        for (int i = 1; i < list.size(); i ++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()); //过滤为null的数据 过滤后存入list中
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n"); // 拼接数组
        }

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        excelToCsv(null);
    }
}
