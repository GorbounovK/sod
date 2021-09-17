package ua.org.gorbounov.sod.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.models.PromOrdersEntity;

@Repository
public interface PromImportOrdersInfoRepozitories extends PagingAndSortingRepository<PromOrdersEntity, Long>{

}
