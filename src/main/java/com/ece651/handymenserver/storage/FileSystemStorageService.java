package com.ece651.handymenserver.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

@Service
public class FileSystemStorageService implements StorageService {
	private Path rootLocation = null;
	private StorageProperties properties;
	
	@Autowired
	FileSystemStorageService(StorageProperties properties) {
		System.out.println("FileSystemStorageService construct");
		this.properties = properties;
	}
	
    public void initPath(ServletContext servletContext) {
    	System.out.println("FileSystemStorageService initPath servletContext:" + servletContext);
    	
    	rootLocation = Paths.get(servletContext.getRealPath(properties.getLocation()));
    	System.out.println("rootLocation:" + rootLocation);

        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            e.printStackTrace();
        }    	
    	
    }
    
    public void store(MultipartFile file) throws Exception {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new Exception("Failed to store empty file " + filename);
        }
        if (filename.contains("..")) {
            // This is a security check
            throw new Exception(
                    "Cannot store file with relative path outside current directory "
                            + filename);
        }
        Files.copy(file.getInputStream(), this.rootLocation.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
    }
    

	
	
	
	
	
	
}
