package ua.org.gorbounov.repositories;

import org.springframework.data.repository.CrudRepository;

import ua.org.gorbounov.models.Image;

public interface ImageRepositories extends CrudRepository<Image, Long>{

}
