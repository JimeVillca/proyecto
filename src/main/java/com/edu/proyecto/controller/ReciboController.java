package com.edu.proyecto.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;


import com.edu.proyecto.models.entity.Archivo;
import com.edu.proyecto.models.entity.Categoria;
import com.edu.proyecto.models.entity.Firma;
import com.edu.proyecto.models.entity.Recibo;
import com.edu.proyecto.models.entity.ReciboC;
import com.edu.proyecto.models.entity.Usuario;
import com.edu.proyecto.models.services.IArchivoService;
import com.edu.proyecto.models.services.IFirmaService;
import com.edu.proyecto.models.services.IReciboService;
import com.edu.proyecto.models.services.IUsuarioService;
import com.edu.proyecto.models.services.IReciboCService;
import com.edu.proyecto.util.paginator.PageRender;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.edu.proyecto.SpringSecuityConfig;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

/*<span sec:authentication="name"></span></a>*/

@Controller

public class ReciboController {

	public static String archno;

	// private static final String SAMPLE_CSV_FILE_PATH = "uploads\\"+archno;

	@Autowired
	private IReciboService recservice;

	@Autowired
	private IReciboCService receservice;

	@Autowired
	private IArchivoService archivoservice;

	@Autowired
	private IUsuarioService usuarioservice;
	
	@Autowired
	private IFirmaService firmaservice;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping(value = "/verecibo/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		ReciboC reciboc = receservice.findOne(id);
		if (reciboc == null) {
			flash.addFlashAttribute("error", "La factura no existe en la base de datos!");
			return "redirect:/listarecibos";
		}

		model.addAttribute("reciboc", reciboc);
		return "verecibo";
	}
	
	@GetMapping(value = "/verecibousuario/{id}")
	public String verecibousuario(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		ReciboC reciboc = receservice.findOne(id);
		if (reciboc == null) {
			flash.addFlashAttribute("error", "La factura no existe en la base de datos!");
			return "redirect:/listarecibos";
		}
		//model.addAttribute("firma", reciboc.getUsuario().get);

		model.addAttribute("reciboc", reciboc);
		return "verecibousuario";
	}

	public static Long idusuarioset;
	
	List<ReciboC> arrayrecibosinfirmausuario = new ArrayList<ReciboC>();

	@RequestMapping(value = "/listarecibosusuario")
	public String listarecibosusuario(Map<String, Object> model, Authentication auten, RedirectAttributes flash) {
		arrayrecibosinfirmausuario.clear();
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " id " + usuario.getIdusuario());
		System.out.print("ESTA ES EL ID MIRAME " + idusuarioset);
		List<ReciboC> reciboc = receservice.findByIdUsuario(usuario);
		if(!reciboc.isEmpty()) {
	System.out.println("idrecibo : " + reciboc.get(0));
}
		
//		 System.out.println("" : " + recibo.getConcepto());
		

		model.put("reciboc", reciboc);
		model.put("titulo", "Lista recibos");
		return "listarecibosusuario";
	}
	
	public static String usuariocorreo;
	
	public static String clavegenerada;
	
	public String nombreusuario;
	
	@RequestMapping(value = "/primerainstanciaindividual", method = RequestMethod.GET)
	public String primerainstanciaindividual(Model model, Authentication auten,String name, String value, HttpSession session ) {
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " id " + usuario.getIdusuario());
		usuariocorreo = usuario.getEmail();
		nombreusuario = usuario.getUsername();
		if(session.getAttribute("name") == null) {
			String uniquename = UUID.randomUUID().toString();
			session.setAttribute("name", uniquename);
			clavegenerada = uniquename;
			
			enviomailfirmaautogenerada();
		}		
		else {
			System.out.println("NAME SESSION "+ session.getAttribute("name"));
			return "primerainstanciafirmasiva";
		}
//		generarcodigo(model, session);		
		System.out.println("NAME SESSION "+ session.getAttribute("name"));		
		model.addAttribute("mensaje", mensaje);
		return "primerainstanciafirmasiva";
	}
		
