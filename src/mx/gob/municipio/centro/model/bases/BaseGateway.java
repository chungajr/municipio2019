/**
 * @author Lsc. Mauricio Hernandez Leon.
 * @version 1.0
 *
 */
package mx.gob.municipio.centro.model.bases;


import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class BaseGateway {
    
    private static Logger log = Logger.getLogger(BaseGateway.class.getName());
    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("namedJdbcTemplate")
    private NamedParameterJdbcTemplate namedJdbcTemplate;    
    @Autowired 
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;
    
    public BaseGateway() {
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    
    public NamedParameterJdbcTemplate getNamedJdbcTemplate() {
        return namedJdbcTemplate;
    }

 
    public boolean esNuloOVacio(String cadena) {
        return ((cadena == null) || (cadena.trim().toString().length() == 0));
    }  
   
    /*public Date formatoFecha (String fecha) {
   	 Date fechaResultado =null;
   	 if (fecha != null && !fecha.equals("")){
		  try {
			  fechaResultado =  new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(fecha).getTime());          
			  } catch (Throwable ex) {				  					  
				  throw new IllegalArgumentException(ex.getMessage(), ex);
			  }           	
   	 }
        return fechaResultado;
   }*/
    public Date formatoFecha (String fecha) {
  	 Date fechaResultado =null;
  	 if (fecha!=null && !fecha.equals("")){
		  try {
			  fechaResultado =  new Date(new SimpleDateFormat("dd/MM/yyyy").parse(fecha).getTime());          
			  } catch (Throwable ex) {				  					  
				  throw new IllegalArgumentException(ex.getMessage(), ex);
			  }           	
  	 }
       return fechaResultado;
  }

    
    public  String rellenarCeros(String cad, int lng){
    	  String pattern = "000000000000000000000000000000000";

    	  if ( cad.equals("") ) return cad;

    	  return pattern.substring(0, lng - cad.length()) + cad;
    	}
    
    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }
    
	public double redondea(String numero, int decimales) 
	{ 
	  double resultado;
	  BigDecimal res;

	  res = new BigDecimal(numero).setScale(decimales, BigDecimal.ROUND_HALF_UP);
	  resultado = res.doubleValue();
	  return resultado; 
	}  
	
	public boolean getPrivilegioEn(int cve_pers, int idprivilegio){
		return this.getJdbcTemplate().queryForInt("SELECT  COUNT(*) AS N "+
													"FROM SAM_USUARIO_ROL "+
														"INNER JOIN SAM_ROL ON (SAM_ROL.ID_ROL = SAM_USUARIO_ROL.ID_ROL) "+
														"INNER JOIN SAM_ROL_PRIVILEGIO ON (SAM_ROL_PRIVILEGIO.ID_ROL = SAM_ROL.ID_ROL) "+
														"INNER JOIN SAM_PRIVILEGIO ON (SAM_PRIVILEGIO.ID_PRIVILEGIO = SAM_ROL_PRIVILEGIO.ID_PRIVILEGIO) "+
													"WHERE SAM_USUARIO_ROL.CVE_PERS = ? AND SAM_ROL_PRIVILEGIO.ID_PRIVILEGIO = ?", new Object[]{cve_pers, idprivilegio})>0;
	}
	
	public Map<String, String> toMap(String input) {
	    Map<String, String> map = new HashMap<String, String>();
	    String[] array = input.split(",");
	    for (String str : array) {
	        String[] pair = str.split("=");
	        map.put(pair[0], pair[1]);
	    }
	    return map;
	}
    
}
