package com.simplity.server.controller;

//import ch.qos.logback.core.model.Model;

import com.simplity.server.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class FileController {
    private static final String NULL_FILE = "";
    @Value("${define.nginx.path}")
    private String nginxPath;

    @Autowired
    private FileService fileService;

    /**
     * 前往上传页面
     *
     * @return 页面名称
     */
    @GetMapping({"/upload", ""})
    public String goIndex() {
        return "upload";
    }

    /**
     * 将文件保存到指定文件夹
     *
     * @param file  单个文件
     * @param files 多个文件
     * @return 重定向到controller层中前往下载页面的url
     * @throws IOException
     */
    @PostMapping("/upload")
    @ResponseBody
    public String uploadAndGoDownLoad(@RequestPart("file") MultipartFile file,
                                      @RequestPart("files") List<MultipartFile> files) throws IOException {
        try {
            if (file == null || NULL_FILE.equals(file.getOriginalFilename())) {
                return "upload failure1";
            }
            fileService.saveFile(file.getBytes(), nginxPath, file.getOriginalFilename());
        } catch (Exception e) {
            return "upload failure2";
        }

        if (files.size() > 0) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {
                    try {
                        fileService.saveFile(multipartFile.getBytes(), nginxPath, multipartFile.getOriginalFilename());
                    } catch (Exception e) {
                        return "upload failure3";
                    }
                }
            }
        }
        return "redirect:/goDownload";

    }
    @GetMapping("/goDownload")
    @ResponseBody
    public String goDownload(Model model) {
        File file = new File(nginxPath);
        //判断文件夹是否存在
        if (file.exists()) {
            //获取文件夹下面的所有名称
            String[] list = file.list();
            model.addAttribute("fileNames", list);
        }
        return "download";
    }
}
