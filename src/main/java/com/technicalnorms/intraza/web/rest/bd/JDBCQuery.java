package com.technicalnorms.intraza.web.rest.bd;

import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.technicalnorms.intraza.web.rest.PropiedadesConfiguracion;
import com.technicalnorms.intraza.web.rest.bd.datos.Articulo;
import com.technicalnorms.intraza.web.rest.bd.datos.Cliente;
import com.technicalnorms.intraza.web.rest.bd.datos.JsonPedido;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoConsultaDato;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoDatosRutero;
import com.technicalnorms.intraza.web.rest.bd.datos.ResultadoEnvioPedido;
import com.technicalnorms.intraza.web.rest.bd.datos.Rutero;
import com.technicalnorms.intraza.web.rest.bd.datos.Observacion;
import com.technicalnorms.intraza.web.rest.bd.datos.Totales;

/**
 * 
 * Clase con las operaciones de consulta a la BD
 *
 */
public class JDBCQuery 
{
	private static Logger logger = Logger.getLogger(JDBCQuery.class);
	
	private final static String SELECT_ARTICULOS = 
			"SELECT tai.iskg, tai.congelado, tai.codigo, trim(\"replace\"(\"replace\"(upper((((f.nombre_formato::text || ' '::text) || p.nombre_pieza::text) || ' '::text) || ca.nombre_clase_animal::text), 'UNIDAD'::text, ''::text), 'ENTERO'::text, ''::text)) AS nombre_completo, ttac.tarifa, to_char(fecha, 'DD-MM-YYYY') "+
			"FROM tipo_articulo_identificado tai ,formato f, pieza p, clase_animal ca, tipo_animal ta, tarifa_tipo_articulo_cliente ttac "+
			"WHERE tai.formato_fk = f.id_formato AND tai.pieza_fk = p.id_pieza AND tai.clase_animal_fk = ca.id_clase_animal AND ca.id_tipo_animal = ta.id_tipo_animal "+
			"AND ttac.cliente_fk is null and ttac.formato_fk=tai.formato_fk and ttac.pieza_fk=tai.pieza_fk and ttac.clase_animal_fk=tai.clase_animal_fk  and ttac.congelado=tai.congelado and ttac.iskg=tai.iskg "+
			"ORDER BY codigo";

	private final static String SELECT_ARTICULOS_SIN_TARIFA_DEFECTO =
			"SELECT distinct tai.iskg, tai.congelado, tai.codigo , trim(\"replace\"(\"replace\"(upper((((f.nombre_formato::text || ' '::text) || p.nombre_pieza::text) || ' '::text) || ca.nombre_clase_animal::text), 'UNIDAD'::text, ''::text), 'ENTERO'::text, ''::text)) AS nombre_completo "+
			"FROM tipo_articulo_identificado tai ,formato f, pieza p, clase_animal ca, tipo_animal ta, tarifa_tipo_articulo_cliente ttac "+ 
			"WHERE tai.formato_fk = f.id_formato "+ 
			"AND tai.pieza_fk = p.id_pieza "+
			"AND tai.clase_animal_fk = ca.id_clase_animal "+ 
			"AND ca.id_tipo_animal = ta.id_tipo_animal "+
			"AND ttac.cliente_fk IS NOT NULL "+
			"AND ttac.formato_fk=tai.formato_fk AND ttac.pieza_fk=tai.pieza_fk AND ttac.clase_animal_fk=tai.clase_animal_fk AND ttac.congelado=tai.congelado AND ttac.iskg=tai.iskg "+
			"AND tai.codigo NOT IN "+
			"(SELECT tai.codigo "+
			"FROM tipo_articulo_identificado tai ,formato f, pieza p, clase_animal ca, tipo_animal ta, tarifa_tipo_articulo_cliente ttac "+ 
			"WHERE tai.formato_fk = f.id_formato "+ 
			"AND tai.pieza_fk = p.id_pieza "+
			"AND tai.clase_animal_fk = ca.id_clase_animal "+ 
			"AND ca.id_tipo_animal = ta.id_tipo_animal "+
			"AND ttac.cliente_fk IS NULL "+
			"AND ttac.formato_fk=tai.formato_fk AND ttac.pieza_fk=tai.pieza_fk AND ttac.clase_animal_fk=tai.clase_animal_fk AND ttac.congelado=tai.congelado AND ttac.iskg=tai.iskg "+
			") ORDER BY codigo";
	
	private final static String SELECT_CLIENTES = 
			 "SELECT id_cliente, nombre_cliente, pedido_obs, telefono "+
			 "FROM cliente "+ 
			 "ORDER BY id_cliente";		
	
	//El campo peso contiene el ultimo peso servido, no el peso pedido
	private final static String SELECT_RUTEROS = 
			 "SELECT codigo_articulo, cliente_fk, to_char(fecha_pedido, 'DD-MM-YYYY'), unidades, peso, precio, observaciones_item, status "+
			 "FROM rutero "+ 
			 "ORDER BY codigo_articulo, cliente_fk";
	
	private final static String SELECT_OBSERVACIONES = 
			 "SELECT id, description, type "+
			 "FROM observaciones "+ 
			 "ORDER BY id";
	
	private final static String SELECT_TOTAL_ARTICULOS = 
			 "SELECT count(*) "+
			 "FROM tipo_articulo_identificado tai ,formato f, pieza p, clase_animal ca, tipo_animal ta "+
			 "WHERE tai.formato_fk = f.id_formato AND tai.pieza_fk = p.id_pieza AND tai.clase_animal_fk = ca.id_clase_animal AND ca.id_tipo_animal = ta.id_tipo_animal"; 
			
	private final static String SELECT_TOTAL_CLIENTES = 
			"SELECT count(*) "+
			"FROM cliente";		
			
	private final static String SELECT_TOTAL_RUTEROS = 
			"SELECT count(*) "+
			"FROM rutero";
			
	private final static String SELECT_TOTAL_OBSERVACIONES = 
			"SELECT count(*) "+
			"FROM observaciones";
	
	private final static String SELECT_ID_PEDIDO_PARA_INSERT = 
			"SELECT max(ID_PEDIDO) "+
			"FROM pedido";
	
