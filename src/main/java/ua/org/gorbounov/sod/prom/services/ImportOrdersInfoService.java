/**
 * 
 */
package ua.org.gorbounov.sod.prom.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.repositories.PromOrdersEntityRepozitories;

/**
 * @author gk
 *
 */
@Log4j2
@Service
public class ImportOrdersInfoService {
	@Autowired
	PromOrdersEntityRepozitories repository;
	
	
	
	public List<PromOrdersEntity> getAllImportOrdersInfo(Integer pageNo, Integer pageSize, String sortBy){
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Page<PromOrdersEntity> pagedResult = repository.findAll(paging);
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<PromOrdersEntity>();
		}
		
	}
	public List<PromOrdersEntity> getAllImportOrdersInfo(){
		Pageable first20 = PageRequest.of(0, 10);
		Pageable last10 = PageRequest.of(0, 10, Sort.by("id").descending());
		
		Page<PromOrdersEntity> pagedResult = repository.findAll(last10);
		log.trace("pagedResult.size {}",pagedResult.getSize());
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<PromOrdersEntity>();
		}

	}

}
