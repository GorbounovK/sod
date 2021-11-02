package ua.org.gorbounov.sod.prom.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;

@Repository
public interface PromOrdersEntityRepozitories extends PagingAndSortingRepository<PromOrdersEntity, Long>{
//    List<PromOrdersEntity> findAll(Pageable pageable);
}
