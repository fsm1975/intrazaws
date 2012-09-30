package com.technicalnorms.intraza.web.rest.bd.datos;

public class ResultadoConsultaDato 
{	
	public final static int SIN_ERROR = 0;
	public final static int CON_ERROR = -1;
	
	private int codigoError = SIN_ERROR;
	private String descripcionError = null;
	private float dato = 0;
	
	public ResultadoConsultaDato(int codigoError, String descripcion, float dato) 
	{
		this.codigoError = codigoError;
		this.descripcionError = descripcion;
		this.dato = dato;
	}
	
	public int getCodigoError() 
	{
		return this.codigoError;
	}
	
	public void setCodigoError(int codigo) 
	{
		this.codigoError = codigo;
	}
	
	public String getDescripcionError() 
	{
		return this.descripcionError;
	}
	
	public void setDescripcionError(String descripcion) 
	{
		this.descripcionError = descripcion;
	}
	
	public float getDato() 
	{
		return this.dato;
	}
	
	public void setDato(float dato) 
	{
		this.dato = dato;
	}
}
