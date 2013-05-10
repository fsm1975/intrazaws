package com.technicalnorms.intraza.web.rest.bd.datos;

public class Rutero
{	
	private String codigoArticulo = null;
	private int idCliente = 0;
	private String fechaPedido = null;
	private int unidades = 0;
	private float peso = 0;
	private int unidadesTotalAnio = 0;
	private float pesoTotalAnio = 0;
	private float precio = 0;
	private float precioCliente = 0;
	private String observacionesItem = null;
    // El status por defecto es 3
	private int status = 3;
	
	
	public Rutero(String codigoArticulo, int idCliente, String fechaPedido, int unidades, float peso, int unidadesTotalAnio, float pesoTotalAnio, float precio, float precioCliente, String observaciones, int status) 
	{
		this.codigoArticulo = codigoArticulo;
		this.idCliente = idCliente;
		this.fechaPedido = fechaPedido;
		this.unidades = unidades;
		this.peso = peso;
		this.unidadesTotalAnio = unidadesTotalAnio;
		this.pesoTotalAnio = pesoTotalAnio;
		this.precio = precio;
		this.precioCliente = precioCliente;
		this.observacionesItem = observaciones;
		this.status = status;
	}
	
	public String getCodigoArticulo() 
	{
		return this.codigoArticulo;
	}
	
	public void setCodigoArticulo(String codigoArticulo) 
	{
		this.codigoArticulo = codigoArticulo;
	}
	
	public int getIdCliente() 
	{
		return this.idCliente;
	}
	
	public void setIdCliente(int idCliente) 
	{
		this.idCliente = idCliente;
	}
	
	public String getFechaPedido() 
	{
		return this.fechaPedido;
	}
	
	public void setFechaPedido(String fechaPedido) 
	{
		this.fechaPedido = fechaPedido;
	}
	
	public float getUnidades() 
	{
		return this.unidades;
	}
	
	public void setUnidades(int unidades) 
	{
		this.unidades = unidades;
	}
	
	public float getPeso() 
	{
		return this.peso;
	}
	
	public void setPeso(float peso) 
	{
		this.peso = peso;
	}
	
	public int getUnidadesTotalAnio() 
	{
		return this.unidadesTotalAnio;
	}
	
	public void setUnidadesTotalAnio(int unidadesTotalAnio) 
	{
		this.unidadesTotalAnio = unidadesTotalAnio;
	}
	
	public float getPesoTotalAnio() 
	{
		return this.pesoTotalAnio;
	}
	
	public void setPesoTotalAnio(float pesoTotalAnio) 
	{
		this.pesoTotalAnio = pesoTotalAnio;
	}
	
	public float getPrecio() 
	{
		return this.precio;
	}
	
	public void setPrecio(float precio) 
	{
		this.precio = precio;
	}
	
	public float getPrecioCliente() 
	{
		return this.precioCliente;
	}
	
	public void setPrecioCliente(float precioCliente) 
	{
		this.precioCliente = precioCliente;
	}
		
	public String getObservacionesItem() 
	{
		return this.observacionesItem;
	}
	
	public void setObservacionesItem(String observaciones) 
	{
		this.observacionesItem = observaciones;
	}
	
	public int getStatus() 
	{
		return this.status;
	}
	
	public void setStatus(int status) 
	{
		this.status = status;
	}
}
