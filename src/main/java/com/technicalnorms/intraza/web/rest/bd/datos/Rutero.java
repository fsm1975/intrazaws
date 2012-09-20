package com.technicalnorms.intraza.web.rest.bd.datos;

public class Rutero
{	
	private String codigoArticulo = null;
	private int idCliente = 0;
	private String fechaPedido = null;
	private float peso = 0;
	private float pesoTotalAnio = 0;
	private float precio = 0;
	private float precioCliente = 0;
	private String observacionesItem = null;
	
	
	public Rutero(String codigoArticulo, int idCliente, String fechaPedido, float peso, float pesoTotalAnio, float precio, float precioCliente, String observaciones) 
	{
		this.codigoArticulo = codigoArticulo;
		this.idCliente = idCliente;
		this.fechaPedido = fechaPedido;
		this.peso = peso;
		this.pesoTotalAnio = pesoTotalAnio;
		this.precio = precio;
		this.precioCliente = precioCliente;
		this.observacionesItem = observaciones;
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
	
	public float getPeso() 
	{
		return this.peso;
	}
	
	public void setPeso(float peso) 
	{
		this.peso = peso;
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
}
