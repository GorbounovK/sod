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

import ua.org.gorbounov.sod.models.PromOrdersEntity;
import ua.org.gorbounov.sod.repositories.PromImportOrdersInfoRepozitories;

/**
 * @author gk
 *
 */
@Service
public class ImportOrdersInfoService {
	@Autowired
	PromImportOrdersInfoRepozitories repository;
	
	public List<PromOrdersEntity> getAllImportOrdersInfo(Integer pageNo, Integer pageSize, String sortBy){
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		Page<PromOrdersEntity> pagedResult = repository.findAll(paging);
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<PromOrdersEntity>();
		}
	}
	
	
}
