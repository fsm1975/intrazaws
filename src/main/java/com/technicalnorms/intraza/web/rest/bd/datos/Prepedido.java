package com.technicalnorms.intraza.web.rest.bd.datos;

public class Prepedido 
{	
	private String id=null;
	private String name = null;
	private boolean esKg = false;
	private boolean esCongelado = false;
	private float precioDefecto = 0;
	private String fechaCambioPrecioDefecto = null;
	
	public Prepedido(String id, String name, boolean esKg, boolean esCongelado, float precioDefecto, String fechaCambioPrecioDefecto) 
	{
		this.id = id;
		this.name = name;
		this.esKg = esKg;
		this.esCongelado = esCongelado;
		this.precioDefecto = precioDefecto;
		this.fechaCambioPrecioDefecto = fechaCambioPrecioDefecto;
	}
	
	public String getId() 
	{
		return this.id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}
	public String getName() 
	{
		return this.name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public boolean getEsKg() 
	{
		return this.esKg;
	}
	
	public void setEsKg(boolean esKg) 
	{
		this.esKg = esKg;
	}
	
	public boolean getEsCongelado() 
	{
		return this.esCongelado;
	}
	
	public void setEsCongelado(boolean esCongelado) 
	{
		this.esCongelado = esCongelado;
	}
	
	public float getPrecioDefecto() 
	{
		return this.precioDefecto;
	}
	
	public void setPrecioDefecto(float precioDefecto) 
	{
		this.precioDefecto = precioDefecto;
	}
	
	public String getFechaCambioPrecioDefecto() 
	{
		return this.fechaCambioPrecioDefecto;
	}
	
	public void setFechaCambioPrecioDefecto(String fecha) 
	{
		this.fechaCambioPrecioDefecto = fecha;
	}
}
