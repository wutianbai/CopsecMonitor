package com.copsec.monitor.web.utils;

import com.copsec.monitor.web.beans.LogConditionBean;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);

    public static OutputStream makeExcel(List<LogConditionBean> list, String fileName, HttpServletResponse response) {

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
        } catch (UnsupportedEncodingException e1) {

            e1.printStackTrace();
        }


        OutputStream output = null;
        try {
            output = new FileOutputStream(new File(fileName));
        } catch (IOException e1) {

            e1.printStackTrace();
        }
        BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);

        // 定义单元格报头
        String worksheetTitle = "Excel导出日志信息";

        HSSFWorkbook wb = new HSSFWorkbook();

        // 创建单元格样式
        HSSFCellStyle cellStyleTitle = wb.createCellStyle();
        // 指定单元格居中对齐
        cellStyleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 指定单元格垂直居中对齐
        cellStyleTitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 指定当单元格内容显示不下时自动换行
        cellStyleTitle.setWrapText(true);
        // ------------------------------------------------------------------
        HSSFCellStyle cellStyle = wb.createCellStyle();
        // 指定单元格居中对齐
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 指定单元格垂直居中对齐
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 指定当单元格内容显示不下时自动换行
        cellStyle.setWrapText(true);
        // ------------------------------------------------------------------
        // 设置单元格字体
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 200);
        cellStyleTitle.setFont(font);

        // 工作表名
        String index = "序号";
        String user = "操作用户";
        String ip = "IP地址";
        String type = "操作类型";
        String desc = "描述";
        String result = "结果";
        String time = "时间";

        HSSFSheet sheet = wb.createSheet();
        ExportExcel exportExcel = new ExportExcel(wb, sheet);
        // 创建报表头部
        exportExcel.createNormalHead(worksheetTitle, 6);
        // 定义第一行
        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = row1.createCell(0);

        //第一行第一列

        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(index));
        //第一行第er列
        cell1 = row1.createCell(1);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(user));

        //第一行第san列
        cell1 = row1.createCell(2);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(ip));

        //第一行第si列
        cell1 = row1.createCell(3);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(type));

        //第一行第wu列
        cell1 = row1.createCell(4);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(desc));

        //第一行第liu列
        cell1 = row1.createCell(5);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(result));

        //第一行第qi列
        cell1 = row1.createCell(6);
        cell1.setCellStyle(cellStyleTitle);
        cell1.setCellValue(new HSSFRichTextString(time));


        //定义第二行
        HSSFRow row = sheet.createRow(2);
        HSSFCell cell = row.createCell(1);
        LogConditionBean log = new LogConditionBean();
        for (int i = 0; i < list.size(); i++) {
            log = list.get(i);
            row = sheet.createRow(i + 2);

            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString((i + 1) + ""));

            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(log.getOperateUser()));

            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(log.getIp()));

            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(log.getOperateType()));

            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(log.getDesc()));

            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(String.valueOf(log.getResult())));

            cell = row.createCell(6);
            cell.setCellValue(new HSSFRichTextString(log.getDate()));
            cell.setCellStyle(cellStyle);

        }
        try {
            bufferedOutPut.flush();
            wb.write(bufferedOutPut);
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedOutPut;
    }
}