//CONFIRMIDAD ESTADO --->>>  0-PENDIENTE DE FIRMA USUARIO 1 - NO CONFORME - 2-CONFORME
	public Long conformidades;
	
	@RequestMapping(value = "/firmaindividual", method = RequestMethod.POST)
	public String firmaindividual(@RequestParam(name = "conformidad") String conformidad,Model model, Authentication auten,HttpSession session,RedirectAttributes flash) {
		session.setAttribute("name", null);
		Usuario usuario = usuarioservice.findByUsername(auten.getName());

		if (conformidad.equals("Conformidad")) {
			conformidades =  2L;
			System.out.println("ESTADO CONFORMIDAD " + conformidades);
		}
		if(session.getAttribute("name") == null) {
			String uniquename = UUID.randomUUID().toString();
			session.setAttribute("name", uniquename);
			
			usuariocorreo = usuario.getEmail();
			nombreusuario = usuario.getUsername();
			clavegenerada = uniquename;

			enviomailfirmaautogenerada();
		}		
		else {
			System.out.println("NAME SESSION "+ session.getAttribute("name"));
			return "firmausuario";
		}
//		generarcodigo(model, session);		
		System.out.println("NAME SESSION USUARIO "+ session.getAttribute("name"));		
		model.addAttribute("mensaje", mensaje);
		return "/firmausuario";
		
	}
	
	@RequestMapping(value = "/firmausuario", method = RequestMethod.POST)
	public String firmausuario(@RequestParam(name = "firmautogeneradas") String firmautogeneradas,@RequestParam(name = "firma") String firma,Model model, Authentication auten,HttpSession session,RedirectAttributes flash) {
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		iduse = usuario.getIdusuario();
		//System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		Firma firma2 = firmaservice.findByUsuario(usuario);
		List<ReciboC> reciboc = receservice.findAllRec();
		
		if(session.getAttribute("name").equals(firmautogeneradas) && firma2.getFirma().equals(firma)){
			for (ReciboC reciboC2 : reciboc) {

				if (reciboC2.getEstado() == 2) {
					reciboC2.setEstado(3);
					reciboC2.setIdfirmausuario(usuario.getIdusuario());
					reciboC2.setConformidad(conformidades);
					arrayrecibosinfirma.add(reciboC2);
					receservice.save(reciboC2);

				}

			}

			String mensajexito = "Se ha valido con exito ";
			flash.addFlashAttribute("success", mensajexito);
			
			return "redirect:/";
		}else {
			System.out.println("FIRMA INGRESADA"+firmautogeneradas);

			String mensajerror = "Error en el codigo ingresado";
			flash.addFlashAttribute("error", mensajerror);
			return "redirect:/listarecibosusuario";
		
		}
		
	}
	
	@GetMapping(value = "/insertaregistros/{nombre}")
	public String insertar(@PathVariable(value = "nombre") String nombre, Map<String, Object> model,
			RedirectAttributes flash) throws IOException {
		System.out.print(nombre);
		// Archivo archivo = archivoservice.findOneArch(id);
		archno = nombre;
		System.out.print(archno);
		String SAMPLE_CSV_FILE_PATH = "uploads\\" + archno;
		File file = new File(SAMPLE_CSV_FILE_PATH);
		String path = file.getAbsolutePath();
		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));) {
		 CsvToBean<Recibo> csvToBean = new CsvToBeanBuilder(reader).withType(Recibo.class).withIgnoreLeadingWhiteSpace(true).build();
		 List<Recibo> lista = new ArrayList<Recibo>();
		 Iterator<Recibo> reciboIterator = csvToBean.iterator();

			while (reciboIterator.hasNext()) {
				Recibo recibo = reciboIterator.next();
				/*
				 * System.out.println("idrecibo : " + recibo.getIdrecibo());
				 * System.out.println("concepto : " + recibo.getConcepto());
				 */
				lista.add(recibo);
				System.out.println("==========================");
			}
			for (Recibo recibo : lista) {
				System.out.println(recibo);
			}
			String mensajeflash = "Archivo insertado con exito!  " + archno;
			recservice.saveAll(lista);
			flash.addFlashAttribute("success", mensajeflash);
			return "redirect:/listararchivos";
		}

	}

	public static String emailadmin;

	List<ReciboC> arrayrecibosinfirma = new ArrayList<ReciboC>();
	
	public Long estadouni;
	
	@GetMapping(value = "/listarecibos/{estado}")
	public String listarecibos(@PathVariable(value = "estado") Long estado, Map<String, Object> model,
			RedirectAttributes flash, Authentication auten) throws IOException {
//		ReciboC reciboc = receservice.findOne(id);
		/* 1-SIN FIRMA 2-CON FIRMA ADMIN 3-CON FIRMA DE TODOS */
		
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " EMAIL - CORREO ELECTRONICO " + usuario.getEmail());
		System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		List<ReciboC> reciboc = receservice.findAllRec();
		arrayrecibosinfirma.clear();
