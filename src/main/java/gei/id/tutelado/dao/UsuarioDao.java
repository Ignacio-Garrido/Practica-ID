package gei.id.tutelado.dao;

import java.util.List;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Usuario;

public interface UsuarioDao {
    	
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	Usuario almacena (Usuario usu);
	Usuario modifica (Usuario usu);
	void elimina (Usuario usu);	
	Usuario recuperaPorCorreo (String correo);
	
	// OPERACIONS POR ATRIBUTOS LAZY
	Usuario restauraCandidaturas (Usuario usu);   
		// Recibe un usuario coa colección de candidaturas como proxy SEN INICIALIZAR
		// Devolve unha copia do usuario coa colección de candidaturas INICIALIZADA
	Usuario restauraTitulacions (Usuario usu);
		// Recibe un usuario (alumno) coa colección de titulacions como proxy SEN INICIALIZAR
		// Devolve unha copia do usuario (alumno) coa colección de titulacions INICIALIZADA
	
	//QUERIES ADICIONAIS
	List<Usuario> nonCandidatos();
}
