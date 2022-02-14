package gei.id.tutelado.dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.LazyInitializationException;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Alumno;
import gei.id.tutelado.model.Eleccion;
import gei.id.tutelado.model.Usuario;


public class UsuarioDaoJPA implements UsuarioDao {

	private EntityManagerFactory emf; 
	private EntityManager em;

	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}

	@Override
	public Usuario almacena(Usuario usu) {

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(usu);

			em.getTransaction().commit();
			em.close();

		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return usu;
	}

	@Override
	public Usuario modifica(Usuario usu) {

		try {
			
			em = emf.createEntityManager();
			em.getTransaction().begin();

			usu = em.merge (usu);

			em.getTransaction().commit();
			em.close();		
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (usu);
	}

	@Override
	public void elimina(Usuario usu) {
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Usuario usuTmp = em.find (Usuario.class, usu.getIdUsuario());

			Iterator<Eleccion> itE = usuTmp.getCandidaturas().iterator();
			while (itE.hasNext()) {
				itE.next().getCandidatos().remove(usuTmp);
			}
			
			List<Eleccion> listE = em.createNamedQuery("Usuario.recuperaEleccionsVotante", Eleccion.class).setParameter("correo", usuTmp.getCorreo()).getResultList();
			listE.forEach(ele -> {
				ele.getVotantes().remove(usuTmp);
			});

			em.remove (usuTmp);
			em.getTransaction().commit();
			em.close();
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
	}


	@Override
	public Usuario recuperaPorCorreo(String correo) {
		List <Usuario> usuarios=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			usuarios = em.createNamedQuery("Usuario.recuperaPorCorreo", Usuario.class).setParameter("correo", correo).getResultList(); 

			em.getTransaction().commit();
			em.close();	

		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}

		return (usuarios.size()!=0?usuarios.get(0):null);
	}


	@Override
	public Usuario restauraCandidaturas (Usuario usu) {  // IMPORTANTE!!! Para inicializar atributo lazy
		// Devolve o obxecto usuario coa coleccion de entradas cargada (se non o estaba xa)

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			try {
				usu.getCandidaturas().size();
			} catch (Exception ex2) {
				if (ex2 instanceof LazyInitializationException)

				{
					usu = em.merge(usu);
					usu.getCandidaturas().size();

				} else {
					throw ex2;
				}
			}
			em.getTransaction().commit();
			em.close();
		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		
		return (usu);

	}
	
	@Override
	public Usuario restauraTitulacions (Usuario usu) {  // IMPORTANTE!!! Para inicializar atributo lazy
		// Devolve o obxecto alumno coa coleccion de titulacons cargada (se non o estaba xa)

		Alumno alumno = (Alumno) usu;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			try {
				alumno.getTitulacions().size();
			} catch (Exception ex2) {
				if (ex2 instanceof LazyInitializationException)

				{
					alumno = em.merge(alumno);
					alumno.getTitulacions().size();

				} else {
					throw ex2;
				}
			}
			em.getTransaction().commit();
			em.close();
		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		
		return (Usuario) alumno;

	}
	
	@Override
	public List<Usuario> nonCandidatos() {
		List <Usuario> usuarios=null;

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			usuarios = em.createNamedQuery("Usuario.nonCandidatos", Usuario.class).getResultList(); 

			em.getTransaction().commit();
			em.close();	

		}
		catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}

		return usuarios;
	}
}
