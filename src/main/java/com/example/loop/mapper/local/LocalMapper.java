package com.example.loop.mapper.local;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocalMapper {
	List<String> getProvince();
	
	List<String> getCityByProvince(String province);
}