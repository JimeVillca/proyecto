package com.edu.proyecto.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.edu.proyecto.models.entity.Categoria;

public interface ICategoriaService {

	public List<Categoria> findAllCat();
	
	public Page<Categoria> findAllCat(Pageable pageable);

	public void save(Categoria categoria);
	
	public Categoria findOneCat(Long idcategoria);
	
	public void delete(Long idcategoria);
}
