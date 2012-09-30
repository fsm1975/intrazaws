package com.technicalnorms.intraza.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import com.technicalnorms.intraza.web.rest.bd.JDBCQuery;
import com.technicalnorms.intraza.web.rest.bd.datos.JsonPedido;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoConsultaDato;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoDatosRutero;
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
	@Path("ruteros_total")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Rutero> consultaRuterosTotalBD() 
	{
		return JDBCQuery.getRuterosTotal();
	}
	
	@GET
	@Path("ruteros")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Rutero> consultaRuterosBD() 
	{
		return JDBCQuery.getRuteros();
	}
	
	@GET
	@Path("rutero_tarifa_cliente")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultadoConsultaDato consultaTarifaClienteRuteroBD(@QueryParam("idCliente") int cliente, @QueryParam("codigoArticulo") String articulo) 
	{
		return JDBCQuery.getRuteroTarifaCliente(cliente, articulo);
	}
	
	@GET
	@Path("rutero_tarifa_defecto")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultadoConsultaDato consultaTarifaDefectoRuteroBD(@QueryParam("codigoArticulo") String articulo) 
	{
		return JDBCQuery.getRuteroTarifaDefecto(articulo);
	}
	
	@GET
	@Path("rutero_peso_total_anio")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultadoConsultaDato consultaPesoTotalAnioRuteroBD(@QueryParam("idCliente") int cliente, @QueryParam("codigoArticulo") String articulo) 
	{
		return JDBCQuery.getRuteroPesoTotalAnio(cliente, articulo);
	}
	
	@GET
	@Path("rutero_datos")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultadoDatosRutero consultaDatosParaRuteroBD(@QueryParam("idCliente") int cliente, @QueryParam("codigoArticulo") String articulo) 
	{
		return JDBCQuery.getDatosParaRutero(cliente, articulo);
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
			JsonPedido datosPrepedido = mapper.readValue(jsonPrepedido, JsonPedido.class);
			
			resultadoEnvio = JDBCQuery.postPrepedido(datosPrepedido);
		}
		catch (Exception e )
		{
			resultadoEnvio = new ResultadoEnvioPedido(ResultadoEnvioPedido.CON_ERROR, "Se ha producido una excepcion al decodificar JSON ("+jsonPrepedido+") ("+e.toString()+")");
		}
		
		return resultadoEnvio;
	}
}
