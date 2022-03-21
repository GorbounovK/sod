package ua.org.gorbounov.sod.models;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
@ToString
@Component
@ApplicationScope
public class ApplicationProperties {
	@Value("${emag.enabled:false}")
	private boolean emagEnabled;

	@Value("${prom.ua.enabled:false}")
	private boolean promEnabled;

	@Value("${rozetka.enabled:false}")
	private boolean rozetkaEnabled;

	@Value("${prom.ua.price.path:''}")
	private String promExportPricePath;

	@Value("${rozetka.ua.price.path:''}")
	private String rozetkaExportPricePath;

	@PostConstruct
	public void init() {
		log.info(toString());
	}
}
