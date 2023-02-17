package ua.org.gorbounov.sod.prom.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ProductSearch {
	private String barcode;
}
