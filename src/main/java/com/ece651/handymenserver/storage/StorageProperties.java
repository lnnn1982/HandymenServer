package com.ece651.handymenserver.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "uploadFile";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }	
	
	
	
}
