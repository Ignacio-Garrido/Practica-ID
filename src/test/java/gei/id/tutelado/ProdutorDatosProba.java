package gei.id.tutelado;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Alumno;
import gei.id.tutelado.model.Eleccion;
import gei.id.tutelado.model.Grupo;
import gei.id.tutelado.model.Profesor;
import gei.id.tutelado.model.Usuario;

public class ProdutorDatosProba {


	// Crea un conxunto de obxectos para utilizar nos casos de proba
	
	private EntityManagerFactory emf=null;
	
	public Alumno a0, a1;	
	public Profesor p0, p1;
	public List<Usuario> listaxeU;
	
	public Eleccion e0, e1;
	public List<Eleccion> listaxeE;

	
	
	public void Setup (Configuracion config) {
		this.emf=(EntityManagerFactory) config.get("EMF");
	}
	
	public void creaUsuariosSoltos() {

		// Crea catro usuarios EN MEMORIA: a0, a1, p0, p1
		

        Set<String> titulacions0 = new HashSet<>();
        titulacions0.add("Ed. Primaria");
        titulacions0.add("Ed. Infantil");
        this.a0 = new Alumno("a0@udc.es", "Alumno", "Cero", "Acero", titulacions0);

        Set<String> titulacions1 = new HashSet<>();
        titulacions1.add("Enx. Camiños");
        this.a1 = new Alumno("a1@udc.es", "Alumno", "Un", "Aun", titulacions1);

        this.p0 = new Profesor("p0@udc.es", "Profesor", "Cero", "Pcero", "Despacho 3.20");

        this.p1 = new Profesor("p1@udc.es", "Profesor", "Un", "Pun", null);

        this.listaxeU = new ArrayList<Usuario> ();
        this.listaxeU.add(0,a0);
        this.listaxeU.add(1,a1);
        this.listaxeU.add(2,p0);
        this.listaxeU.add(3,p1);        

	}
	
	public void creaEleccionsSoltas () {

		// Crea dúas eleccions EN MEMORIA: e0, e1
		// Sen usuario asignado (momentaneamente)
		
		this.e0=new Eleccion();
		this.e0.setNome("Eleccion 0");
		this.e0.setCentro("Centro 0");
		this.e0.setFecha(LocalDate.parse("2022-02-10"));
		this.e0.setGrupoCandidatos(Grupo.PROFESORES);
		this.e0.setGrupoVotantes(Grupo.TODOS);
		
		this.e1=new Eleccion();
		this.e1.setNome("Eleccion 1");
		this.e1.setCentro("Centro 1");
		this.e1.setFecha(LocalDate.parse("2022-03-01"));
		this.e1.setGrupoCandidatos(Grupo.ALUMNOS);
		this.e1.setGrupoVotantes(Grupo.ALUMNOS);
		
        this.listaxeE = new ArrayList<Eleccion> ();
        this.listaxeE.add(0,this.e0);
        this.listaxeE.add(1,this.e1);        

	}
	
	public void creaUsuariosConCandidaturas () {

		this.creaUsuariosSoltos();
		this.creaEleccionsSoltas();
		
        this.p1.engadirCandidatura(this.e0);
        this.p1.engadirCandidatura(this.e1);
        this.a0.engadirCandidatura(this.e1);

	}
	
	public void creaEleccionesConCandidaturasYVotantes () {

		this.creaUsuariosConCandidaturas();
		
		Set<Usuario> votantesE0 = new HashSet<Usuario>();
		votantesE0.add(a0);
		votantesE0.add(a1);
		votantesE0.add(p0);
        this.e0.setVotantes(votantesE0);
        Set<Usuario> votantesE1 = new HashSet<Usuario>();
		votantesE1.add(a1);
		votantesE1.add(p0);
        this.e1.setVotantes(votantesE1);

	}
	
	public void gravaUsuarios() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Usuario> itU = this.listaxeU.iterator();
			while (itU.hasNext()) {
				Usuario u = itU.next();
				em.persist(u);
				// DESCOMENTAR SE A PROPAGACION DO PERSIST NON ESTA ACTIVADA
				
				Iterator<Eleccion> itE = u.getCandidaturas().iterator();
				while (itE.hasNext()) {
					em.persist(itE.next());
				}
				
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}	
	}
	
	public void gravaEleccions() {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Iterator<Eleccion> itE = this.listaxeE.iterator();
			while (itE.hasNext()) {
				Eleccion e = itE.next();
				em.persist(e);
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}	
	}
	
	public void limpaBD () {
		EntityManager em=null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			em.createQuery("DELETE FROM Eleccion").executeUpdate();
			em.createNativeQuery("DELETE FROM alumno_titulacions").executeUpdate();
			em.createQuery("DELETE FROM Usuario").executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idUsuario'" ).executeUpdate();
			em.createNativeQuery("UPDATE taboa_ids SET ultimo_valor_id=0 WHERE nome_id='idEleccion'" ).executeUpdate();

			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw (e);
			}
		}
	}
	
	
}
