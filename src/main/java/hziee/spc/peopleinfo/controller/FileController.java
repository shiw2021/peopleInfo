package hziee.spc.peopleinfo.controller;

import hziee.spc.peopleinfo.entitey.People;
import hziee.spc.peopleinfo.mapper.PeopleMapper;
import hziee.spc.peopleinfo.util.ExcelEdit;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;


import org.springframework.http.ResponseEntity;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static hziee.spc.peopleinfo.util.ExcelEdit.getStringCellValue;

@Controller
public class FileController {
    private final PeopleMapper peopleMapper;
    private final ExcelEdit excelEdit;

    public FileController(PeopleMapper peopleMapper, ExcelEdit excelEdit) {
        this.peopleMapper = peopleMapper;
        this.excelEdit = excelEdit;
    }

    @GetMapping("/template")//下载模板
    public void downloadTemplate(HttpServletResponse response) {
        File file = new File("src/main/resources/static/template.xlsx");
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=template.xlsx");


        try (FileInputStream inputStream = new FileInputStream(file);) {
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/importExcel")//导入excel
    public ResponseEntity<Object> handleFileUpload(@RequestParam("excelFile") MultipartFile file) throws Exception {

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!suffix.equals("xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件格式错误");
        }
        //get the fileinputstream
        ExcelEdit excelEdit = new ExcelEdit();
        int badData = excelEdit.importData(file, peopleMapper);

        if (badData == 0)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("有" + badData + "条数据插入失败");
    }


    @GetMapping("/exportExcel")//导出数据至表格
    public void exportExcel(HttpServletResponse response) {
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=exportInfo.xlsx");

        Iterable<People> peopleList = peopleMapper.selectAll();
        Workbook workbook = excelEdit.writeFileByDate(peopleList);
        try {
            OutputStream out = response.getOutputStream();
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