//		PageRender<ReciboC> pageRender = new PageRender<ReciboC>("/listarecibos", reciboc);
		if (estado == 1) {
			for (ReciboC reciboC2 : reciboc) {

				if (reciboC2.getEstado() == 1) {
					arrayrecibosinfirma.add(reciboC2);
				}
			}
			model.put("estado", true);
			model.put("titulo", "Listado de recibo");
			model.put("reciboc", arrayrecibosinfirma);
			return "listarecibos";
		}
		model.put("estado",false);
		if (estado == 2) {
			List<ReciboC> arrayrecibos = new ArrayList<ReciboC>();

			for (ReciboC reciboC2 : reciboc) {
				if (reciboC2.getEstado() == 2) {
					arrayrecibos.add(reciboC2);

				}
			}
			model.put("titulo", "Listado de recibo");
			model.put("reciboc", arrayrecibos);
			return "listarecibos";
		}
		if (estado == 3) {
			List<ReciboC> arrayrecibos = new ArrayList<ReciboC>();
			for (ReciboC reciboC2 : reciboc) {
				if (reciboC2.getEstado() == 3) {
					arrayrecibos.add(reciboC2);

				}
			}

			model.put("titulo", "Listado de recibo");
			model.put("reciboc", arrayrecibos);
			return "listarecibos";
		}

		model.put("titulo", "Listado de recibo");
		model.put("reciboc", receservice.findAllRec());
//		model.addAttribute("page", pageRender);
		return "listarecibos";

	}
	@Value("${application.controllers.mensaje}")
	private String mensaje;
	
	
	public String generarcodigo(Model model,HttpSession session ) {
		String uniquename = UUID.randomUUID().toString();

		session.setAttribute("name", uniquename);
		
		return "name";
		
	}
	
	
	@RequestMapping(value = "/primerainstanciafirmasiva", method = RequestMethod.GET)
	public String primerainstanciafirmasiva(Model model, Authentication auten,String name, String value, HttpSession session ) {
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " id " + usuario.getIdusuario());
		usuariocorreo = usuario.getEmail();
		nombreusuario = usuario.getUsername();
		System.out.println("EMAIL "+ usuariocorreo);
		
		if(session.getAttribute("name") == null) {
			String uniquename = UUID.randomUUID().toString();
			session.setAttribute("name", uniquename);
			clavegenerada = (String) session.getAttribute("name") ;
			System.out.println("Clave AUTOGENERADA "+ clavegenerada);

			enviomailfirmaautogenerada();
			System.out.println("LLEGO Y MANDO MAIL");
		}	
		else {
			System.out.println("NAME SESSION "+ session.getAttribute("name"));
			
			System.out.println("Clave AUTOGENERADA "+ clavegenerada);
			enviomailfirmaautogenerada();
			return "primerainstanciafirmasiva";
		}

