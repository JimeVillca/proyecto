package com.edu.proyecto.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.proyecto.models.entity.Categoria;
import com.edu.proyecto.models.entity.Firma;
import com.edu.proyecto.models.entity.ReciboC;
import com.edu.proyecto.models.entity.TipoDocumento;
import com.edu.proyecto.models.entity.Usuario;
import com.edu.proyecto.models.services.IFirmaService;
import com.edu.proyecto.models.services.IUsuarioService;
import com.edu.proyecto.util.paginator.PageRender;

@Controller
@SessionAttributes("firma")
public class FirmaController {
	
	@Autowired
	private IFirmaService firmaService;
	
	@Autowired
	private IUsuarioService usuarioservice;

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	@RequestMapping(value = "/listarfirma", method = RequestMethod.GET)
	public String listarfirma(@RequestParam(name="page", defaultValue="0") int page,Model model) {
		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Firma> firma = firmaService.findAllFirma(pageRequest);
		
		PageRender<Firma> pageRender = new PageRender<Firma>("/listarfirma", firma);
				
		model.addAttribute("titulo", "Listado de firma");
		model.addAttribute("firma", firmaService.findAllFirma());
		model.addAttribute("page", pageRender);
		return "listar";
		
	}

	
	@RequestMapping(value="/eliminarfirma/{usuarioid}")
	public String eliminar(@PathVariable(value="usuarioid") Long usuarioid,RedirectAttributes flash) {
		
		if(usuarioid > 0) {
			firmaService.delete(usuarioid);
			flash.addFlashAttribute("success", "Firma eliminado con éxito!");
		}
		return "redirect:/listar";
	}
	
	@RequestMapping(value = "/registrarfirma")
	public String crear( Model model,Authentication auten, HttpSession session ) {
				
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		//System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		Firma firma = firmaService.findByUsuario(usuario);

	//	if(session.getAttribute("regfir") == null) {
			//session.setAttribute("regfir", uniquename);		
	//	}
		
	//	if(firma.getUsuario().equals(usuario.getIdusuario())) {
	//	session.setAttribute("regfit", true);	
	//	}else {
	//	session.setAttribute("regfit", false);	
	//	}
			
		return "/";
	}
	@RequestMapping(value = "/formfirma")
	public String crearfirma(Map<String, Object> model,Authentication auten) {
		
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());

		//System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		Firma firma2 = firmaService.findByUsuario(usuario);
		
		System.out.print(usuario);
		Firma firma = new Firma();
		//firma.setUsuario(idusuario);

		model.put("firma", firma);
		model.put("titulo", "Formulario de firma");
		return "formfirma";
		
	}
	/* System.out.println("idrecibo : " + recibo.getIdrecibo());
	 * System.out.println("concepto : " + recibo.getConcepto()); */
	
	@RequestMapping(value = "/formfirma", method = RequestMethod.POST)
	public String formfirma(@RequestParam(name = "codigofirma") String codigofirma,@RequestParam(name = "refirma") String refirma,Model model, Authentication auten,HttpSession session,RedirectAttributes flash) {

		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		Firma firma = new Firma();
		System.out.print("CODIGO FIRMA  " + codigofirma );
		System.out.print("  RE FIRMA  " + refirma +"   ");

		if(codigofirma.equals(refirma)) {
			firma.setUsuario(usuario);
			firma.setFirma(codigofirma);
			firmaService.save(firma);
			String mensajeflash = "Se registro con exito la firma";
			flash.addFlashAttribute("success", mensajeflash);

			return "redirect:/";
		}
		else{
			String mensajerror = "REVISE LOS VALORES INGRESADOS";
			flash.addFlashAttribute("error", mensajerror);
			return "redirect:/formfirma";

		}
		
	}
	
	@RequestMapping(value = "/blanqueofirma")
	public String blanqueofirma( Model model,Authentication auten, HttpSession session ) {
				
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		//System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		Firma firma = firmaService.findByUsuario(usuario);

		firmaService.delete(usuario.getIdusuario());
	//	if(session.getAttribute("regfir") == null) {
			//session.setAttribute("regfir", uniquename);		
	//	}
		
	//	if(firma.getUsuario().equals(usuario.getIdusuario())) {
	//	session.setAttribute("regfit", true);	
	//	}else {
	//	session.setAttribute("regfit", false);	
	//	}
			
		return "/";
	}
}
