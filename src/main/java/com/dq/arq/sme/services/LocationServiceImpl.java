package com.dq.arq.sme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.LocationDao;
import com.dq.arq.sme.domain.LocationDo;

@Service
@Transactional
public class LocationServiceImpl implements LocationService{

	@Autowired
	LocationDao locationDao;
	
	@Override
	public Integer saveLocationDo(LocationDo locationDo) {
		
		Integer id = locationDao.saveLocationDo(locationDo);
		return id;
	}

	@Override
	public List<LocationDo> getLocationDosList() {
		
		return locationDao.getLocationDosList();
	}

	@Override
	public LocationDo getLocationDoById(Integer id) {
		
		return locationDao.getLocationDoById(id);
	}

	@Override
	public LocationDo getLocationDoByName(String name) {
		return locationDao.getLocationDoByName(name);
	}
	
	@Override
	public List<LocationDo> getLocationsInIndia(){
		return locationDao.getLocationsInIndia();
	}
	
	@Override
	public List<LocationDo> getCountryList(){
		return locationDao.getCountryList();
	}
}
