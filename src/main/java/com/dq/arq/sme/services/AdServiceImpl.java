package com.dq.arq.sme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.AdDao;
import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;

@Transactional
@Service
public class AdServiceImpl implements AdService{

	@Autowired
	AdDao adDao;
	
	@Override
	public Integer saveAdDo(AdDo adDo){
		
		return adDao.saveAdDo(adDo);
	}

	@Override
	public List<AdDo> getAdDosList(){
		
		return adDao.getAdDosList();
	}

	@Override
	public AdDo getAdDoById(Integer id){
		
		return adDao.getAdDoById(id);
	}

	@Override
	public List<AdDo> getAdDosListByAdgroupDo(AdgroupDo adgroupDo){
		
		return adDao.getAdDosListByAdgroupDo(adgroupDo);
	}

	@Override
	public void updateAdDo(AdDo adDo){
		
		adDao.updateAdDo(adDo);
	}

	@Override
	public Long countAdDosByAdgroupDo(AdgroupDo adgroupDo){
		
		return adDao.countAdDosByAdgroupDo(adgroupDo); 
	}
	
	
	@Override
	public void deleteAdDo(AdDo adDo){
		adDao.deleteAdDo(adDo);
	}

	
	@Override
	public AdgroupDo getAdgroupDoByAdId(Integer adId) {
		return adDao.getAdgroupDoByAdId(adId);
	}
	
	
}
