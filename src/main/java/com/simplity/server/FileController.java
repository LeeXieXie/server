package com.simplity.server;

//import ch.qos.logback.core.model.Model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@Api(tags = {"文件上传下载编译"})
@Controller
@RequestMapping("/upload")
public class FileController {
    private static final String NULL_FILE = "";
    @Value("${define.nginx.path}")
    private String nginxPath;

    @Autowired
    private FileService fileService;

    @GetMapping(value = "/goToUpload")
    public String goToUploadHtml() {
        return "upload.html";
    }

    @ApiOperation("单文件上传")
    @PostMapping("/uploadFile")
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || NULL_FILE.equals(file.getOriginalFilename())) {
                return "upload failure";
            }
            fileService.saveFile(file.getBytes(), nginxPath, file.getOriginalFilename());
        } catch (Exception e) {
            return "upload failure";
        }
        return "upload success";
    }

    /**
     * 注意：我们需要请求头信息是：contentType:multipart/form-data
     *      但是Swagger只能提供 contentType:application/json
     * 所以 Swagger 无法测试该方法
     * @param files
     * @return
     * @throws Exception
     */
    @ApiOperation("批量文件上传")
    @PostMapping("/uploadFiles")
    @ResponseBody
    public String multiFileUpload(@RequestParam("file") MultipartFile[] files) {
        try {

            for (int i = 0; i < files.length; i++) {
                //check file
                if (NULL_FILE.equals(files[i].getOriginalFilename())) {
                    continue;
                }
                fileService.saveFile(files[i].getBytes(), nginxPath, files[i].getOriginalFilename());
            }
        } catch (Exception e) {
            return "upload failure";
        }
        return "upload success";
    }

    @ApiOperation("文件下传")
    @PostMapping("/download")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件名",name = "fileName",dataType = "string")
    })
    public void  testDownload(HttpServletResponse response, String filePath, String fileName, Model model) {
        fileService.download(response, filePath, fileName, model);
        //成功后返回成功信息
        model.addAttribute("result","下载成功");
    }
}