	private final static String SELECT_ID_ARTICULO_COMERCIAL = 
			"SELECT max(ID) "+
			"FROM articulo_comercial";
	
	/**
	 * Devuelve una conexion con la BD
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection() throws Exception
	{
		// Load the database driver
		Class.forName("org.postgresql.Driver");

		// Get a connection to the database
		Connection conn = DriverManager.getConnection(PropiedadesConfiguracion.leeStringConfiguracion("URL_BD", "jdbc:postgresql://localhost/intraza?user=postgres"));

		return conn;
	}

	/**
	 * Obtiene los totales de los datos de articulo, cliente, rutero y observaciones a sincronizar de la BD de intraza
	 * 
	 * @return ArrayList donde cada elemento seran los datos de un articulo
	 */
	public static Totales getRegistrosTotales() 
	{
		Totales totales = new Totales(0, 0, 0, 0);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos el total de articulos
			logger.debug("Consultamos el total de articulos con la select: "+SELECT_TOTAL_ARTICULOS);
			rs = stmt.executeQuery(SELECT_TOTAL_ARTICULOS);

			while (rs.next())
			{
				totales.setTotalArticulos(rs.getInt(1));
			}
			
			//Consultamos el total de clientes
			logger.debug("Consultamos el total de clientes con la select: "+SELECT_TOTAL_CLIENTES);
			rs = stmt.executeQuery(SELECT_TOTAL_CLIENTES);

			while (rs.next())
			{
				totales.setTotalClientes(rs.getInt(1));
			}
			
			//Consultamos el total de ruteros
			logger.debug("Consultamos el total de ruteros con la select: "+SELECT_TOTAL_RUTEROS);
			rs = stmt.executeQuery(SELECT_TOTAL_RUTEROS);

			while (rs.next())
			{
				totales.setTotalRuteros(rs.getInt(1));
			}
			
			//Consultamos el total de observaciones
			logger.debug("Consultamos el total de observaciones con la select: "+SELECT_TOTAL_OBSERVACIONES);
			rs = stmt.executeQuery(SELECT_TOTAL_OBSERVACIONES);

			while (rs.next())
			{
				totales.setTotalObservaciones(rs.getInt(1));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();
			
			//Asi se indica que se ha producido un error
			totales = new Totales(-1, -1, -1, -1);
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return totales;
	}	
	
	/**
	 * Obtiene los datos de los articulos de la BD de intraza, para la tabla articulo de la BD de la tablet
	 * 
	 * @return ArrayList donde cada elemento seran los datos de un articulo
	 */
	public static ArrayList<Articulo> getArticulos() 
	{
		ArrayList<Articulo> listaArticulos = new ArrayList<Articulo>();
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			logger.debug("Consultamos los articulos con la select: "+SELECT_ARTICULOS);
			rs = stmt.executeQuery(SELECT_ARTICULOS);

			//Obtenemos los datos de cada registro
			while (rs.next())
			{
				listaArticulos.add(new Articulo(rs.getString(3), rs.getString(4), rs.getBoolean(1), rs.getBoolean(2), rs.getFloat(5), rs.getString(6)));
			}
			 
			logger.debug("Consultamos los articulos sin tarifa defecto con la select: "+SELECT_ARTICULOS_SIN_TARIFA_DEFECTO);
			rs = stmt.executeQuery(SELECT_ARTICULOS_SIN_TARIFA_DEFECTO);

			//Obtenemos los datos de cada registro
			while (rs.next())
			{
				listaArticulos.add(new Articulo(rs.getString(3), rs.getString(4), rs.getBoolean(1), rs.getBoolean(2), 0, null));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();
			
			//Devolvemos una lista vacia
			listaArticulos = new ArrayList<Articulo>();
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return listaArticulos;
	}
	
	/**
	 * Obtiene los datos de los clientes de la BD de intraza, para la tabla cliente de la BD de la tablet
	 * 
	 * @return ArrayList donde cada elemento seran los datos de un cliente
	 */
	public static ArrayList<Cliente> getClientes() 
	{
		ArrayList<Cliente> listaClientes = new ArrayList<Cliente>();
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los articulos
			logger.debug("Consultamos los clientes con la select: "+SELECT_CLIENTES);
			rs = stmt.executeQuery(SELECT_CLIENTES);

			//Obtenemos los datos de cada registro
			while (rs.next())
			{
				listaClientes.add(new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();
			
			//Devolvemos una lista vacia
			listaClientes = new ArrayList<Cliente>();
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try 
			{
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		return listaClientes;
	}
	
    /**
     * Obtiene los datos de los ruteros de la BD de intraza, para la tabla rutero de la BD de la tablet
     *
     * @return ArrayList donde cada elemento seran los datos de un rutero
     */
    public static ArrayList<Rutero> getRuterosTotal()
    {
            ArrayList<Rutero> listaRuteros = new ArrayList<Rutero>();
            Statement stmt = null;
            Statement stmtTarifaCliente = null;
            Statement stmtTarifaDefecto = null;
            Statement stmtPesoTotalAnio = null;
            Connection conn = null;
            ResultSet rs = null;
            ResultSet rsTarifaCliente = null;
            ResultSet rsTarifaDefecto = null;
            ResultSet rsPesoTotalAnio = null;
            String codigoArticulo = null;
            int idCliente = 0;
            String fechaPedido = null;
            int unidades = 0;
            float peso = 0;
            int unidadesTotalAnio = 0;
            float pesoTotalAnio = 0;
            float precio = 0;
            float precioCliente = 0;
            String observaciones = null;
            int status= 3;

            try
            {
                    conn = getConnection();
                    stmt = conn.createStatement();

                    //Consultamos los ruteros
                    logger.debug("Consultamos los ruteros con la select: "+SELECT_RUTEROS);
                    rs = stmt.executeQuery(SELECT_RUTEROS);

                    //Obtenemos los datos de cada registro
                    while (rs.next())
                    {
                            codigoArticulo = rs.getString(1);
                            idCliente = rs.getInt(2);
                            fechaPedido = rs.getString(3);
                            unidades = rs.getInt(4);
                            peso = rs.getFloat(5);
                            precio = rs.getFloat(6);
                            observaciones = rs.getString(7);
                            status = rs.getInt(8);
                           
                            //Para cada registro de rutero tenemos que obtener su tarifa cliente    
                            stmtTarifaCliente = conn.createStatement();
                            rsTarifaCliente = stmtTarifaCliente.executeQuery(dameSelectTarifaClienteArticuloParaRutero(idCliente, codigoArticulo));
                           
                            //Si no hemos obtenido registro, es porque no tiene tarifa cliente y hay que poner la tarifa por defecto
                            if (rsTarifaCliente.next())
                            {
                                    precioCliente = rsTarifaCliente.getFloat(1);
                            }
                            else
                            {
                                    //Para cada registro de rutero tenemos que obtener su tarifa cliente    
                                    stmtTarifaDefecto = conn.createStatement();
                                    rsTarifaDefecto = stmtTarifaDefecto.executeQuery(dameSelectTarifaDefectoArticuloParaRutero(codigoArticulo));
                                   
                                    if (rsTarifaDefecto.next())
                                    {
                                            precioCliente = rsTarifaDefecto.getFloat(1);
                                    }
                                    else
                                    {
                                            logger.debug("TARIFA CLIENTE 0. idCliente ("+idCliente+") codigoArticulo ("+codigoArticulo+")");
                                            precioCliente = 0;
                                    }                                      
                                   
                                    rsTarifaDefecto.close();
                                    stmtTarifaDefecto.close();
                            }
                           
                            rsTarifaCliente.close();
                            stmtTarifaCliente.close();
                           
                            //Tenemos que obtener el peso total al anio
                            stmtPesoTotalAnio = conn.createStatement();
                            rsPesoTotalAnio = stmtPesoTotalAnio.executeQuery(dameSelectPesoTotalAnioParaClienteArticulo(idCliente, codigoArticulo));
                           
                            if (rsPesoTotalAnio.next())
                            {      
                                    //Si es kilos tomamos el peso total, sino las unidades totales
//                                    if (rsPesoTotalAnio.getBoolean(1))
//                                    {
//                                            pesoTotalAnio = rsPesoTotalAnio.getFloat(2);
//                                    }
//                                    else
//                                    {
//                                            unidadesTotalAnio = rsPesoTotalAnio.getInt(3);                                    
//                                    }
                            	pesoTotalAnio = rsPesoTotalAnio.getFloat(2);
                            	unidadesTotalAnio = rsPesoTotalAnio.getInt(3);   
                            }
                            else
                            {
                            	pesoTotalAnio = 0;
                            	unidadesTotalAnio = 0;
                            }
                           
                            rsPesoTotalAnio.close();
                            stmtPesoTotalAnio.close();
                           
                            listaRuteros.add(new Rutero(codigoArticulo, idCliente, fechaPedido, unidades, peso, unidadesTotalAnio, pesoTotalAnio, precio, precioCliente, observaciones, status));
                    }
            }
            catch (Exception e)
            {
                    logger.error("Excepcion: "+e.toString());
                    e.printStackTrace();
                   
                    //Devolvemos una lista vacia
                    listaRuteros = new ArrayList<Rutero>();
            }
            //Cerramos los objetos abiertos para la consulta en la BD
            finally
            {
                    try {
                            if (rs!=null) rs.close();
                            if (rsTarifaCliente!=null) rsTarifaCliente.close();
                            if (rsTarifaDefecto!=null) rsTarifaDefecto.close();
                            if (rsPesoTotalAnio!=null) rsPesoTotalAnio.close();
                            if (stmt!=null) stmt.close();
                            if (stmtTarifaCliente!=null) stmtTarifaCliente.close();
                            if (stmtTarifaDefecto!=null) stmtTarifaDefecto.close();
                            if (stmtPesoTotalAnio!=null) stmtPesoTotalAnio.close();
                            if (conn!=null) conn.close();
                    }
                    catch (Exception e) {
                           e.printStackTrace();
                    }
            }

            return listaRuteros;
    }
	
	/**
	 * Obtiene los datos de los ruteros de la BD de intraza, para la tabla rutero de la BD de la tablet
	 * 
	 * @return ArrayList donde cada elemento seran los datos de un rutero
	 */
	public static ArrayList<Rutero> getRuteros() 
	{
		ArrayList<Rutero> listaRuteros = new ArrayList<Rutero>();
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String codigoArticulo = null;
		int idCliente = 0;
		String fechaPedido = null;
		int unidades = 0;
		float peso = 0;
		int unidadesTotalAnio = 0;
		float pesoTotalAnio = 0;
		float precio = 0;
		float precioCliente = 0;
		String observaciones = null;
        int status= 3;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los ruteros
			logger.debug("SINCRONIZANDO RUTEROS EN MODO 3G");
			logger.debug("Consultamos los ruteros con la select: "+SELECT_RUTEROS);
			rs = stmt.executeQuery(SELECT_RUTEROS);

			//Obtenemos los datos de cada registro
			while (rs.next())
			{
				codigoArticulo = rs.getString(1);
				idCliente = rs.getInt(2);
				fechaPedido = rs.getString(3);
				peso = rs.getFloat(4);
				precio = rs.getFloat(5);
				observaciones = rs.getString(6);
				status = rs.getInt(7);	
				
				listaRuteros.add(new Rutero(codigoArticulo, idCliente, fechaPedido, unidades, peso, unidadesTotalAnio, pesoTotalAnio, precio, precioCliente, observaciones, status));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();
			
			//Devolvemos una lista vacia
			listaRuteros = new ArrayList<Rutero>();
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return listaRuteros;
	}	
	
	/**
	 * Obtiene los datos de la tarifa cliente para un rutero
	 * 
	 * @param idCliente
	 * @param codigoArticulo
	 * 
	 * @return objeto con el resultado de la consulta
	 */
	public static ResultadoConsultaDato getRuteroTarifaCliente(int idCliente, String codigoArticulo) 
	{
		ResultadoConsultaDato resultado = new ResultadoConsultaDato(ResultadoEnvioPedido.SIN_ERROR, null, -1, 0);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los ruteros
			logger.debug("Consultamos la tarifa cliente para idCliente ("+idCliente+") codigoArticulo ("+codigoArticulo+")");

			rs = stmt.executeQuery(dameSelectTarifaClienteArticuloParaRutero(idCliente, codigoArticulo));
				
			if (rs.next())
			{
				resultado.setDato(rs.getFloat(1));
			}
			
			logger.debug("Tarifa cliente obtenida ("+resultado.getDato()+")");
			
			rs.close();
			stmt.close();
		} 
		catch (Exception e) 
		{
			resultado.setCodigoError(ResultadoConsultaDato.CON_ERROR);
			resultado.setDescripcionError(e.toString());
			
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();	
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null)stmt.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resultado;
	}
	
	/**
	 * Obtiene los datos de la tarifa defecto para un rutero
	 * 
	 * @param codigoArticulo
	 * 
	 * @return objeto con el resultado de la consulta
	 */
	public static ResultadoConsultaDato getRuteroTarifaDefecto(String codigoArticulo) 
	{
		ResultadoConsultaDato resultado = new ResultadoConsultaDato(ResultadoEnvioPedido.SIN_ERROR, null, -1, 0);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los ruteros
			logger.debug("Consultamos la tarifa defecto para codigoArticulo ("+codigoArticulo+")");

			rs = stmt.executeQuery(dameSelectTarifaDefectoArticuloParaRutero(codigoArticulo));
				
			if (rs.next())
			{
				resultado.setDato(rs.getFloat(1));
			}
			
			logger.debug("Tarifa defecto obtenida ("+resultado.getDato()+")");
			
			rs.close();
			stmt.close();
		} 
		catch (Exception e) 
		{
			resultado.setCodigoError(ResultadoConsultaDato.CON_ERROR);
			resultado.setDescripcionError(e.toString());
			
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();	
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resultado;
	}
	
	/**
	 * Obtiene los datos de la tarifa defecto para un rutero
	 *
	 * @param idCliente
	 * @param codigoArticulo
	 * 
	 * @return objeto con el resultado de la consulta
	 */
	public static ResultadoConsultaDato getRuteroPesoTotalAnio(int idCliente, String codigoArticulo) 
	{
		ResultadoConsultaDato resultado = new ResultadoConsultaDato(ResultadoEnvioPedido.SIN_ERROR, null, 0, 0);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los ruteros
			logger.debug("Consultamos el peso total al anio para idCliente ("+idCliente+") codigoArticulo ("+codigoArticulo+")");

			rs = stmt.executeQuery(dameSelectPesoTotalAnioParaClienteArticulo(idCliente, codigoArticulo));
				
			if (rs.next())
			{	
				//Si es kilos tomamos el peso total, sino las unidades totales
//				if (rs.getBoolean(1))
//				{
//					resultado.setDato(rs.getFloat(2));
//				}
//				else
//				{
//					resultado.setDato(rs.getFloat(3));					
//				}
				resultado.setDato(rs.getFloat(2));
				resultado.setDato2(rs.getInt(3));	
			}
			else
			{
				resultado.setDato(0);
				resultado.setDato2(0);
			}
			
			logger.debug("Peso total anual obtenido ("+resultado.getDato()+")");
			
			rs.close();
			stmt.close();
		} 
		catch (Exception e) 
		{
			resultado.setCodigoError(ResultadoConsultaDato.CON_ERROR);
			resultado.setDescripcionError(e.toString());
			
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();	
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!= null) stmt.close();
				if (conn!=null) conn.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resultado;
	}
	
    /**
     * Obtiene la tarifa cliente, tarifa defecto y peso total de una linea de rutero
     *
     * @param idCliente
     * @param codigoArticulo
     * @return ArrayList donde cada elemento seran los datos de un rutero
     */
    public static ResultadoDatosRutero getDatosParaRutero(int idCliente, String codigoArticulo)
    {
    		ResultadoDatosRutero resultadoDatosRutero = new ResultadoDatosRutero(ResultadoEnvioPedido.SIN_ERROR, null, -1, -1, 0);
            Statement stmtTarifaCliente = null;
            Statement stmtTarifaDefecto = null;
            Statement stmtPesoTotalAnio = null;
            Connection conn = null;
            ResultSet rsTarifaCliente = null;
            ResultSet rsTarifaDefecto = null;
            ResultSet rsPesoTotalAnio = null;

            try
            {
            	conn = getConnection();
                           
            	//Para cada registro de rutero tenemos que obtener su tarifa cliente    
            	stmtTarifaCliente = conn.createStatement();
            	rsTarifaCliente = stmtTarifaCliente.executeQuery(dameSelectTarifaClienteArticuloParaRutero(idCliente, codigoArticulo));
                           
            	//Si no hemos obtenido registro, es porque no tiene tarifa cliente y hay que poner la tarifa por defecto
            	if (rsTarifaCliente.next())
            	{
            		resultadoDatosRutero.setTarifaCliente(rsTarifaCliente.getFloat(1));
            	}
            	else
            	{    
            		stmtTarifaDefecto = conn.createStatement();
            		rsTarifaDefecto = stmtTarifaDefecto.executeQuery(dameSelectTarifaDefectoArticuloParaRutero(codigoArticulo));
                                   
            		if (rsTarifaDefecto.next())
            		{
            			resultadoDatosRutero.setTarifaCliente(rsTarifaDefecto.getFloat(1));
            		}
            		else
            		{
            			logger.debug("TARIFA CLIENTE 0. idCliente ("+idCliente+") codigoArticulo ("+codigoArticulo+")");
            			resultadoDatosRutero.setTarifaCliente(0);
            		}                                      
                                   
            		rsTarifaDefecto.close();
            		stmtTarifaDefecto.close();
            	}
                           
            	rsTarifaCliente.close();
            	stmtTarifaCliente.close();
                           
            	//Tenemos que obtener el peso total al anio
            	stmtPesoTotalAnio = conn.createStatement();
            	rsPesoTotalAnio = stmtPesoTotalAnio.executeQuery(dameSelectPesoTotalAnioParaClienteArticulo(idCliente, codigoArticulo));
                           
            	if (rsPesoTotalAnio.next())
            	{      
            		//Si es kilos tomamos el peso total, sino las unidades totales
//            		if (rsPesoTotalAnio.getBoolean(1))
//            		{
//            			resultadoDatosRutero.setPesoTotalAnio(rsPesoTotalAnio.getFloat(2));
//            		}
//            		else
//            		{
//            			resultadoDatosRutero.setPesoTotalAnio(rsPesoTotalAnio.getFloat(3));                                    
//            		}
            		
            		resultadoDatosRutero.setPesoTotalAnio(rsPesoTotalAnio.getFloat(2));
            		resultadoDatosRutero.setUnidadesTotalAnio(rsPesoTotalAnio.getInt(3));
            	}
            	else
            	{
            		resultadoDatosRutero.setPesoTotalAnio(0);
            		resultadoDatosRutero.setUnidadesTotalAnio(0);
            	}
                           
            	rsPesoTotalAnio.close();
            	stmtPesoTotalAnio.close();
            }
            catch (Exception e)
            {
                    logger.error("Excepcion: "+e.toString());
                    e.printStackTrace();
            }
            //Cerramos los objetos abiertos para la consulta en la BD
            finally
            {
                    try {
                            if (rsTarifaCliente!=null) rsTarifaCliente.close();
                            if (rsTarifaDefecto!=null) rsTarifaDefecto.close();
                            if (rsPesoTotalAnio!=null) rsPesoTotalAnio.close();
                            if (stmtTarifaCliente!=null) stmtTarifaCliente.close();
                            if (stmtTarifaDefecto!=null) stmtTarifaDefecto.close();
                            if (stmtPesoTotalAnio!=null) stmtPesoTotalAnio.close();
                            if (conn!=null) conn.close();
                    }
                    catch (Exception e) {
                            e.printStackTrace();
                    }
            }

            logger.debug("Para idCliente ("+idCliente+") codigoArticulo ("+codigoArticulo+") tenemos tarifa cliente ("+resultadoDatosRutero.getTarifaCliente()+") y peso total ("+resultadoDatosRutero.getPesoTotalAnio()+")");
            
            return resultadoDatosRutero;
    }
	
	private static String dameSelectTarifaClienteArticuloParaRutero(int idCliente, String codigoArticulo) throws Exception
	{
		String select = null;

		select = "SELECT  ttac.tarifa "+
				 "FROM tipo_articulo_identificado tai, formato f, pieza p, clase_animal ca, tipo_animal ta, tarifa_tipo_articulo_cliente ttac "+
				 "WHERE tai.formato_fk=f.id_formato AND tai.pieza_fk=p.id_pieza AND tai.clase_animal_fk=ca.id_clase_animal AND ca.id_tipo_animal=ta.id_tipo_animal "+
				 "AND ttac.cliente_fk="+idCliente+" AND ttac.pieza_fk=tai.pieza_fk AND ttac.formato_fk=tai.formato_fk AND ttac.clase_animal_fk=tai.clase_animal_fk "+
				 "AND ttac.congelado=tai.congelado AND ttac.iskg=tai.iskg AND codigo like '"+codigoArticulo+"' AND pi_observaciones like '' AND tarifa_porcentaje IS NULL";
		
		return select;
	}
	
	private static String dameSelectTarifaDefectoArticuloParaRutero(String codigoArticulo) throws Exception
	{
		String select = null;

		select = "SELECT  ttac.tarifa "+
				 "FROM tipo_articulo_identificado tai, formato f, pieza p, clase_animal ca, tipo_animal ta, tarifa_tipo_articulo_cliente ttac "+
				 "WHERE tai.formato_fk=f.id_formato AND tai.pieza_fk=p.id_pieza AND tai.clase_animal_fk=ca.id_clase_animal AND ca.id_tipo_animal=ta.id_tipo_animal "+
				 "AND ttac.cliente_fk IS NULL AND ttac.pieza_fk=tai.pieza_fk AND ttac.formato_fk=tai.formato_fk AND ttac.clase_animal_fk=tai.clase_animal_fk "+
				 "AND ttac.congelado=tai.congelado AND ttac.iskg=tai.iskg AND codigo like '"+codigoArticulo+"'";

		return select;
	}
	
	private static String dameSelectPesoTotalAnioParaClienteArticulo(int idCliente, String codigoArticulo) throws Exception
	{
		String select = null;

		select = "SELECT tai.iskg, sum(pi.peso_servido) as pesototal, sum(pi.unidades) as unidadestotales "+
				 "FROM pedido_item pi, articulo_comercial ac, tipo_articulo_identificado tai, pedido p "+
				 "WHERE pi.articulo_comercial_fk=ac.id AND tai.pieza_fk=ac.pieza_fk AND tai.clase_animal_fk=ac.clase_animal_fk "+
				 "AND tai.congelado=ac.es_congelado AND pi.iskg=tai.iskg AND tai.codigo like '"+codigoArticulo+"' AND p.id_pedido=pi.id_pedido "+
				 "AND p.id_cliente="+idCliente+" AND p.fecha_pedido > to_date(to_char(now(), 'DD-01-01 00:01'), 'YYYY') "+
				 "GROUP BY tai.iskg";

		return select;
	}
	
	/**
	 * Obtiene los datos de las observaciones de la BD de intraza, para la tabla observacion de la BD de la tablet
	 * 
	 * @return ArrayList donde cada elemento seran los datos de una observacion
	 */
	public static ArrayList<Observacion> getObservaciones() 
	{
		ArrayList<Observacion> listaObservaciones = new ArrayList<Observacion>();
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();

			//Consultamos los ruteros
			logger.debug("Consultamos las observaciones con la select: "+SELECT_OBSERVACIONES);
			rs = stmt.executeQuery(SELECT_OBSERVACIONES);

			//Obtenemos los datos de cada registro
			while (rs.next())
			{
				listaObservaciones.add(new Observacion(rs.getInt(1), rs.getString(2), rs.getInt(3)));
			}
		} 
		catch (Exception e) 
		{
			logger.error("Excepcion: "+e.toString());
			e.printStackTrace();
			
			//Devolvemos una lista vacia
			listaObservaciones = new ArrayList<Observacion>();
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return listaObservaciones;
	}
	
	/**
	 * Obtiene los datos de las observaciones de la BD de intraza, para la tabla observacion de la BD de la tablet
	 * 
	 * @return ArrayList donde cada elemento seran los datos de una observacion
	 */
	public static ResultadoEnvioPedido postPrepedido(JsonPedido prepedido) 
	{
		ResultadoEnvioPedido resultado = new ResultadoEnvioPedido(ResultadoEnvioPedido.SIN_ERROR, null);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		int idPedidoInTraza = 0;
		
		int formatoArticuloInTraza = 0;
		int piezaArticuloInTraza = 0;
		int claseAnimalArticuloInTraza = 0;
		boolean esCongeladoArticuloInTraza = false;
		boolean esKgArticuloInTraza = false;
		
		int idArticuloComercialInTraza = 0;

		try 
		{
			conn = getConnection();
			stmt = conn.createStatement();
			
			//**********************************
			//Insertamos los datos del prepedido
			//**********************************
			
			logger.debug("*** Vamos a enviar a intraza un prepedido nuevo. Cliente ("+prepedido.getIdCliente()+")");
			
			//Insertamos el prepedido
			stmt.executeUpdate(dameInsertPrepedido(prepedido.getIdCliente(), prepedido.getObservaciones(), prepedido.getDiaFechaPedido(), prepedido.getMesFechaPedido(), prepedido.getAnioFechaPedido(), prepedido.getDiaFechaEntrega(), prepedido.getMesFechaEntrega(), prepedido.getAnioFechaEntrega(), prepedido.getDescuentoEspecial()));
			
			//Obtenemos el id prepedido insertado en intraza
			rs = stmt.executeQuery(SELECT_ID_PEDIDO_PARA_INSERT);
			rs.next();
			idPedidoInTraza = rs.getInt(1);
			
			//Comprobamos si hay que fijar las observaciones del pedido en InTraza
			if (prepedido.getFijarObservaciones())
			{
				//Modificamos la observaciones por defecto que se encuentran en la tabla cliente y rutero
				stmt.executeUpdate(dameUpdateObservacionesPrepedidoTablaCliente(prepedido.getIdCliente(), prepedido.getObservaciones()));
				stmt.executeUpdate(dameUpdateObservacionesPrepedidoTablaRutero(prepedido.getIdCliente(), prepedido.getObservaciones()));
				
				logger.debug("Modificadas observaciones por defecto para el pedido. Cliente ("+prepedido.getIdCliente()+") observaciones ("+prepedido.getObservaciones()+")");
			}
			
			logger.debug("Insertado pedido ("+idPedidoInTraza+")");
			
			//******************************************
			//Insertamos los datos de los prepedido item
			//******************************************
			
			for (int i=0; i<prepedido.getLineasPedido().size(); i++)
			{
				logger.debug("*** Vamos a enviar a intraza un nuevo prepedido item. idPrepedido ("+idPedidoInTraza+") Cliente ("+prepedido.getIdCliente()+") codArticulo ("+prepedido.getLineasPedido().get(i).getCodArticulo()+")");
				
				//Obtenemos la información del articulo en intraza
				rs = stmt.executeQuery(dameSelectDatosArticulo(prepedido.getLineasPedido().get(i).getCodArticulo()));
				rs.next();
				formatoArticuloInTraza = rs.getInt(1);
				piezaArticuloInTraza = rs.getInt(2);
				claseAnimalArticuloInTraza = rs.getInt(3);
				esCongeladoArticuloInTraza = rs.getBoolean(4);
				esKgArticuloInTraza = rs.getBoolean(5);
				
				logger.debug("Datos articulo ("+formatoArticuloInTraza+") ("+piezaArticuloInTraza+") ("+claseAnimalArticuloInTraza+") ("+esCongeladoArticuloInTraza+")");
				
				//Hacemos un insert previso en el articulo comercial
				stmt.executeUpdate(dameInsertArticuloComercial(formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, esCongeladoArticuloInTraza));
				
				//Obtenemos el id del articulo comercial insertado en intraza
				rs = stmt.executeQuery(SELECT_ID_ARTICULO_COMERCIAL);
				rs.next();
				idArticuloComercialInTraza = rs.getInt(1);
				
				logger.debug("Articulo comercial creado ("+idArticuloComercialInTraza+")");
				
				//Hacemos un insert de la linea de prepedido
				stmt.executeUpdate(dameInsertPrepedidoItem(idPedidoInTraza, prepedido.getLineasPedido().get(i).getCantidadKg(), prepedido.getLineasPedido().get(i).getCantidadUd(), prepedido.getLineasPedido().get(i).getPrecio(), idArticuloComercialInTraza, prepedido.getLineasPedido().get(i).getObservaciones(), esKgArticuloInTraza));
				
				logger.debug("Insertado prepedido item");
				
				//Comprobamos si hay que fijar el articulo en el rutero del cliente en InTraza
				if (prepedido.getLineasPedido().get(i).getFijarArticulo())
				{
					//Insertamos los datos del articulo en el rutero del cliente
					stmt.executeUpdate(dameInsertArticuloEnRutero(idPedidoInTraza, prepedido.getDiaFechaPedido(), prepedido.getMesFechaPedido(), 
																  prepedido.getAnioFechaPedido(), prepedido.getIdCliente(), prepedido.getLineasPedido().get(i).getCantidadKg(), prepedido.getLineasPedido().get(i).getCantidadUd(),
																  esKgArticuloInTraza, prepedido.getLineasPedido().get(i).getPrecio(), 
																  prepedido.getLineasPedido().get(i).getCodArticulo(), prepedido.getLineasPedido().get(i).getNombreArticulo()));
					
					logger.debug("Insertado nuevo articulo. Cliente ("+prepedido.getIdCliente()+") codigo articulo ("+prepedido.getLineasPedido().get(i).getCodArticulo()+") nombreArticulo ("+prepedido.getLineasPedido().get(i).getNombreArticulo()+")");
				}
				
				//Comprobamos si hay que fijar las observaciones del pedido item en InTraza
				if (prepedido.getLineasPedido().get(i).getFijarObservaciones())
				{
					//Modificamos la observaciones por defecto para los prepedido item
					stmt.executeUpdate(dameDeleteObservacionesPrepedidoItem(prepedido.getIdCliente(), formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, esKgArticuloInTraza, esCongeladoArticuloInTraza));
					stmt.executeUpdate(dameInsertObservacionesPrepedidoItem(formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, prepedido.getIdCliente(), esKgArticuloInTraza, esCongeladoArticuloInTraza, prepedido.getLineasPedido().get(i).getObservaciones()));
					stmt.executeUpdate(dameUpdateObservacionesPrepedidoItemTablaRutero(prepedido.getIdCliente(), prepedido.getLineasPedido().get(i).getCodArticulo(), prepedido.getLineasPedido().get(i).getObservaciones()));
					
					logger.debug("Modificadas observaciones por defecto para el rutero. Cliente ("+prepedido.getIdCliente()+") codigo articulo ("+prepedido.getLineasPedido().get(i).getCodArticulo()+") observaciones ("+prepedido.getLineasPedido().get(i).getObservaciones()+")");
				}
				
				//Comprobamos si hay que fijar el precio del pedido item en InTraza
				if (prepedido.getLineasPedido().get(i).getFijarPrecio())
				{
					//Modificamos la observaciones por defecto para los prepedido item
					stmt.executeUpdate(dameDeletePrecioPrepedidoItem(prepedido.getIdCliente(), formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, esKgArticuloInTraza, esCongeladoArticuloInTraza));
					
					if (prepedido.getLineasPedido().get(i).getFijarObservaciones())
					{
						stmt.executeUpdate(dameInsertPrecioPrepedidoItemConObservaciones(formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, prepedido.getIdCliente(), esKgArticuloInTraza, esCongeladoArticuloInTraza, prepedido.getLineasPedido().get(i).getPrecio(), prepedido.getLineasPedido().get(i).getObservaciones()));
					}
					else
					{
						stmt.executeUpdate(dameInsertPrecioPrepedidoItem(formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, prepedido.getIdCliente(), esKgArticuloInTraza, esCongeladoArticuloInTraza, prepedido.getLineasPedido().get(i).getPrecio()));						
					}
					
					logger.debug("Modificado precio cliente. Cliente ("+prepedido.getIdCliente()+") codigo articulo ("+prepedido.getLineasPedido().get(i).getCodArticulo()+") precio ("+prepedido.getLineasPedido().get(i).getPrecio()+")");
				}
				
				//Comprobamos si hay que suprimir el precio del pedido item en InTraza
				if (prepedido.getLineasPedido().get(i).getSuprimirPrecio())
				{
					//Modificamos la observaciones por defecto para los prepedido item
					stmt.executeUpdate(dameDeletePrecioPrepedidoItem(prepedido.getIdCliente(), formatoArticuloInTraza, piezaArticuloInTraza, claseAnimalArticuloInTraza, esKgArticuloInTraza, esCongeladoArticuloInTraza));
					
					logger.debug("Quitado precio cliente. Cliente ("+prepedido.getIdCliente()+") codigo articulo ("+prepedido.getLineasPedido().get(i).getCodArticulo()+") precio ("+prepedido.getLineasPedido().get(i).getPrecio()+")");
				}
			}
			
			//Leemos de la configuracion como hay que insertar el pedido, si como prepedido o pedido, al crear el pedido siempre se pone este campo a "false"
			if (PropiedadesConfiguracion.leeStringConfiguracion("INSERTAR_PEDIDOS_COMO_PREPEDIDOS", "SI").equals("SI"))
			{
				stmt.executeUpdate(dameUpdatePrepedidoCampoEsPrepedido(idPedidoInTraza, true));
				
				logger.debug("Modificado el Pedido ("+idPedidoInTraza+") para indicar que es prepedido");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			resultado.setCodigoError(ResultadoEnvioPedido.CON_ERROR);
			resultado.setDescripcionError("Se ha producido una excepcion ("+e.toString()+")");
		} 
		//Cerramos los objetos abiertos para la consulta en la BD
		finally 
		{
			try {
				if (rs!=null) rs.close();
				if (stmt!=null) stmt.close();
				if (conn!=null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resultado;
	}
	
	private static String dameInsertPrepedido(int idCliente, String observaciones,
											  int diaPedido, int mesPedido, int anioPedido,
											  int diaEntrega, int mesEntrega, int anioEntrega, int descuentoEspecial) throws Exception
	{
		String insert = null;
		
		//Insertamos siempre a false, el campo que indica si es prepedido y al final de insertar las lineas de pedido, hacemos un update segun se indique en
		//configuracion, asi nos evitamos este problema:
		// "Si mientras envian algun prepedido, desde intraza recuperan un prepedido
		// que se esta enviando pero no ha terminado pueden recuperarlo incompleto,
		// lo guardan y pierden la mitad de pedido items"	
		
		insert =
				"INSERT INTO pedido "+
				"(fecha_pedido, id_cliente, fecha_entrega, observaciones, es_prepedido, descuento_especial) "+
				"VALUES "+
				"(to_timestamp('"+diaPedido+"-"+mesPedido+"-"+anioPedido+"', 'DD-MM-YYYY'), "+idCliente+", to_timestamp('"+diaEntrega+"-"+mesEntrega+"-"+anioEntrega+"', 'DD-MM-YYYY'), '"+observaciones+"', false, "+descuentoEspecial+")";
		
		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameUpdatePrepedidoCampoEsPrepedido(int idPedido, boolean esPrepedido) throws Exception
	{
		String update = null;

		update =
				"UPDATE pedido "+
				"SET es_prepedido = "+esPrepedido+" "+
				"WHERE id_pedido = "+idPedido;
		
		logger.debug(update);
		
		return update;
	}
	
	private static String dameSelectDatosArticulo(String codigoArticulo) throws Exception
	{
		String select = null;

		select = "SELECT formato_fk, pieza_fk, clase_animal_fk, congelado, iskg "+
				 "FROM tipo_articulo_identificado "+
				 "WHERE codigo like '"+codigoArticulo+"'";
		
		logger.debug(select);

		return select;
	}
	
	private static String dameUpdateObservacionesPrepedidoTablaCliente(int idCliente, String observaciones) throws Exception
	{
		String update = null;

		update =
				"UPDATE cliente "+
				"SET pedido_obs = '"+observaciones+"' "+
				"WHERE id_cliente = "+idCliente;
		
		logger.debug(update);
		
		return update;
	}
	
	private static String dameUpdateObservacionesPrepedidoTablaRutero(int idCliente, String observaciones) throws Exception
	{
		String update = null;

		update =
				"UPDATE rutero "+
				"SET observaciones = '"+observaciones+"' "+
				"WHERE cliente_fk = "+idCliente;
		
		logger.debug(update);
		
		return update;
	}
	
	private static String dameInsertArticuloComercial(int formato, int pieza, int claseAnimal, boolean esCongelado) throws Exception
	{
		String insert = null;
		
		insert =
				"INSERT INTO articulo_comercial "+
				"(es_articulo, formato_fk, pieza_fk, clase_animal_fk, es_congelado, es_custom) "+
				"VALUES "+
				"(true, "+formato+", "+pieza+", "+claseAnimal+", "+esCongelado+", false)";

		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameInsertPrepedidoItem(int idPedido, float cantidadKg, int cantidadUd, float precio,
												  int idArticuloComercial, String observaciones, boolean esKg) throws Exception
	{
		String insert = null;
		
		insert =
				"INSERT INTO pedido_item "+
				"(id_pedido, unidades, peso, precio_kg, articulo_comercial_fk, observaciones, numero_lote_proveedor, status, isKg) "+
				"VALUES "+
				"("+idPedido+", "+cantidadUd+", "+cantidadKg+", "+precio+", "+idArticuloComercial+", '"+observaciones+"', '', 3, "+esKg+")";
		
		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameInsertArticuloEnRutero(int idPedido, int diaPedido, int mesPedido, int anioPedido, int idCliente, float cantidadKg, int cantidadUd, boolean esKg, float precio, String codigoArticulo, String nombreArticulo) throws Exception
	{
		String insert = null;

		insert =
				"INSERT INTO rutero "+
				"(num_pedido, fecha_pedido, cliente_fk, unidades, peso, precio, codigo_articulo, nombre_articulo) "+
				"VALUES "+
				"("+idPedido+", to_timestamp('"+diaPedido+"-"+mesPedido+"-"+anioPedido+"', 'DD-MM-YYYY'), "+idCliente+", "+cantidadUd+", "+cantidadKg+", "+precio+", '"+codigoArticulo+"', '"+nombreArticulo+"')";

		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameDeleteObservacionesPrepedidoItem(int idCliente, int formato, int pieza, int claseAnimal, boolean esKg, boolean esCongelado) throws Exception
	{
		String delete = null;

		delete =
				"DELETE FROM tarifa_tipo_articulo_cliente "+
				"WHERE formato_fk="+formato+ " AND pieza_fk="+pieza+" AND clase_animal_fk="+claseAnimal+" AND cliente_fk="+idCliente+" AND iskg = "+esKg+" AND congelado = "+esCongelado+" AND pi_observaciones <> ''";
		
		logger.debug(delete);
		
		return delete;
	}
	
	private static String dameInsertObservacionesPrepedidoItem(int formato, int pieza, int claseAnimal, int idCliente, boolean esKg, boolean esCongelado, String observaciones) throws Exception
	{
		String insert = null;

		insert =
				"INSERT INTO tarifa_tipo_articulo_cliente "+
				"(formato_fk, pieza_fk, clase_animal_fk, cliente_fk, iskg, congelado, pi_observaciones) "+
				"VALUES "+
				"("+formato+", "+pieza+", "+claseAnimal+", "+idCliente+", "+esKg+", "+esCongelado+", '"+observaciones+"')";
		
		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameUpdateObservacionesPrepedidoItemTablaRutero(int idCliente, String codigoArticulo, String observaciones) throws Exception
	{
		String update = null;

		update =
				"UPDATE rutero "+
				"SET observaciones_item = '"+observaciones+"' "+
				"WHERE cliente_fk = "+idCliente+" AND codigo_articulo like '"+codigoArticulo+"'";
	
		logger.debug(update);
		
		return update;
	}
	
	private static String dameDeletePrecioPrepedidoItem(int idCliente, int formato, int pieza, int claseAnimal, boolean esKg, boolean esCongelado) throws Exception
	{
		String delete = null;

		delete =
				"DELETE FROM tarifa_tipo_articulo_cliente "+
				"WHERE formato_fk="+formato+ " AND pieza_fk="+pieza+" AND clase_animal_fk="+claseAnimal+" AND cliente_fk="+idCliente+" AND iskg = "+esKg+" AND congelado = "+esCongelado+" AND tarifa > 0";

		logger.debug(delete);
		
		return delete;
	}
	
	private static String dameInsertPrecioPrepedidoItem(int formato, int pieza, int claseAnimal, int idCliente, boolean esKg, boolean esCongelado, float precio) throws Exception
	{
		String insert = null;

		insert =
				"INSERT INTO tarifa_tipo_articulo_cliente "+
				"(formato_fk, pieza_fk, clase_animal_fk, cliente_fk, tarifa, iskg, congelado) "+
				"VALUES "+
				"("+formato+", "+pieza+", "+claseAnimal+", "+idCliente+", "+precio+", "+esKg+", "+esCongelado+")";

		logger.debug(insert);
		
		return insert;
	}
	
	private static String dameInsertPrecioPrepedidoItemConObservaciones(int formato, int pieza, int claseAnimal, int idCliente, boolean esKg, boolean esCongelado, float precio, String observaciones) throws Exception
	{
		String insert = null;

		insert =
				"INSERT INTO tarifa_tipo_articulo_cliente "+
				"(formato_fk, pieza_fk, clase_animal_fk, cliente_fk, tarifa, iskg, congelado, pi_observaciones) "+
				"VALUES "+
				"("+formato+", "+pieza+", "+claseAnimal+", "+idCliente+", "+precio+", "+esKg+", "+esCongelado+", '"+observaciones+"')";

		logger.debug(insert);
		
		return insert;
	}
}