package com.edu.proyecto.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import com.edu.proyecto.models.entity.Firma;
import com.edu.proyecto.models.entity.ReciboC;
import com.edu.proyecto.models.entity.TipoDocumento;
import com.edu.proyecto.models.entity.Usuario;
import com.edu.proyecto.models.entity.Rol;
import com.edu.proyecto.models.services.IFirmaService;
import com.edu.proyecto.models.services.IRolService;
import com.edu.proyecto.models.services.ITipoDocumentoService;
import com.edu.proyecto.models.services.IUsuarioService;
import com.edu.proyecto.util.paginator.PageRender;

@Controller
//@SessionAttributes("usuario")
public class UsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IFirmaService firmaService;

	@Autowired
	private IRolService rolService;

	@Autowired
	private ITipoDocumentoService tipodocumentoservice;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Value("${application.controllers.mensaje}")
	private String mensaje;

//	private String molestia;

	@GetMapping("/")
	public String inicio(Model model, HttpSession session, Authentication auten, HttpServletRequest request) {
		model.addAttribute("mensaje", mensaje);
		if (auten == null) {
			model.addAttribute("mensaje", mensaje);
			return "inicio";
		}
		if (auten != null) {
			Usuario usuario = usuarioService.findByUsername(auten.getName());
			log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
			// System.out.print("ESTA ES EL ID MIRAME " + emailadmin);
			Firma firma = firmaService.findByUsuario(usuario);
			if (firma != null) {
				if (firma.getUsuario() != null) {
					session.setAttribute("regfit", false);
				}
			} else {
				session.setAttribute("regfit", true);
			}
			if (request.isUserInRole("ROLE_INIT_ADMIN")) {
				return "redirect:/nuevapass";
			}
			if (request.isUserInRole("ROLE_INIT_USER")) {
				return "redirect:/nuevapass";
			}
		}
		return "inicio";
	}

	@RequestMapping(value = "/nuevapass")
	public String crearfirma(Map<String, Object> model, Authentication auten) {
		Usuario usuario = usuarioService.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		// System.out.print("ESTA ES EL ID MIRAME " + emailadmin);
		return "nuevapass";
	}

	@RequestMapping(value = "/nuevapass", method = RequestMethod.POST)
	public String password(@RequestParam(name = "password") String password,
			@RequestParam(name = "repass") String repass, Model model, Authentication auten, HttpSession session,
			RedirectAttributes flash, BCryptPasswordEncoder passEncoder) {
		Usuario usuario = usuarioService.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID FIRMA " + usuario.getIdusuario());
		System.out.print("CODIGO FIRMA  " + password);
		System.out.print("  RE FIRMA  " + repass + "   ");
//		List<Rol> rol = rolService.findOneRol(rol);

		if (password.equals(repass)) {
			usuario.setPassword(passEncoder.encode(password));
			if (usuario.getRol().getIdrol() == 3) {
				System.out.print("PASO");
				usuario.setRol(null);
				Rol rol = new Rol();
				rol.setIdrol(1L);
				rol.setDescripcion("Administrador");
				usuario.setRol(rol);
			}
			if (usuario.getRol().getIdrol() == 4) {
				System.out.print("PASO");
				usuario.setRol(null);
				Rol rol = new Rol();
				rol.setIdrol(2L);
				rol.setDescripcion("Usuario");
				usuario.setRol(rol);
			}
			usuarioService.save(usuario);
			String mensajeflash = "Se registro con exito";
			flash.addFlashAttribute("success", mensajeflash);

			return "inicio";
		} else {
			System.out.print("ERROR");
			String mensajerror = "REVISE LOS VALORES INGRESADOS";
			flash.addFlashAttribute("error", mensajerror);
			return "redirect:/nuevapass";

		}
	}

	@GetMapping("/generarecibos")
	public String generarecibos(Model model) {
		model.addAttribute("molestia", mensaje);
		return "generarecibos";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication) {
		if (authentication != null) {
			log.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			log.info(
					"Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): Usuario autenticado: "
							.concat(auth.getName()));
		}
		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Usuario> usuarios = usuarioService.findAll(pageRequest);
		PageRender<Usuario> pageRender = new PageRender<Usuario>("/listar", usuarios);
		model.addAttribute("titulo", "Listado de usuarios");
		model.addAttribute("usuarios", usuarioService.findAll());
		model.addAttribute("page", pageRender);
		return "listar";
	}
	/// verperfil

	@RequestMapping(value = "/verperfil")
	public String verperfil(Authentication auten, Map<String, Object> model, RedirectAttributes flash) {
		// Usuario usuario = new Usuario();

		Usuario usuario = usuarioService.findByUsername(auten.getName());
		log.info(auten.getName() + " id " + usuario.getIdusuario());
		System.out.print("ESTA ES EL ID MIRAME " + usuario.getIdusuario());

		usuarioService.findOne(usuario.getIdusuario());
		System.out.print(usuario.getIdusuario());

		model.put("usuario", usuario);
		model.put("titulo", "Detalle usuario: " + usuario.getNombre());
		return "/verperfil";
	}

	public static Long idusuario;

	@GetMapping(value = "/verusuario/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		// Usuario usuario = new Usuario();

		Usuario usuario = usuarioService.findOne(id);
		idusuario = id;
		System.out.print(idusuario);
		if (usuario == null) {
			flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
			return "listar";
		}
		model.put("usuario", usuario);
		model.put("titulo", "Detalle usuario: " + usuario.getNombre());
		return "verusuario";
	}

	public Long idusuariomail;
	public String mailnuevo;
	public String passnuevo;
	public String usuarionuevo;
	public String passnuevamail;

	List<Rol> arrayrolactivo = new ArrayList<Rol>();

	@RequestMapping(value = "/form")	
	public String crear(Model model) {
		Usuario usuario = new Usuario();
		arrayrolactivo.clear();
		List<TipoDocumento> findAll = tipodocumentoservice.findAll();
		List<Rol> findAllRol = rolService.findAllRol();

		for (Rol listarolactivo : findAllRol) {

			if (listarolactivo.getVisible() == 1) {
				arrayrolactivo.add(listarolactivo);
			}
		}
		model.addAttribute("titulo", "Formulario de usuario");
		model.addAttribute("usuario", usuario);
		model.addAttribute("tipodocumento", findAll);
		model.addAttribute("rol", arrayrolactivo);
//		mailnuevo = usuario.getEmail();
//		passnuevo = usuario.getContrasena();
//		enviarmailpass();

		return "form";
	}

	@RequestMapping(value = "/form/{idusuario}")
	public String editar(@PathVariable(value = "idusuario") Long idusuario, RedirectAttributes flash,
			Map<String, Object> model) {

		Usuario usuario = null;
		List<TipoDocumento> findAll = tipodocumentoservice.findAll();
		List<Rol> findAllRol = rolService.findAllRol();

		for (Rol listarolactivo : findAllRol) {

			if (listarolactivo.getVisible() == 1) {
				arrayrolactivo.add(listarolactivo);
			}
		}

		if (idusuario > 0) {
			usuario = usuarioService.findOne(idusuario);
			if (usuario == null) {
				flash.addFlashAttribute("error", "El id de usuario no puede ser cero!");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
			return "redirect:/listar";
		}
		model.put("usuario", usuario);
		model.put("tipodocumento", findAll);
		model.put("rol", arrayrolactivo);
		model.put("titulo", "Editar usuario");

		return "/form";
	}

	public static Long maxUsuario;
	

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Usuario usuario, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status, BCryptPasswordEncoder passEncoder) {
		Usuario usuarioRep=usuarioService.findByUsername(usuario.getUsername());
		List<TipoDocumento> findAll = tipodocumentoservice.findAll();
		List<Rol> findAllRol = rolService.findAllRol();
		model.addAttribute("tipodocumento", findAll);
		model.addAttribute("rol", arrayrolactivo);
		if(usuarioRep!= null) {
			model.addAttribute("error","El valor del campo usuario esta repetido");
			return "form";
		}
		
		if (result.hasErrors()) {
			model.addAttribute("tipodocumento", findAll);
			model.addAttribute("rol", findAllRol);
			model.addAttribute("titulo", "Formulario de usuario");
			return "form";
		}
		String uniqueFilename = UUID.randomUUID().toString();
		passnuevamail = uniqueFilename;
		flash.addFlashAttribute("info", "Se ha enviado mail de notificacion '");
		usuario.setPassword(passEncoder.encode(uniqueFilename));
		usuario.getIdusuario();
//		usuario.setPassword(passEncoder.encode(password));

		String mensajeflash = (usuario.getIdusuario() != null) ? "Usuario editado con exito!"
				: "Usuario creado con exito!";
		
		usuarioService.save(usuario);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeflash);

		System.out.print("NUEVO USUARIO " + usuario.getIdusuario());
		idusuariomail = usuario.getIdusuario();
		mailnuevo = usuario.getEmail();
		passnuevo = usuario.getPassword();
		usuarionuevo = usuario.getUsername();

		enviarmailpass();
		return "redirect:listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			usuarioService.delete(id);
		}
		flash.addFlashAttribute("success", "Usuario eliminado con exito!");
		return "redirect:/listar";
	}

	/*--------------------------------------------------------------------*/
	/*--------------------------------------------------------------------*/

