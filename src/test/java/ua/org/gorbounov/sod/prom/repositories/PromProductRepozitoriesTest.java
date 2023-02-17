package ua.org.gorbounov.sod.prom.repositories;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import ua.org.gorbounov.sod.prom.models.PromProduct;

@Log4j2
public class PromProductRepozitoriesTest {

	@Autowired
	private PromProductRepozitories promProductRepozitories;

//	@Test
	@Transactional
	public void testBarcode() {
		Pageable last10 = PageRequest.of(0, 10, Sort.by("id").descending());

		List<PromProduct> list = promProductRepozitories.findByBarcodeContaining("8033210292639", last10).getContent();
		list.forEach(e -> log.trace(e.getBarcode()));
	}
}
