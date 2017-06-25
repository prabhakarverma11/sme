package com.dq.arq.sme.services;

import java.util.List;

import com.dq.arq.sme.domain.LocationDo;

public interface LocationService {

	Integer saveLocationDo(LocationDo locationDo);

	List<LocationDo> getLocationDosList();

	LocationDo getLocationDoById(Integer id);
	
	LocationDo getLocationDoByName(String name);

	List<LocationDo> getLocationsInIndia();
	
	List<LocationDo> getCountryList();
}
