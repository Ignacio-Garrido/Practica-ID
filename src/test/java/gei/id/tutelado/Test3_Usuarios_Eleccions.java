package gei.id.tutelado;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.EleccionDao;
import gei.id.tutelado.dao.EleccionDaoJPA;
import gei.id.tutelado.dao.UsuarioDao;
import gei.id.tutelado.dao.UsuarioDaoJPA;
import gei.id.tutelado.model.Eleccion;
import gei.id.tutelado.model.Profesor;
import gei.id.tutelado.model.Usuario;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test3_Usuarios_Eleccions {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static UsuarioDao usuDao;
    private static EleccionDao eleDao;
    
    @Rule
    public TestRule watcher = new TestWatcher() {
       protected void starting(Description description) {
    	   log.info("");
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    	   log.info("Iniciando test: " + description.getMethodName());
    	   log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
       }
       protected void finished(Description description) {
    	   log.info("");
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
    	   log.info("Finalizado test: " + description.getMethodName());
    	   log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
       }
    };
    
    
    @BeforeClass
    public static void init() throws Exception {
    	cfg = new ConfiguracionJPA();
    	cfg.start();

    	usuDao = new UsuarioDaoJPA();
    	eleDao = new EleccionDaoJPA();
    	usuDao.setup(cfg);
    	eleDao.setup(cfg);
    	
    	produtorDatos = new ProdutorDatosProba();
    	produtorDatos.Setup(cfg);
    }
    
    @AfterClass
    public static void endclose() throws Exception {
    	cfg.endUp();    	
    }
    
    
    
	@Before
	public void setUp() throws Exception {		
		log.info("");	
		log.info("Limpando BD -----------------------------------------------------------------------------------------------------");
		produtorDatos.limpaBD();
	}

	@After
	public void tearDown() throws Exception {
	}	

	
    @Test 
    public void t1_CRUD_TestAlmacena() {


    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
    	produtorDatos.gravaUsuarios();
    	produtorDatos.creaEleccionsSoltas();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da gravacion de usuario con candidatura\n");   	

    	// Situación de partida:
    	// p0 desligado    	
    	// e0, e1 transitorios

    	produtorDatos.p0.engadirCandidatura(produtorDatos.e0);
		
    	log.info("");	
		log.info("Gravando candidatura dun usuario --------------------------------------------------------------------");
    	Assert.assertNull(produtorDatos.e0.getIdEleccion());
    	eleDao.almacena(produtorDatos.e0);
    	Assert.assertNotNull(produtorDatos.e0.getIdEleccion());

    	produtorDatos.p0.engadirCandidatura(produtorDatos.e1);
    	Assert.assertNotNull(produtorDatos.p0.getCandidaturas());
    	Assert.assertNotNull(produtorDatos.e0.getCandidatos());

    } 	

    @Test 
    public void t2_CRUD_TestRecupera() {
    	
    	Profesor p;
    	Eleccion e;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da recuperación de propiedades LAZY\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación de usuario con colección (LAZY) de candidaturas \n"
		+ "\t\t\t\t b) Carga forzada de colección LAZY da dita coleccion\n"
    	+ "\t\t\t\t c) Recuperación de elección con colección (LAZY) de candidatos \n"
		+ "\t\t\t\t d) Carga forzada de colección LAZY da dita coleccion\n"	
    	+ "\t\t\t\t e) Recuperación de elección con colección (LAZY) de votantes \n"
		+ "\t\t\t\t f) Carga forzada de colección LAZY da dita coleccion\n");	

    	// Situación de partida:
    	// p1, e0, e1 desligados
    	
		log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");
    	
    	p = (Profesor) usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo());
		log.info("Acceso a candidaturas de usuario");
    	try	{
        	Assert.assertEquals(2, p.getCandidaturas().size());
        	Assert.assertEquals(produtorDatos.e0, p.getCandidaturas().first());
        	Assert.assertEquals(produtorDatos.e1, p.getCandidaturas().last());	
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertTrue(excepcion);
    
    	log.info("");
    	log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");
    	
    	p = (Profesor) usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo());   // Usuario p con proxy sen inicializar
    	p = (Profesor) usuDao.restauraCandidaturas(p);						// Usuario p con proxy xa inicializado
    	
    	Assert.assertEquals(2, p.getCandidaturas().size());
    	Assert.assertEquals(produtorDatos.e0, p.getCandidaturas().first());
    	Assert.assertEquals(produtorDatos.e1, p.getCandidaturas().last());
    	
    	// Situación de partida:
    	// e0, a0, p1 desligados
    	Set<Usuario> cand = new HashSet<>();
    	cand.add(produtorDatos.p1);
    	cand.add(produtorDatos.a0);
    	
		log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");
    	
    	e = eleDao.recuperaPorNome(produtorDatos.e1.getNome());
		log.info("Acceso a candidatos de eleccion");
    	try	{
        	Assert.assertEquals(2, e.getCandidatos().size());
        	Assert.assertEquals(cand, e.getCandidatos());	
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertTrue(excepcion);
    
    	log.info("");
    	log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");
    	
    	e = eleDao.recuperaPorNome(produtorDatos.e1.getNome());   // Eleccion e con proxy sen inicializar
    	e = eleDao.restauraCandidatos(e);						// Eleccion e con proxy xa inicializado
    	
    	Assert.assertEquals(2, e.getCandidatos().size());
    	Assert.assertEquals(cand, e.getCandidatos());
    	
    	// Situación de partida:
    	// e1, a1, p0 desligados
    	Set<Usuario> vot = new HashSet<>();
    	cand.add(produtorDatos.a1);
    	cand.add(produtorDatos.p0);
    	
		log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");
    	
    	e = eleDao.recuperaPorNome(produtorDatos.e1.getNome());
		log.info("Acceso a votantes de eleccion");
    	try	{
        	Assert.assertEquals(2, e.getVotantes().size());
        	Assert.assertEquals(vot, e.getVotantes());	
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertTrue(excepcion);
    
    	log.info("");
    	log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");
    	
    	e = eleDao.recuperaPorNome(produtorDatos.e1.getNome());   // Eleccion e con proxy sen inicializar
    	e = eleDao.restauraVotantes(e);						// Eleccion e con proxy xa inicializado
    	
    	Assert.assertEquals(2, e.getVotantes().size());
    	//Assert.assertEquals(vot, e.getVotantes());
    } 	

    @Test 
    public void t3a_CRUD_TestElimina() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
   	
    	produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de usuario con candidaturas asociadas\n");

    	// Situación de partida:
    	// p1, e0, e1 desligados

    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));
		
		// Aqui o remove sobre p1 non debe propagarse a e0 e e1
		usuDao.elimina(produtorDatos.p1); 	
		
		Assert.assertNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));

    } 
    
    @Test 
    public void t3b_CRUD_TestElimina() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
   	
    	produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
    	log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de elección con usuarios asociados\n"
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Candidatos asociados \n"
		+ "\t\t\t\t b) Votantes asociados \n");	

    	// Situación de partida:
    	// a0, p0, p1, a0, e1 desligados

    	//Eleccion
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));
		//Candidatos
    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
		Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo()));
		//Votantes
    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo()));
		Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.a1.getCorreo()));
		
		
		
		// Aqui o remove sobre e1 non debe propagarse a p0, p1, a0 e a1
		eleDao.elimina(produtorDatos.e1); 	
		
		//Eleccion
		Assert.assertNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));
		//Candidatos
    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
		Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo()));
		//Votantes
    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo()));
		Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.a1.getCorreo()));

    }
    
    @Test 
    public void t3c_CRUD_TestElimina() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
   	
    	produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
    	log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de usuario con candidaturas\n");	

    	// Situación de partida:
    	// p1, e0, e1 desligados

		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
		
		// Aqui o remove sobre p1 non debe propagarse a e0 e e1
		usuDao.elimina(produtorDatos.p1); 	

		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e1.getNome()));
		Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
    	Assert.assertNull(usuDao.recuperaPorCorreo(produtorDatos.p1.getCorreo()));
    }
    
 // NOTA: Non probamos modificacións porque non teñen sentido no dominio considerado, non inflúen


    @Test
    public void t4_CRUD_TestExcepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
		produtorDatos.gravaUsuarios();
		
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violacion de restricions candidatura duplicada\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Candidatura repetida\n"
    			+ "\t\t\t\t b) Votante repetido\n");

    	// Situación de partida:
    	// p0 desligado, e1 transitorio (e sen usuario asociado)
    	    	
    	log.info("");	
		log.info("Probando gravacion de candidatura duplicada -------------------------------------------------------------------");
    	try {
    		produtorDatos.p1.engadirCandidatura(produtorDatos.e1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    }
}