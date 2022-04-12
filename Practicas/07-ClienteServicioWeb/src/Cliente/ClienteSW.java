import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Base64;
import java.lang.reflect.Type;
import com.google.gson.*;

// Comando para compilar este programa
// javac -cp gson-2.3.1.jar ClienteSW.java

// Comando para ejecutar este programa en Windows
// java -cp gson-2.3.1.jar;. ClienteSW

//   Clase AdaptadorGsonBase64
//   Adaptador GSON para serializar/deserializar byte[] como base 64
//   Ver: https://sites.google.com/site/gson/gson-user-guide
//   Carlos Pineda Guerrero, marzo 2022.

class AdaptadorGsonBase64 implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
	public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
	}

	public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		// jax-rs reemplaza cada "+" por " ", pero el decodificador Base64 no reconoce " "
		String s = json.getAsString().replaceAll("\\ ", "+");
		return Base64.getDecoder().decode(s);
	}
}

class Usuario {
	String email;
	String nombre;
	String apellido_paterno;
	String apellido_materno;
	String fecha_nacimiento;
	String telefono;
	String genero;
	byte[] foto;
}

class ClienteSW {
	static String ip = "20.228.172.140";

	public static void main(String[] args) throws Exception {
		while (true) {
			System.out.println("\nMENU");
			System.out.println("a. Alta usuario");
			System.out.println("b. Consulta usuario");
			System.out.println("c. Borra usuario");
			System.out.println("d. Salir");
			System.out.print("Opcion: ");

			String op = System.console().readLine();
			Usuario usuario = new Usuario();

			switch (op) {
				case "a":
					System.out.println("\n\tAlta usuario\n");

					System.out.print("Email: ");
					usuario.email = System.console().readLine();

					System.out.print("Nombre: ");
					usuario.nombre = System.console().readLine();

					System.out.print("Apellido Paterno: ");
					usuario.apellido_paterno = System.console().readLine();

					System.out.print("Apellido Materno: ");
					usuario.apellido_materno = System.console().readLine();

					System.out.print("Fecha de nacimiento (YYYY-MM-DD) : ");
					usuario.fecha_nacimiento = System.console().readLine();

					System.out.print("Telefono: ");
					usuario.telefono = System.console().readLine();

					System.out.print("Genero (M/F): ");
					usuario.genero = System.console().readLine();

					altaUsuario(usuario);

					break;
				case "b":
					System.out.println("\n\tConsulta usuario\n");

					System.out.print("Ingresa el email del usuario: ");
					String emailConsulta = System.console().readLine();
					consultarUsuario(emailConsulta);

					break;
				case "c":
					System.out.println("\n\tBorra usuario\n");

					System.out.print("Ingresa el email del usuario: ");
					String borrarEmail = System.console().readLine();
					borrarUsuario(borrarEmail);

					break;
				case "d":
					System.exit(0);
					break;
				default:
					System.out.println("Debe elegir alguna de las opciones del menu");
					break;
			}

		}
	}

	public static void altaUsuario(Usuario usuario) throws Exception {
		URL url = new URL("http://" + ip + ":8080/Servicio/rest/ws/alta_usuario");
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);
		conexion.setRequestMethod("POST");
		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		Gson g = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
		String parametros = "usuario=" + URLEncoder.encode(g.toJson(usuario), "UTF-8");
		OutputStream salida = conexion.getOutputStream();
		salida.write(parametros.getBytes());
		salida.flush();