//Hacer que el formulario funcione comun, setear la firma en VER{ID} y pasarlo en el formulario en un firma.setidusuario;

	public String enviarmailpass() {
		Usuario usuario = new Usuario();
		List<Usuario> findAllUse = usuarioService.findAll();
		System.out.println("Hello World!");
		Properties propiedad = new Properties();
		propiedad.setProperty("mail.smtp.host", "smtp.gmail.com");
		propiedad.setProperty("mail.smtp.starttls.enable", "true");
		propiedad.setProperty("mail.smtp.port", "587");
		propiedad.setProperty("mail.smtp.auth", "true");

//      mailnuevo = usuario.getEmail();
//		passnuevo = usuario.getContrasena();
		Session sesion = Session.getDefaultInstance(propiedad);

		String correoEnvia = "hedlanrecibos@gmail.com";// tu correo gmaildesde donde se envia
		String contrasena = "lanabanana";// tu contraseña de acceso a gmaila esa cuenta

		// String receptor = mailnuevo; // cuenta que recibe
		String receptor = "jimena.villca2@gmail.com";
		String asunto = "Bienvenido al sistema de RECIBOS HEDLAN";

		String mensajeuno = "<h3>Estimado  " + usuarionuevo + "</h3><br> "
				+ " <h4><br>Se acaba crear su usuario en el sistema HEDLAN, sistema de recibos electronico, para poder activar su usuario le pedimos que ingrese al sistema con las siguientes credenciales ";
		String mensajetres = "sus credenciales son:  "

				+ "<blockquote> USUARIO " + usuarionuevo + " <br> CONTRASEÑA " + passnuevamail
				+ "</blockquote><br>su contraseña es temporal, ingrese al sistema para cambiar su password </h4><br><hr style=\\\"width:100%;\\\">\\r\\n <h3>SISTEMA HELDAN</h3>";


		String mensaje = new String(mensajeuno + mensajetres);

		MimeMessage mail = new MimeMessage(sesion);
		try {
			mail.setContent(mensaje, "text/html; charset=utf-8");
			mail.setFrom(new InternetAddress(correoEnvia));
			mail.addRecipient(Message.RecipientType.TO, new InternetAddress(receptor));
			mail.setSubject(asunto);
//			mail.setText(mensaje);

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

		return "listar";
	}

}
