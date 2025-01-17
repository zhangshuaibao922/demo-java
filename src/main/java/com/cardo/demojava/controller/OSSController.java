package com.cardo.demojava.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.cardo.demojava.dto.UserDto;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Role;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.RoleMapper;
import com.cardo.demojava.mapper.UserMapper;
import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Api(tags = "存储功能接口")
@RestController
@RequestMapping("/oss")
public class OSSController {
    @Value("${ENDPOINT}")
    private String ENDPOINT;
    @Value("${ACCESS_KEY_ID}")
    private String ACCESS_KEY_ID;
    @Value("${ACCESS_KEY_SECRET}")
    private String ACCESS_KEY_SECRET;
    @Value("${BUCKET_NAME}")
    private String BUCKET_NAME;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private FieldMapper fieldMapper;

    @GetMapping("/download-excel")
    public Response<String> generateAndUploadExcel() throws IOException {
        List<User> users = userMapper.selectList(null);
        List<Field> fields = fieldMapper.selectList(null);
        List<Role> roles = roleMapper.selectList(null);
        ArrayList<UserDto> userDtos = new ArrayList<>();
         for (User record : users) {
             UserDto userDto = new UserDto();
             BeanUtils.copyProperties(record, userDto);
             for (Field field : fields) {
                 if (Objects.equals(field.getFieldId(), record.getFieldId())) {
                     userDto.setFieldName(field.getFieldName());
                 }
             }
             for (Role role : roles) {
                 if (role.getRoleId().equals(record.getRoleId())) {
                     userDto.setRoleName(role.getRoleName());
                 }
             }
             userDtos.add(userDto);
         }
        // 生成 Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户信息"); // 创建一个名为“用户信息”的工作表
        // 填充标题行
        String[] headers = {"账号", "密码", "姓名","领域","权限","年限","评分"};
        Row headerRow = sheet.createRow(0); // 创建第一行（通常为标题行）
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        // 填充数据
        int rowNum = 1;
        for (UserDto user : userDtos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getAccount());
            row.createCell(1).setCellValue(user.getPassword());
            row.createCell(2).setCellValue(user.getName());
            row.createCell(3).setCellValue(user.getFieldName());
            row.createCell(4).setCellValue(user.getRoleName());
            row.createCell(5).setCellValue(user.getOld());
            row.createCell(6).setCellValue(user.getScore());
        }
        // 写入到输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
        // 上传到 OSS
        String objectName = "users.xlsx";
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        try {
            ossClient.putObject(BUCKET_NAME, objectName, new ByteArrayInputStream(outputStream.toByteArray()));
            Date expiration = new Date(new Date().getTime() + 3600 * 1000); // 1小时后过期
            URL url = ossClient.generatePresignedUrl(BUCKET_NAME, objectName, expiration);
            return Response.ok("https://"+BUCKET_NAME+"."+ENDPOINT+"/"+objectName);
        } finally {
            ossClient.shutdown();
        }
    }
}
