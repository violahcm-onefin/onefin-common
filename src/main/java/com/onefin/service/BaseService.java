package com.onefin.service;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseService implements IBaseService {

	@Override
	public Map<String, Object> convertObject2Map(Object data) {
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> map = oMapper.convertValue(data, Map.class);
		return map;
	}

}
