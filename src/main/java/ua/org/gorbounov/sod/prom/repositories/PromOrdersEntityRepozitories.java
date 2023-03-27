package ua.org.gorbounov.sod.prom.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;

@Repository
public interface PromOrdersEntityRepozitories extends PagingAndSortingRepository<PromOrdersEntity, Long>{
//    List<PromOrdersEntity> findAll(Pageable pageable);
}
