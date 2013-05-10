package com.technicalnorms.intraza.web.rest.bd.datos;

public class ResultadoConsultaDato 
{	
	public final static int SIN_ERROR = 0;
	public final static int CON_ERROR = -1;
	
	private int codigoError = SIN_ERROR;
	private String descripcionError = null;
	private float dato = 0;
	private int dato2 = 0;
	
	public ResultadoConsultaDato(int codigoError, String descripcion, float dato, int dato2) 
	{
		this.codigoError = codigoError;
		this.descripcionError = descripcion;
		this.dato = dato;
		this.dato2 = dato2;
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
	
	public int getDato2() 
	{
		return this.dato2;
	}
	
	public void setDato2(int dato2) 
	{
		this.dato2 = dato2;
	}
}
