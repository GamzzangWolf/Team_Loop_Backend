package com.example.loop.service.local;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.loop.mapper.local.LocalMapper;

@Service
public class LocalServiceImpl implements LocalService{
	
	private final LocalMapper localMapper;
	
	public LocalServiceImpl(LocalMapper localMapper) {
		this.localMapper = localMapper;
	}


	@Override
	public List<String> getProvince() {
		// TODO Auto-generated method stub
		
		List<String> result = localMapper.getProvince();
//		System.out.println("호출 확인해보기 : " + result);
		return result;
	}


	@Override
	public List<String> getCityByProvince(String province) {
		// TODO Auto-generated method stub
		List<String> result = localMapper.getCityByProvince(province);
		return result;
	}

}