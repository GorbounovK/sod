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
import ua.org.gorbounov.sod.models.ImageEntity;
import ua.org.gorbounov.sod.prom.repositories.ImageRepositories;

@Log4j2
@Service
public class ImagesInfoService {
	@Autowired
	ImageRepositories repository;
	
	public List<ImageEntity> getAllImportOrdersInfo(){
//		Pageable first20 = PageRequest.of(0, 10);
		Pageable last10 = PageRequest.of(0, 10, Sort.by("id").descending());
		
		Page<ImageEntity> pagedResult = repository.findAll(last10);
		log.trace("pagedResult.size {}",pagedResult.getSize());
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<ImageEntity>();
		}

	}
}