		if (conexion.getResponseCode() == 200) {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
			String res;
			System.out.println("\n\tOK. Usuario registrado exitosamente");
			while ((res = buffer.readLine()) != null){
				System.out.println("\n\t" + res);
			}
			
		} else {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String res;
			while ((res = buffer.readLine()) != null) {
				System.out.println("\n\t" + res);
			}
		}
		conexion.disconnect();
	}

	public static void consultarUsuario(String email) throws Exception {
		URL url = new URL("http://" + ip + ":8080/Servicio/rest/ws/consulta_usuario");
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);
		conexion.setRequestMethod("POST");
		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String parametros = "email=" + URLEncoder.encode("" + email, "UTF-8");
		OutputStream salida = conexion.getOutputStream();
		salida.write(parametros.getBytes());
		salida.flush();

		if (conexion.getResponseCode() == 200) {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

			String res;
			Usuario userAux = new Usuario();
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

			while ((res = buffer.readLine()) != null) {
				userAux = (Usuario) gson.fromJson(res, Usuario.class);
				System.out.println("\nEmail: " + userAux.email);
				System.out.println("Nombre: " + userAux.nombre);
				System.out.println("Apellido paterno: " + userAux.apellido_paterno);
				System.out.println("Apellido materno: " + userAux.apellido_materno);
				System.out.println("Fecha de nacimiento: " + userAux.fecha_nacimiento);
				System.out.println("Telefono: " + userAux.telefono);
				System.out.println("Genero: " + userAux.genero);

			}

			System.out.print("\n\tDesea modificar los datos del usuario? (s/n): ");
			String op = System.console().readLine();

			switch (op) {
				case "s":
					Usuario usuario = new Usuario();

					usuario.email = userAux.email;

					System.out.print("\nNombre: ");
					usuario.nombre = System.console().readLine();
					if (usuario.nombre == null || usuario.nombre.equals("")) {
						usuario.nombre = userAux.nombre;
					}

					System.out.print("Apellido Paterno: ");
					usuario.apellido_paterno = System.console().readLine();
					if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals("")) {
						usuario.apellido_paterno = userAux.apellido_paterno;
					}

					System.out.print("Apellido Materno: ");
					usuario.apellido_materno = System.console().readLine();
					if (usuario.apellido_materno == null || usuario.apellido_materno.equals("")) {
						usuario.apellido_materno = userAux.apellido_materno;
					}

					System.out.print("Fecha de nacimiento: ");
					usuario.fecha_nacimiento = System.console().readLine();
					if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals("")) {
						usuario.fecha_nacimiento = userAux.fecha_nacimiento;
					}

					System.out.print("Telefono: ");
					usuario.telefono = System.console().readLine();
					if (usuario.telefono == null || usuario.telefono.equals("")) {
						usuario.telefono = userAux.telefono;
					}

					System.out.print("Genero (M/F): ");
					usuario.genero = System.console().readLine();
					if (usuario.genero == null || usuario.genero.equals("")) {
						usuario.genero = userAux.genero;
					}

					modificarUsuario(usuario);

					break;
				case "n":
					System.out.println("\n\tNo se realizaron modificaciones al usuario");
					break;
				default:
					System.out.println("\n\tDebe elegir una de las dos opciones (s/n)");
					break;
			}

		} else {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String res;
			while ((res = buffer.readLine()) != null){
				System.out.println("\n\t" + res);
			}
		}
		conexion.disconnect();
	}

	public static void modificarUsuario(Usuario usuario) throws Exception {
		URL url = new URL("http://" + ip + ":8080/Servicio/rest/ws/modifica_usuario");
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);
		conexion.setRequestMethod("POST");
		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		Gson g = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
		String parametros = "usuario=" + URLEncoder.encode(g.toJson(usuario), "UTF-8");
		OutputStream salida = conexion.getOutputStream();
		salida.write(parametros.getBytes());
		salida.flush();

		if (conexion.getResponseCode() == 200) {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
			String res;
			System.out.println("\n\tOK. El usuario se modifico correctamente");
			while ((res = buffer.readLine()) != null) {
				System.out.println("\n\t" + res);
			}
		} else {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String res;
			while ((res = buffer.readLine()) != null) {
				System.out.println("\n\t" + res);
			}
		}
		conexion.disconnect();
	}

	public static void borrarUsuario(String email) throws Exception {
		URL url = new URL("http://" + ip + ":8080/Servicio/rest/ws/borra_usuario");
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

		conexion.setDoOutput(true);
		conexion.setRequestMethod("POST");
		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String parametros = "email=" + URLEncoder.encode("" + email, "UTF-8");
		OutputStream salida = conexion.getOutputStream();
		salida.write(parametros.getBytes());
		salida.flush();

		if (conexion.getResponseCode() == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
			String res;
			System.out.println("\n\tOK. El usuario se borro correctamente");
			while ((res = br.readLine()) != null) {
				System.out.println("\n\t" + res);
			}

		} else {
			BufferedReader buffer = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String res;
			while ((res = buffer.readLine()) != null) {
				System.out.println("\n\t" + res);
			}
		}
		conexion.disconnect();
	}
}