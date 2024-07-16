package com.szy.reggie.controller;

import com.szy.reggie.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${pic.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        // 传入的变量名（file）必须与前端发来的一致
        // file是临时文件，需要转存到指定位置
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // uuid重新生成文件名，防止覆盖文件
        String filename = UUID.randomUUID().toString() + suffix;
        file.transferTo(new File(basePath + filename));
        return R.success(filename);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            // 通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 通过输出流把文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];  // 1024bytes, 1kb
            while( (len = fileInputStream.read(bytes)) != -1) {  // 读取不会超过bytes数组长度
                outputStream.write(bytes, 0, len);  // 写入到输出流len长度的数据
                outputStream.flush(); // 刷新
            }
            // 关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
