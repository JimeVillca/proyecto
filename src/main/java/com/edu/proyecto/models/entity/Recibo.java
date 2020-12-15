package com.edu.proyecto.models.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.opencsv.bean.CsvBindByName;

@Entity
@Table(name = "recibos")
/*ESTADOS 
 * SIN FIRMA  	 - 1
 * FIRMA ADMIN 	 - 2
 * FIRMA USUARIO - 3*/

public class Recibo implements Serializable{
	
	private static final long serialVersionUID = 7536804169972451999L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true)
	private Long id;
	
	@CsvBindByName(column = "idtrax")
	private Long idtrax;	
	
	private int estado = 1;
	
	@CsvBindByName(column = "idusuario")
	private Long idusuario;
	
	@CsvBindByName(column = "periodo")
	private String periodo;
	
	@CsvBindByName(column = "idconceptouno")
	private int idconceptouno;
	
	@CsvBindByName(column = "conceptouno")
	private String conceptouno;
	
	@CsvBindByName(column = "importeuno")
	private int importeuno;
	
	@CsvBindByName(column = "idconceptodos")
	private int idconceptodos;
	
	@CsvBindByName(column = "conceptodos")
	private String conceptodos;
	
	@CsvBindByName(column = "importedos")
	private int importedos;

	@CsvBindByName(column = "idconceptotres")
	private int idconceptotres;
	
	@CsvBindByName(column = "conceptotres")
	private String conceptotres;
	
	@CsvBindByName(column = "importetres")
	private int importetres;
	
	@CsvBindByName(column = "importetotal")
	private int importetotal;
	
	
	private Integer idfirmausuario;
	
	
	private Integer idfirmadmin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(Long idusuario) {
		this.idusuario = idusuario;
	}

	public Long getIdtrax() {
		return idtrax;
	}

	public void setIdtrax(Long idtrax) {
		this.idtrax = idtrax;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public int getIdconceptouno() {
		return idconceptouno;
	}

	public void setIdconceptouno(int idconceptouno) {
		this.idconceptouno = idconceptouno;
	}

	public int getImporteuno() {
		return importeuno;
	}

	public void setImporteuno(int importeuno) {
		this.importeuno = importeuno;
	}

	public int getIdconceptodos() {
		return idconceptodos;
	}

	public void setIdconceptodos(int idconceptodos) {
		this.idconceptodos = idconceptodos;
	}

	public int getImportedos() {
		return importedos;
	}

	public void setImportedos(int importedos) {
		this.importedos = importedos;
	}

	public int getIdconceptotres() {
		return idconceptotres;
	}

	public void setIdconceptotres(int idconceptotres) {
		this.idconceptotres = idconceptotres;
	}

	public int getImportetres() {
		return importetres;
	}

	public void setImportetres(int importetres) {
		this.importetres = importetres;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getImportetotal() {
		return importetotal;
	}

	public void setImportetotal(int importetotal) {
		this.importetotal = importetotal;
	}

	public Integer getIdfirmausuario() {
		return idfirmausuario;
	}

	public void setIdfirmausuario(Integer idfirmausuario) {
		this.idfirmausuario = idfirmausuario;
	}

	public Integer getIdfirmadmin() {
		return idfirmadmin;
	}

	public void setIdfirmadmin(Integer idfirmadmin) {
		this.idfirmadmin = idfirmadmin;
	}

	public String getConceptouno() {
		return conceptouno;
	}

	public void setConceptouno(String conceptouno) {
		this.conceptouno = conceptouno;
	}

	public String getConceptodos() {
		return conceptodos;
	}

	public void setConceptodos(String conceptodos) {
		this.conceptodos = conceptodos;
	}

	public String getConceptotres() {
		return conceptotres;
	}

	public void setConceptotres(String conceptotres) {
		this.conceptotres = conceptotres;
	}

		

}
