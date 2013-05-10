package com.technicalnorms.intraza.web.rest.bd.datos;

public class ResultadoDatosRutero 
{	
	public final static int SIN_ERROR = 0;
	public final static int CON_ERROR = -1;
	
	private int codigoError = SIN_ERROR;
	private String descripcionError = null;
	private float tarifaCliente = 0;
	private float pesoTotalAnio = 0;
	private int unidadesTotalAnio = 0;
	
	public ResultadoDatosRutero(int codigoError, String descripcion, float tarifaCliente, float pesoTotalAnio, int unidadesTotalAnio) 
	{
		this.codigoError = codigoError;
		this.descripcionError = descripcion;
		this.tarifaCliente = tarifaCliente;
		this.pesoTotalAnio = pesoTotalAnio;
		this.unidadesTotalAnio = unidadesTotalAnio;
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
	
	public float getTarifaCliente() 
	{
		return this.tarifaCliente;
	}
	
	public void setTarifaCliente(float tarifa) 
	{
		this.tarifaCliente = tarifa;
	}
		
	public float getPesoTotalAnio() 
	{
		return this.pesoTotalAnio;
	}
	
	public void setPesoTotalAnio(float tarifa) 
	{
		this.pesoTotalAnio = tarifa;
	}
	
	public float getUnidadesTotalAnio() 
	{
		return this.unidadesTotalAnio;
	}
	
	public void setUnidadesTotalAnio(int tarifa) 
	{
		this.unidadesTotalAnio = tarifa;
	}
}
