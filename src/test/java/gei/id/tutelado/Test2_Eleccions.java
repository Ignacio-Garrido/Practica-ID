package gei.id.tutelado;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import gei.id.tutelado.model.Eleccion;
import gei.id.tutelado.model.Grupo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test2_Eleccions {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
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

    	eleDao = new EleccionDaoJPA();
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
		log.info("Limpando BD --------------------------------------------------------------------------------------------");
		produtorDatos.limpaBD();
	}

	@After
	public void tearDown() throws Exception {
	}
	

    @Test 
    public void t1_CRUD_TestAlmacena() {

    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
  
		produtorDatos.creaEleccionsSoltas();
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de gravación na BD de nova elección\n");
    	
    	// Situación de partida:
    	// e0 transitorio    	
    	
    	Assert.assertNull(produtorDatos.e0.getIdEleccion());
    	eleDao.almacena(produtorDatos.e0);    	
    	Assert.assertNotNull(produtorDatos.e0.getIdEleccion());
    }
    
    @Test 
    public void t2_CRUD_TestRecupera() {
    	
    	Eleccion e;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionsSoltas();
    	produtorDatos.gravaEleccions();
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de recuperación desde a BD de eleccion (sen candidatos nin votantes) por nome\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Recuperación por nome existente\n"
    			+ "\t\t\t\t b) Recuperacion por nome inexistente\n");

    	// Situación de partida:
    	// e0 desligado    	

    	log.info("Probando recuperacion de eleccion por correo EXISTENTE --------------------------------------------------");
    	
    	e = eleDao.recuperaPorNome(produtorDatos.e0.getNome());
    	Assert.assertEquals(produtorDatos.e0.getNome(),       e.getNome());
    	Assert.assertEquals(produtorDatos.e0.getCentro(),       e.getCentro());
    	Assert.assertEquals(produtorDatos.e0.getFecha(),       e.getFecha());
    	Assert.assertEquals(produtorDatos.e0.getGrupoCandidatos(),       e.getGrupoCandidatos());
    	Assert.assertEquals(produtorDatos.e0.getGrupoVotantes(),       e.getGrupoVotantes());

    	log.info("");	
		log.info("Probando recuperacion de eleccion por nome INEXISTENTE -----------------------------------------------");
    	
    	e = eleDao.recuperaPorNome("Nome INEXISTENTE");
    	Assert.assertNull (e);
    }
    
    @Test 
    public void t3_CRUD_TestElimina() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionsSoltas();
    	produtorDatos.gravaEleccions();

    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación da BD de eleccion sen candidatos nin votantes\n");   
 
    	// Situación de partida:
    	// e0 desligado  

    	Assert.assertNotNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
    	eleDao.elimina(produtorDatos.e0);    	
    	Assert.assertNull(eleDao.recuperaPorNome(produtorDatos.e0.getNome()));
    }
    
    @Test 
    public void t4_CRUD_TestModifica() {
    	
    	Eleccion e1, e2;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionsSoltas();
    	produtorDatos.gravaEleccions();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de modificación da información básica dunha eleccion sen candidatos nin votantes\n");

    	// Situación de partida:
    	// e0 desligado  

		String novoCentroE = new String ("Nome centro novo");
		Grupo novoVotantesE = Grupo.ALUMNOS;

		e1 = eleDao.recuperaPorNome(produtorDatos.e0.getNome());
		Assert.assertNotEquals(novoCentroE, e1.getCentro());
		Assert.assertNotEquals(novoVotantesE, e1.getGrupoVotantes());
		
    	e1.setCentro(novoCentroE);
    	e1.setGrupoVotantes(novoVotantesE);

    	eleDao.modifica(e1);    	
    	
    	e2 = eleDao.recuperaPorNome(produtorDatos.e0.getNome());
		Assert.assertEquals(novoCentroE, e2.getCentro());
		Assert.assertEquals(novoVotantesE, e2.getGrupoVotantes());
    }
    
    @Test
    public void t5_CRUD_TestExcepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionsSoltas();
    	eleDao.almacena(produtorDatos.e0);
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violación de restricións not null e unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Gravación de eleccion con nome duplicado\n"
    			+ "\t\t\t\t b) Gravación de eleccion con nome nulo\n");

    	// Situación de partida:
    	// e0 desligado, e1 transitorio
    	
		log.info("Probando gravacion de eleccion con nome duplicado -----------------------------------------------");
    	produtorDatos.e1.setNome(produtorDatos.e0.getNome());
    	try {
        	eleDao.almacena(produtorDatos.e1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");	
		log.info("Probando gravacion de eleccion con nome nulo ----------------------------------------------------");
    	produtorDatos.e1.setNome(null);
    	try {
        	eleDao.almacena(produtorDatos.e1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    }
}