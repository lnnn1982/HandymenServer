package com.ece651.handymenserver.storage;

import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void initPath(ServletContext servletContext);
    void store(MultipartFile file) throws Exception;
}
