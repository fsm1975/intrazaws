package com.technicalnorms.intraza.web.rest.bd.datos;

public class ResultadoEnvioPedido 
{	
	public final static int SIN_ERROR = 0;
	public final static int CON_ERROR = -1;
	
	private int codigoError = SIN_ERROR;
	private String descripcionError = null;
	
	public ResultadoEnvioPedido(int codigoError, String descripcion) 
	{
		this.codigoError = codigoError;
		this.descripcionError = descripcion;
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
}