//		generarcodigo(model, session);		
		System.out.println("NAME SESSION "+ session.getAttribute("name"));		
		model.addAttribute("mensaje", mensaje);
		return "primerainstanciafirmasiva";
	}
	public Long iduse;
	@RequestMapping(value = "/firmamasiva", method = RequestMethod.POST)
	public String firmasiva(@RequestParam(name = "firmautogenerada") String firmautogenerada,@RequestParam(name = "firma") String firma ,Model model, Authentication auten,HttpSession session,RedirectAttributes flash) {
		Usuario usuario = usuarioservice.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		iduse = usuario.getIdusuario();
		//System.out.print("ESTA ES EL ID MIRAME " + emailadmin);		
		Firma firma1 = firmaservice.findByUsuario(usuario);
		List<ReciboC> reciboc = receservice.findAllRec();
		
		
		System.out.println("NAME SESSION2 "+ session.getAttribute("name"));
		System.out.println("FIRMA BASE DE DATOS "+firma1.getFirma());		
		System.out.println("FIRMA"+ firma);
		
		if(session.getAttribute("name").equals(firmautogenerada) && firma1.getFirma().equals(firma)){
			for (ReciboC reciboC2 : reciboc) {

				if (reciboC2.getEstado() == 1) {
					reciboC2.setEstado(2);
					reciboC2.setIdfirmadmin(usuario.getIdusuario());
					arrayrecibosinfirma.add(reciboC2);
					receservice.save(reciboC2);

				}

			}

			String mensajexito = "Se ha valido con exito ";
			flash.addFlashAttribute("success", mensajexito);
			
			return "redirect:/";
		
		}else {
			System.out.println("FIRMA INGRESADA"+firmautogenerada);

			String mensajerror = "Error en el codigo ingresado";
			flash.addFlashAttribute("error", mensajerror);
			return "redirect:/listarecibos/1";

		}
		

	}

	public String enviomailfirmaautogenerada() {
		System.out.println("LLEGO Y MANDO MAIL");

			System.out.println( "Hello World!" );
	        Properties propiedad = new Properties();
	        propiedad.setProperty("mail.smtp.host", "smtp.gmail.com");
	        propiedad.setProperty("mail.smtp.starttls.enable", "true");
	        propiedad.setProperty("mail.smtp.port", "587");
	        propiedad.setProperty("mail.smtp.auth", "true");
	        
//	      mailnuevo = usuario.getEmail();
//			passnuevo = usuario.getContrasena();
	        Session sesion = Session.getDefaultInstance(propiedad);
	        
	        String correoEnvia = "hedlanrecibos@gmail.com";//tu correo gmaildesde donde se envia 
	        String contrasena = "lanabanana";//tu contraseña de acceso a gmaila esa cuenta
	        
	        String receptor = usuariocorreo; //cuenta que recibe
	      	            
	        String asunto = "RECIBOS HEDLAN - Firma autogenerada";
	                
	        String mensajeuno = "<h3>Estimado  " + nombreusuario + "</h3><br> "
					+ " <h4><br>Se acaba generar una firma, la misma deber ser ingresada en la aplicacion para poder finalidar la firma de su recibo"

					+ "<blockquote> Firma autogenerada    " + clavegenerada 
					+ "</blockquote><br> </h4><br><hr style=\\\"width:100%;\\\">\\r\\n <h3>SISTEMA HELDAN</h3>";


			String mensaje = new String(mensajeuno);
	        
	        MimeMessage mail = new MimeMessage(sesion);
	        try {
	        	mail.setContent(mensaje, "text/html; charset=utf-8");
				mail.setFrom(new InternetAddress(correoEnvia));
				mail.addRecipient(Message.RecipientType.TO, new InternetAddress(receptor));
				mail.setSubject(asunto);
//				mail.setText(mensaje);

				Transport transportar = sesion.getTransport("smtp");
				transportar.connect(correoEnvia, contrasena);
				transportar.sendMessage(mail, mail.getRecipients(Message.RecipientType.TO));
				transportar.close();

	    	} catch (AddressException ex) {
				Logger.getLogger(ex.toString());
				System.err.println(ex);
			} catch (MessagingException ex) {
				Logger.getLogger(ex.toString());
				System.err.println(ex);
			}

			Logger.getLogger("Enviado");

			return "/";
	}
}