package com.simplity.server;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service

public interface FileService {
    void saveFile(byte[] file, String filePath, String fileName) throws Exception;

    void download(HttpServletResponse response, String filePath, String filename, Model model);

}
