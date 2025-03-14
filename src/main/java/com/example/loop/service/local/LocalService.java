package com.example.loop.service.local;

import java.util.List;

public interface LocalService {
	List<String> getProvince();

	List<String> getCityByProvince(String province);
}