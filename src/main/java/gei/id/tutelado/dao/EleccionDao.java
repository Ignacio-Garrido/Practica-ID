package gei.id.tutelado.dao;

import java.util.List;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Eleccion;

public interface EleccionDao {
    
	void setup (Configuracion config);
	
	// OPERACIONS CRUD BASICAS
	Eleccion almacena (Eleccion elec);
	Eleccion modifica (Eleccion elec);
	void elimina (Eleccion elec);
	Eleccion recuperaPorNome (String nome);
	
	// OPERACIONS POR ATRIBUTOS LAZY
	Eleccion restauraCandidatos (Eleccion elec);   
		// Recibe unha eleccion coa colección de candidatos como proxy SEN INICIALIZAR
		// Devolve unha copia de eleccion coa colección de candidatos INICIALIZADA

	Eleccion restauraVotantes (Eleccion elec);   
		// Recibe unha eleccion coa colección de votantess como proxy SEN INICIALIZAR
		// Devolve unha copia de eleccion coa colección de votantes INICIALIZADA
	
	//QUERIES ADICIONAIS
	List<Eleccion> eleccionsUsuarioVotante (String correo);
	List<Long> contarVotantes ();
	List<Eleccion> tienenMenosCandidatos (String nome);
}
