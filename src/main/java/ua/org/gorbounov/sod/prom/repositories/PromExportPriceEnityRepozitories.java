package ua.org.gorbounov.sod.prom.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.prom.models.PromExportPriceEntity;

@Repository
public interface PromExportPriceEnityRepozitories extends PagingAndSortingRepository<PromExportPriceEntity, Long>{

}
