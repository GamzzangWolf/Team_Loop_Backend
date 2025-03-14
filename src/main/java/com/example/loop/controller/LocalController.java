package com.example.loop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loop.service.local.LocalService;

@RestController
@RequestMapping("/api/local")
public class LocalController {
	private final LocalService localService;

	public LocalController(LocalService localService) {
		this.localService = localService;
	}
	
	
	@GetMapping("/province")
	public List<String> getMethodName() {
		return localService.getProvince();
	}
	
	@PostMapping("/getCity")
	public List<String> getCityByProvince(@RequestBody Map<String, String> getData){
		String province = getData.get("province");
		return localService.getCityByProvince(province);
	}
	
	
}