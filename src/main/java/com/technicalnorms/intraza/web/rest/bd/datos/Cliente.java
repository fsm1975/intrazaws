package com.technicalnorms.intraza.web.rest.bd.datos;

public class Cliente 
{	
	private int idCliente = 0;
	private String nombreCliente = null;
	private String pedidoObs = null;
	private String telefono = null;
	
	public Cliente(int idCliente, String nombreCliente, String pedidoObs, String telefono) 
	{
		this.idCliente = idCliente;
		this.nombreCliente = nombreCliente;
		this.pedidoObs = pedidoObs;
		this.telefono = telefono;
	}
	
	public int getIdCliente() 
	{
		return this.idCliente;
	}
	
	public void setIdCliente(int idCliente) 
	{
		this.idCliente = idCliente;
	}
	
	public String getNombreCliente() 
	{
		return this.nombreCliente;
	}
	
	public void setNombreCliente(String nombreCliente) 
	{
		this.nombreCliente = nombreCliente;
	}
	
	public String getPedidoObs() 
	{
		return this.pedidoObs;
	}
	
	public void setPedidoObs(String pedidoObs) 
	{
		this.pedidoObs = pedidoObs;
	}
	
	public String getTelefono() 
	{
		return this.telefono;
	}
	
	public void setTelefono(String telefono) 
	{
		this.telefono = telefono;
	}
}
