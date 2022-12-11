package com.simplity.server;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.*;
import java.net.URLEncoder;



@Service
public class FileServiceImpl implements FileService {

    @Override
    public void saveFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    @Override
    public void download(HttpServletResponse response, String filePath, String filename, Model model) {
        response.setContentType("application/force-download");
        response.setCharacterEncoding("UTF-8");
        //设置下载后的文件名和header
        response.addHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(filename));
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;

        try {
            outputStream = response.getOutputStream();

            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(new File(filePath + filename)));
            int read = bis.read(buff);


            //通过while 循环写入到指定的文件夹
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常返回给页面失败的信息
            System.out.println("--------------------------------");
            model.addAttribute("result", "下载失败");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void complie() {

    }
}
