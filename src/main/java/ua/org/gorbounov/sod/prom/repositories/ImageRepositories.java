package ua.org.gorbounov.sod.prom.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ua.org.gorbounov.sod.models.ImageEntity;

public interface ImageRepositories extends PagingAndSortingRepository<ImageEntity, Long>{

}
