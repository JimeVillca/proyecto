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



@Entity
@Table(name = "recibos")
public class ReciboC  implements Serializable {
	

	private static final long serialVersionUID = 7536804169972451999L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true)
	private Long id;
	
	private Long idtrax;	
	
	private int estado;
	
	
	private String periodo;
	
	private int idconceptouno;
	
	private String conceptouno;
	
	private Long importeuno;
	
	private int idconceptodos;
	
	private String conceptodos;
	
	private Long importedos;

	private int idconceptotres;
	
	private String conceptotres;
	
	private Long importetres;
	
	private Long importetotal;
	
	private Long idfirmausuario;
	
	private Long idfirmadmin;
	
	private Long conformidad;
	
	@ManyToOne 
	@JoinColumn(name="idusuario")
	public Usuario usuario;
	
	@ManyToOne 
	@JoinColumn(name="idempresa")
	public Empresa empresa;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdtrax() {
		return idtrax;
	}

	public void setIdtrax(Long idtrax) {
		this.idtrax = idtrax;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
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

	public Long getImporteuno() {
		return importeuno;
	}

	public void setImporteuno(Long importeuno) {
		this.importeuno = importeuno;
	}

	public int getIdconceptodos() {
		return idconceptodos;
	}

	public void setIdconceptodos(int idconceptodos) {
		this.idconceptodos = idconceptodos;
	}

	public Long getImportedos() {
		return importedos;
	}

	public void setImportedos(Long importedos) {
		this.importedos = importedos;
	}

	public int getIdconceptotres() {
		return idconceptotres;
	}

	public void setIdconceptotres(int idconceptotres) {
		this.idconceptotres = idconceptotres;
	}

	public Long getImportetres() {
		return importetres;
	}

	public void setImportetres(Long importetres) {
		this.importetres = importetres;
	}

	public Long getImportetotal() {
		return importetotal;
	}

	public void setImportetotal(Long importetotal) {
		this.importetotal = importetotal;
	}

	
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public Long getIdfirmausuario() {
		return idfirmausuario;
	}

	public void setIdfirmausuario(Long idfirmausuario) {
		this.idfirmausuario = idfirmausuario;
	}

	public Long getIdfirmadmin() {
		return idfirmadmin;
	}

	public void setIdfirmadmin(Long idfirmadmin) {
		this.idfirmadmin = idfirmadmin;
	}

	public Long getConformidad() {
		return conformidad;
	}

	public void setConformidad(Long conformidad) {
		this.conformidad = conformidad;
	}
	
}
