package com.example.loop.controller;

import java.net.URLEncoder;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.loop.service.file.FileService;

@RequestMapping("/api/file/*")
@Controller
public class FileController {
	@Autowired
	private FileService service;
	
	@GetMapping("thumbnail/{systemname}")
	public ResponseEntity<Resource> thumbnail(@PathVariable("systemname") String systemname) throws Exception{
		HashMap<String, Object> datas = service.getTumbnailResource(systemname);
		String contentType = (String)datas.get("contentType");
		Resource resource = (Resource)datas.get("resource");
		//응답 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);
		return new ResponseEntity<Resource>(resource,headers,HttpStatus.OK);
	}
	@GetMapping("download/{systemname}")
	public ResponseEntity<Resource> download(@PathVariable("systemname") String systemname) throws Exception{
		HashMap<String, Object> result = service.downloadFile(systemname);
		Resource resource = (Resource)result.get("resource");
		String orgname = (String)result.get("orgname");
		HttpHeaders headers = new HttpHeaders();
		String dwName = "";
		dwName = URLEncoder.encode(orgname,"UTF-8").replaceAll("\\+", "%20");
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(dwName).build());
		return new ResponseEntity<Resource>(resource,headers,HttpStatus.OK);
	}
}