package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.LocationDo;

public interface LocationDao {

	Integer saveLocationDo(LocationDo locationDo);

	List<LocationDo> getLocationDosList();

	LocationDo getLocationDoById(Integer locationId);
	
	LocationDo getLocationDoByName(String name);
	
	List<LocationDo> getLocationsInIndia();
	
	List<LocationDo> getCountryList();
	
}
