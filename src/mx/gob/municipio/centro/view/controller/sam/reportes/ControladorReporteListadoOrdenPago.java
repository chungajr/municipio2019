package mx.gob.municipio.centro.view.controller.sam.reportes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import mx.gob.municipio.centro.model.gateways.sam.GatewayOrdenDePagos;
import mx.gob.municipio.centro.model.gateways.sam.GatewayPedidos;
import mx.gob.municipio.centro.view.bases.ControladorBase;

@Controller
@RequestMapping("/sam/reportes/rpt_listado_op.action")
public class ControladorReporteListadoOrdenPago extends ControladorBase {
public final static  int VER_TODAS_LAS_UNIDADES = 25;
	
	@Autowired
	GatewayOrdenDePagos gatewayOrdenDePagos;
	
	public ControladorReporteListadoOrdenPago() {
		// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = {RequestMethod.POST,RequestMethod.GET}) 
	public ModelAndView  requestGetControlador( Map data, HttpServletRequest request) {
		boolean privilegio = this.getPrivilegioEn(this.getSesion().getIdUsuario(), VER_TODAS_LAS_UNIDADES);
		String unidad=request.getParameter("cbodependencia")==null ?this.getSesion().getClaveUnidad() : request.getParameter("cbodependencia");
		String estatus=request.getParameter("status")==null ? Integer.toString(gatewayOrdenDePagos.OP_ESTADO_EN_EDICION): this.arrayToString(request.getParameterValues("status"),",");
		String fechaIni=request.getParameter("fechaInicial");
		String fechaFin=request.getParameter("fechaFinal");
		String tipoGasto=request.getParameter("cbotipogasto");
		String cve_beneficiario= request.getParameter("cboprestadorservicio")==null ? "": request.getParameter("cboprestadorservicio");
		data.put("cbocapitulo", (request.getParameter("cbocapitulo")==null ? "": request.getParameter("cbocapitulo")));
		
		String cve_benefi=request.getParameter("CVE_BENEFI");
		String tipo=request.getParameter("cbotipo");
		String numop = request.getParameter("txtnumop");
		String numped = request.getParameter("txtpedido");
		
		//determinar si solo puede filtrar capitulo 5000 en privilegios
		if(this.getPrivilegioEn(this.getSesion().getIdUsuario(), 113)){
			data.put("cbocapitulo" ,5000);
		}
		
		if(privilegio){
			if(request.getParameter("cbodependencia")==null)
				unidad = "0";
			if(request.getParameter("cbodependencia")!=null)
				unidad = request.getParameter("cbodependencia");
		}
		
		if(!privilegio){
			if(request.getParameter("cbodependencia")==null)
				unidad = this.getSesion().getClaveUnidad();
			if(request.getParameter("cbodependencia")!=null)
				unidad = request.getParameter("cbodependencia");
		}
		
		//if(beneficiario==null ||beneficiario.equals("")) cve_benefi = "";
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		String fecha = dateFormat.format(date); 
		data.put("FECHA_IMPRESION", fecha);
		
		String unidad_adm = (String) this.getJdbcTemplate().queryForObject("SELECT CLV_UNIADM+' '+DEPENDENCIA FROM CAT_DEPENDENCIAS WHERE ID = ?", new Object[]{this.getSesion().getClaveUnidad()}, String.class);
		data.put("UNIDAD_SOLICITANTE", unidad_adm);
		
		String verUnidad=request.getParameter("verUnidad");
		data.put("idUnidad", unidad);
		
		data.put("cbotipo", tipo);
		data.put("txtnumop", numop);
		data.put("txtpedido", numped);
		data.put("ejercicio",this.getSesion().getEjercicio());
		data.put("fechaInicial",fechaIni);
		data.put("fechaFinal",fechaFin);
		data.put("status",estatus);
		data.put("tipo_gto",tipoGasto );
		data.put("CVE_BENEFI",cve_benefi );
		data.put("verUnidad",verUnidad);
		data.put("nombreUnidad",this.getSesion().getUnidad());
		List <Map<String, Object>> lista = this.getListadoOrdenes(unidad, estatus, fechaIni,fechaFin, cve_benefi, this.getSesion().getEjercicio(),tipoGasto,this.getSesion().getIdUsuario(),verUnidad, tipo, numop,  privilegio, data.get("cbocapitulo").toString());
		data.put("rs",new JRMapCollectionDataSource(lista));		
		return new ModelAndView("rpt_listado_op",data);
	}
	
	public List <Map<String, Object>>getListadoOrdenes(String unidad, String  estatus , String fechaInicial, String fechaFinal , String clv_benefi, Integer ejercicio, String tipoGasto, Integer idUsuario, String verUnidad, String tipo, String numop,  boolean privilegio, String capitulo){
		return this.gatewayOrdenDePagos.getListaDeOrdenesPorEjemplo(unidad, estatus , this.formatoFecha(fechaInicial), this.formatoFecha(fechaFinal) , clv_benefi, ejercicio, tipoGasto, idUsuario, verUnidad, tipo, numop,  privilegio, capitulo);
	}
	

}
