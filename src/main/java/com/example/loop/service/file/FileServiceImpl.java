package com.example.loop.service.file;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.loop.mapper.file.FileMapper;

@Service
public class FileServiceImpl implements FileService {
	
	@Value("${file.dir}")
	private String saveFolder;
	
	@Autowired
	private FileMapper fmapper;

	@Override
	public HashMap<String, Object> getTumbnailResource(String systemname) throws Exception {
		//경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
		Path path = Paths.get(saveFolder+systemname);
		//경로에 있는 파일의 MIME 타입을 조사해서 담기
		String contentType = Files.probeContentType(path);
		//해당 경로(path)에 있는 파일로부터 뻗어나오는 InputStream[Files.newInputStream(path)]
		//을 통해 자원화[new InputStreamResource()]
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("contentType", contentType);
		datas.put("resource", resource);
		return datas;
	}

	@Override
	public HashMap<String, Object> downloadFile(String systemname) throws Exception {
		Path path = Paths.get(saveFolder+systemname);
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		String orgname = fmapper.getFileBySystemname(systemname).getOrgName();
		
		HashMap<String, Object> result = new HashMap<>();
		result.put("resource", resource);
		result.put("orgname", orgname);
		
		return result;
	}

}