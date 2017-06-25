package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;

public interface AdDao {


	Integer saveAdDo(AdDo adDo);
	
	List<AdDo> getAdDosList();

	AdDo getAdDoById(Integer id);

	List<AdDo> getAdDosListByAdgroupDo(AdgroupDo adgroupDo);

	void updateAdDo(AdDo adDo);

	Long countAdDosByAdgroupDo(AdgroupDo adgroupDo);
	
	void deleteAdDo(AdDo adDo);

	AdgroupDo getAdgroupDoByAdId(Integer adId);
	
}
