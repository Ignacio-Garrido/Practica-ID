package gei.id.tutelado.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.LazyInitializationException;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Eleccion;

public class EleccionDaoJPA implements EleccionDao {

	private EntityManagerFactory emf; 
	private EntityManager em;
    
	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}
	

	@Override
	public Eleccion almacena(Eleccion elec) {
		try {
				
			em = emf.createEntityManager();
			em.getTransaction().begin();

			em.persist(elec);

			em.getTransaction().commit();
			em.close();
		
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return elec;
	}

	@Override
	public Eleccion modifica(Eleccion elec) {
		try {

			em = emf.createEntityManager();		
			em.getTransaction().begin();

			elec = em.merge (elec);

			em.getTransaction().commit();
			em.close();
			
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return elec;
	}

	@Override
	public void elimina(Eleccion elec) {
		try {

			em = emf.createEntityManager();
			em.getTransaction().begin();

			Eleccion elecTmp = em.find (Eleccion.class, elec.getIdEleccion());
			em.remove (elecTmp);

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
	public Eleccion recuperaPorNome(String nome) {

		List<Eleccion> elecciones=null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			elecciones = em.createNamedQuery("Eleccion.recuperaPorNome", Eleccion.class)
					.setParameter("nome", nome).getResultList(); 

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (elecciones.size()==0?null:elecciones.get(0));
	}

	@Override
	public Eleccion restauraCandidatos (Eleccion elec) {  // IMPORTANTE!!! Para inicializar atributo lazy
		// Devolve o obxecto profe coa coleccion de candidatos cargada (se non o estaba xa)

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			try {
				elec.getCandidatos().size();
			} catch (Exception ex2) {
				if (ex2 instanceof LazyInitializationException)

				{
					elec = em.merge(elec);
					elec.getCandidatos().size();

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
		return elec;
	}
	
	@Override
	public Eleccion restauraVotantes (Eleccion elec) {  // IMPORTANTE!!! Para inicializar atributo lazy
		// Devolve o obxecto profe coa coleccion de votantes cargada (se non o estaba xa)

		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			try {
				elec.getVotantes().size();
			} catch (Exception ex2) {
				if (ex2 instanceof LazyInitializationException)

				{
					elec = em.merge(elec);
					elec.getVotantes().size();

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
		return elec;
	}

	@Override
	public List<Eleccion> eleccionsUsuarioVotante(String correo) {

		List<Eleccion> elecciones=null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			elecciones = em.createNamedQuery("Eleccion.eleccionsUsuarioVotante", Eleccion.class)
					.setParameter("correo", correo).getResultList(); 

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return elecciones;
	}
	
	@Override
	public List<Long> contarVotantes() {

		List<Long> elecciones=null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			elecciones = em.createNamedQuery("Eleccion.contarVotantes", Long.class).getResultList(); 

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return elecciones;
	}
	
	@Override
	public List<Eleccion> tienenMenosCandidatos(String nome) {

		List<Eleccion> elecciones=null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			elecciones = em.createNamedQuery("Eleccion.tienenMenosCandidatos", Eleccion.class)
					.setParameter("nome", nome).getResultList(); 

			em.getTransaction().commit();
			em.close();
		} catch (Exception ex ) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return elecciones;
	}
}
