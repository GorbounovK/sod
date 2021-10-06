package ua.org.gorbounov.sod.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.models.PromExportPriceEntity;
import ua.org.gorbounov.sod.models.PromOrdersEntity;

@Repository
public interface PromExportPriceEnityRepozitories extends PagingAndSortingRepository<PromExportPriceEntity, Long>{

}
