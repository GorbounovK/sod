package ua.org.gorbounov.sod.rozetka.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.rozetka.models.RozetkaExportPriceEntity;

@Repository
public interface RozetkaExportPriceEnityRepozitories extends PagingAndSortingRepository<RozetkaExportPriceEntity, Long>{

}
