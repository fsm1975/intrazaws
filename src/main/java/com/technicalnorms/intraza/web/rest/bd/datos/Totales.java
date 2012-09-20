package com.technicalnorms.intraza.web.rest.bd.datos;

public class Totales 
{	
	private int totalArticulos = 0;
	private int totalClientes = 0;
	private int totalRuteros = 0;
	private int totalObservaciones = 0;
	
	public Totales(int totalArticulos, int totalClientes, int totalRuteros, int totalObservaciones) 
	{
		this.totalArticulos = totalArticulos;
		this.totalClientes = totalClientes;
		this.totalRuteros = totalRuteros;
		this.totalObservaciones = totalObservaciones;
	}
	
	public int getTotalArticulos() 
	{
		return this.totalArticulos;
	}
	
	public void setTotalArticulos(int totalArticulos) 
	{
		this.totalArticulos = totalArticulos;
	}
	
	public int getTotalClientes() 
	{
		return this.totalClientes;
	}
	
	public void setTotalClientes(int totalClientes) 
	{
		this.totalClientes = totalClientes;
	}
	
	public int getTotalRuteros() 
	{
		return this.totalRuteros;
	}
	
	public void setTotalRuteros(int totalRuteros) 
	{
		this.totalRuteros = totalRuteros;
	}
	
	public int getTotalObservaciones() 
	{
		return this.totalObservaciones;
	}
	
	public void setTotalObservaciones(int totalObservaciones) 
	{
		this.totalObservaciones = totalObservaciones;
	}
}
