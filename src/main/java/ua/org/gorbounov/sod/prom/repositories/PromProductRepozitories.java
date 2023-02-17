package ua.org.gorbounov.sod.prom.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ua.org.gorbounov.sod.prom.models.PromExportPriceEntity;
import ua.org.gorbounov.sod.prom.models.PromOrdersEntity;
import ua.org.gorbounov.sod.prom.models.PromProduct;

@Repository
public interface PromProductRepozitories extends PagingAndSortingRepository<PromProduct, Long>{

		Page<PromProduct> findByBarcodeContaining(String barcode, Pageable pageable);
		
		long countByBarcode(String barcode);
		
		List<PromProduct> findByPromId(String promId);
}
