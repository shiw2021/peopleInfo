package hziee.spc.peopleinfo.util;


import hziee.spc.peopleinfo.entitey.People;
import hziee.spc.peopleinfo.mapper.PeopleMapper;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@NoArgsConstructor(force = true)
public class ExcelEdit {


    public static void main(String[] args) {
        ExcelEdit excelEdit = new ExcelEdit();
        excelEdit.eazyShow();


    }

    public Workbook writeFileByDate(Iterable<People> peopleList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        //This data needs to be written (Object[])
        //读取内容，创建对象
                /*
                public class People {

                    String id;
                    String name;
                    private String namePinYin;
                    String gender;
                    String idType;
                    String IdNumber;
                    LocalDate birthday;
                    String phone;
                    String email;
                    LocalDateTime createtime;
                    LocalDateTime updatetime;
                }
                表格格式：
                姓名	姓名全拼	性别	身份证类型（居民身份证、士官证、学生证、驾驶证、护照、港澳通行证）

                身份证号码	出生日期	手机号码	电子邮箱	创建时间	修改时间
                 */
        Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
        data.put(1, new Object[]{"姓名", "姓名全拼", "性别", "身份证类型（居民身份证、士官证、学生证、驾驶证、护照、港澳通行证）", "身份证号码", "出生日期", "手机号码", "电子邮箱", "创建时间", "修改时间"});

        int i = 2;
        for (People people : peopleList) {
            data.put(i, new Object[]{people.getName(), people.getNamePinYin(), people.getGender(), people.getIdType(), people.getIdNumber(), people.getBirthday(), people.getPhone(), people.getEmail(), people.getCreatetime(), people.getUpdatetime()});
            i++;
        }
        Set<Integer> keyset = data.keySet();
        System.out.println(keyset);
        int rownum = 0;
        for (Integer key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
                else if (obj instanceof LocalDate){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 定义日期格式
                    String formattedDate = ((LocalDate) obj).format(formatter); // 将 LocalDate 格式化为字符串
                    cell.setCellValue(formattedDate);
                }
                else if (obj instanceof LocalDateTime){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 定义日期格式
                    String formattedDate = ((LocalDateTime) obj).format(formatter); // 将 LocalDate 格式化为字符串
                    cell.setCellValue(formattedDate);
                }

            }
        }
        return workbook;
    }

    public int importData(MultipartFile file, PeopleMapper peopleMapper) throws Exception {
        InputStream is = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        //计算行数
        int rowNum = sheet.getLastRowNum();
        //日志
        System.out.println("总行数:" + rowNum);
        //从第二行开始读取数据，第一行是表头
        int badData = 0;
        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            //读取内容，创建对象
                /*
                public class People {

                    String id;
                    String name;
                    private String namePinYin;
                    String gender;
                    String idType;
                    String IdNumber;
                    LocalDate birthday;
                    String phone;
                    String email;
                    LocalDateTime createtime;
                    LocalDateTime updatetime;
                }
                表格格式：
                姓名	姓名全拼	性别	身份证类型（居民身份证、士官证、学生证、驾驶证、护照、港澳通行证）

                身份证号码	出生日期	手机号码	电子邮箱	创建时间	修改时间
                 */
            People people = new People();
            people.setName(getStringCellValue(row.getCell(0)));
            people.setNamePinYin(getStringCellValue(row.getCell(1)));
            people.setGender(getStringCellValue(row.getCell(2)));
            people.setIdType(getStringCellValue(row.getCell(3)));
            people.setIdNumber(getStringCellValue(row.getCell(4)));
            Cell cell = row.getCell(5);
            people.setBirthday((row.getCell(5).getDateCellValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            people.setPhone(row.getCell(6).getStringCellValue());
            people.setEmail(getStringCellValue(row.getCell(7)));
            people.setCreatetime(LocalDateTime.now());
            people.setUpdatetime(LocalDateTime.now());
            people.setId(UUID.randomUUID().toString().replace("-", ""));
            //插入数据库
            try {
                peopleMapper.save(people);
            } catch (DuplicateKeyException e) {
                System.out.println("插入失败，有重复数据:" + e.getCause().getMessage());
                badData += 1;
            }
        }

        return badData;
    }

    public static String getStringCellValue(Cell cell) {
        String cellValue;
        switch (cell.getCellType()) {
            case STRING -> cellValue = cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue().toString();
                } else {
                    cellValue = Double.toString(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> cellValue = Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> cellValue = cell.getCellFormula();
            case BLANK -> cellValue = "";
            default -> cellValue = "";
        }
        return cellValue;
    }

    private void eazyShow() {

        String filePath = "src/main/resources/static/template.xlsx";
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个Sheet
            // 遍历每一行
            for (Row row : sheet) {
                // 遍历每一列
                for (Cell cell : row) {
                    // 获取单元格的值
                    String cellValue = getStringCellValue(cell);
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        String filePath = "src/main/resources/static/template.xlsx";

        try (
                Workbook workbook = WorkbookFactory.create(new FileInputStream(filePath))) {
            int numSheets = workbook.getNumberOfSheets();

            System.out.println("Excel File Structure:");

            for (int i = 0; i < numSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                System.out.println("\nSheet: " + sheetName);

                int lastRowNum = sheet.getLastRowNum();

                for (int j = 0; j <= lastRowNum; j++) {
                    Row row = sheet.getRow(j);

                    if (row != null) {
                        short lastCellNum = row.getLastCellNum();

                        for (int k = 0; k < lastCellNum; k++) {
                            Cell cell = row.getCell(k);

                            if (cell != null) {
                                CellType cellType = cell.getCellType();
                                String cellValue = "";

                                switch (cellType) {
                                    case STRING:
                                        cellValue = cell.getStringCellValue();
                                        break;
                                    case NUMERIC:
                                        cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        cellValue = cell.getCellFormula();
                                        break;
                                    default:
                                        break;
                                }

                                System.out.println("Row: " + (j + 1) + ", Column: " + (k + 1) + ", Value: " + cellValue);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
