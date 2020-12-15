package com.edu.proyecto.controller;

import java.security.Principal;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.logging.Logger;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.proyecto.models.entity.Rol;
import com.edu.proyecto.models.entity.TipoDocumento;
import com.edu.proyecto.models.entity.Usuario;
import com.edu.proyecto.models.services.IFirmaService;
import com.edu.proyecto.models.services.IRolService;
import com.edu.proyecto.models.services.ITipoDocumentoService;
import com.edu.proyecto.models.services.IUsuarioService;

@Controller
@SessionAttributes("usuario")
public class LoginController {

	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IFirmaService firmaService;

	@Autowired
	private IRolService rolService;

	@Autowired
	private ITipoDocumentoService tipodocumentoservice;
	
	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	
	@GetMapping ("/login")
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required = false) String logout,
			Model model, Principal principal, RedirectAttributes flash,Authentication auten,HttpServletRequest request) {
		
		if(principal != null) {
			flash.addFlashAttribute("info", "Ya ha inciado sesión anteriormente");
			
//				Usuario usuario = usuarioService.findByUsername(auten.getName());
//				log.info(auten.getName() + " ID - ID usuario " + usuario.getIdusuario());

			}
				
//		}
		log.info("Se salto el principal");
		if(error != null) {
			model.addAttribute("error", "Error en el login: Nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo!");
	//		System.out.println("USUARIO "+principal);
	//		System.out.println("ROL"+ auten.getAuthorities());
	//		System.out.println("ROL"+ request.isUserInRole);
		}
		
		if(logout != null) {
			model.addAttribute("success", "Ha cerrado sesión con éxito!");
		}
				
		return "login";
	}
	
	//Public void validaractivo();
	
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@RequestMapping(value="/nuevacontrasena")
	public String nuevacontrasena(@PathVariable(value="passuno") String passuno,@PathVariable(value="passdos") String passdos,RedirectAttributes flash, Map<String, Object> model,Authentication auten,AuthenticationManagerBuilder builder) {
		Usuario usuario = usuarioService.findByUsername(auten.getName());
		log.info(auten.getName() + " ID - ID usuario " + usuario.getIdusuario());
		
		if(passuno != passdos) {
			model.put("error", "Error los valores en los campos no coiciden");

		}
		else {
			String passencriptada = passEncoder.encode(passuno);
			System.out.println("LA NUEVA CONTRASENA ENCRIPTADA ES "+passencriptada);
			usuario.setPassword(passencriptada);
			usuarioService.save(usuario);
		}
		return "redirect:/";

	}

	@RequestMapping(value = "/olvidepassword")
	public String oldpass(Model model) {
		

		
		System.out.println("ENTRA AL FORMULARIO INICIAL DE OLVIDE PASSWORD");
		return "olvidepassword";
	}
	public String mailresetear;
	public String passresetear;
	public String usuarioresetear;
	@RequestMapping(value = "/olvidepassword", method = RequestMethod.POST)
	public String olvidepassword(@RequestParam(name = "correo") String correo,Model model, Authentication auten, HttpSession session,
		RedirectAttributes flash, BCryptPasswordEncoder passEncoder) {
		
		System.out.println("ENTRA A LA SEGUNDA OLVIDE PASSWORD");
		Usuario usuario =usuarioService.findByEmail(correo);
		System.out.print("CORREO  " + correo);

		if(correo== null) {
			model.addAttribute("error", "El campo esta vacio");
		}
					
		
		if(correo.equals(usuario.getEmail())) {
			String uniqueFilename = UUID.randomUUID().toString();
			passresetear = uniqueFilename;
			model.addAttribute("success", "Se envio con exito la nueva contraseña a su mail");
			usuario.setPassword(passEncoder.encode(uniqueFilename));
			mailresetear = usuario.getEmail();
			usuarioresetear = usuario.getUsername();
//Se maneja el envio de mail y se cambia la pass y el rol del usuario por el que sea init, redireccionar a NUEVAPASS.		
			System.out.println("ROL "+ usuario.getRol());		
			
			if(usuario.getRol().getIdrol()== 1) {
				usuario.setRol(null);
				Rol rol = new Rol();
				rol.setIdrol(3L);
				rol.setDescripcion("Rol Administrador");
				usuario.setRol(rol);
			}
			
			if(usuario.getRol().getIdrol()== 2) {
				usuario.setRol(null);
				Rol rol = new Rol();
				rol.setIdrol(4L);
				rol.setDescripcion("Rol Usuario");
				usuario.setRol(rol);
			}
			model.addAttribute("success", "Se envio con exito la nueva contraseña a su mail");
			enviarpasswordreseteada();
			
		}else {
			model.addAttribute("error", "El valor no tiene un usuario relacionado");

		}
			return "redirect:/";
	}
	
	public String enviarpasswordreseteada() {
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
		String receptor = mailresetear;
		String asunto = "Bienvenido al sistema de RECIBOS HEDLAN";

		String mensajeuno = "<h3>Estimado  " + usuarioresetear + "</h3><br> "
				+ " <h4><br>Se acaba blanquear contraseña en el sistema HEDLAN, sistema de recibos electronico, para poder activar su usuario le pedimos que ingrese al sistema con las siguientes credenciales ";
		String mensajetres = "sus credenciales son:  "

				+ " <br> CONTRASEÑA " + passresetear 
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
