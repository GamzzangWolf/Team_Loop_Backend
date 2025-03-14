package com.example.loop.service.file;

import java.util.HashMap;

import org.springframework.core.io.Resource;

public interface FileService {
	HashMap<String, Object> getTumbnailResource(String systemname) throws Exception;
	HashMap<String, Object> downloadFile(String systemname) throws Exception;
}