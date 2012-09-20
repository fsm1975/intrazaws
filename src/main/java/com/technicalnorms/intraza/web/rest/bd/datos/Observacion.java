package com.technicalnorms.intraza.web.rest.bd.datos;

public class Observacion 
{	
	private int id = 0;
	private String descripcion = null;
	private int tipo = 1;
	
	public Observacion(int id, String descripcion, int tipo) 
	{
		this.id = id;
		this.descripcion = descripcion;
		this.tipo = tipo;
	}
	
	public int getId() 
	{
		return this.id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public String getDescripcion() 
	{
		return this.descripcion;
	}
	
	public void setDescripcion(String descripcion) 
	{
		this.descripcion = descripcion;
	}
	
	public int getTipo() 
	{
		return this.tipo;
	}
	
	public void setTipo(int tipo) 
	{
		this.tipo = tipo;
	}
}
