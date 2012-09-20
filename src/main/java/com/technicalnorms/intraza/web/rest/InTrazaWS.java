package com.technicalnorms.intraza.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import com.technicalnorms.intraza.web.rest.bd.JDBCQuery;
import com.technicalnorms.intraza.web.rest.bd.datos.JsonPrepedido;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoEnvioPedido;
import com.technicalnorms.intraza.web.rest.bd.datos.Totales;
import com.technicalnorms.intraza.web.rest.bd.datos.Articulo;
import com.technicalnorms.intraza.web.rest.bd.datos.Cliente;
import com.technicalnorms.intraza.web.rest.bd.datos.Rutero;
import com.technicalnorms.intraza.web.rest.bd.datos.Observacion;

/**
 * 
 * Clase que define los WebService REST
 * 
 */
@Path(value = "/sincroniza")
public class InTrazaWS 
{
	@GET
	@Path("totales")
	@Produces(MediaType.APPLICATION_JSON)
	public Totales consultaTotalesBD() 
	{
		return JDBCQuery.getRegistrosTotales();
	}
	
	@GET
	@Path("articulos")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Articulo> consultaArticulosBD() 
	{
		return JDBCQuery.getArticulos();
	}
	
	@GET
	@Path("clientes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Cliente> consultaClientesBD() 
	{
		return JDBCQuery.getClientes();
	}
	
	@GET
	@Path("ruteros")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Rutero> consultaRuterosBD() 
	{
		return JDBCQuery.getRuteros();
	}
	
	@GET
	@Path("observaciones")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Observacion> consultaObservacionesBD() 
	{
		return JDBCQuery.getObservaciones();
	}
	
	@POST
	@Path("prepedido")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultadoEnvioPedido enviaPrepedidoBD(String jsonPrepedido) 
	{
		ResultadoEnvioPedido resultadoEnvio = null;
		try
		{
			//Convertimos el JSON que nos llega a un objeto java
			ObjectMapper mapper = new ObjectMapper();
			JsonPrepedido datosPrepedido = mapper.readValue(jsonPrepedido, JsonPrepedido.class);
			
			resultadoEnvio = JDBCQuery.postPrepedido(datosPrepedido);
		}
		catch (Exception e )
		{
			resultadoEnvio = new ResultadoEnvioPedido(ResultadoEnvioPedido.CON_ERROR, "Se ha producido una excepcion al decodificar JSON ("+jsonPrepedido+") ("+e.toString()+")");
		}
		
		return resultadoEnvio;
	}
}
