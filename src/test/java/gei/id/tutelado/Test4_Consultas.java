package gei.id.tutelado;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.UsuarioDao;
import gei.id.tutelado.dao.UsuarioDaoJPA;
import gei.id.tutelado.dao.EleccionDao;
import gei.id.tutelado.dao.EleccionDaoJPA;
import gei.id.tutelado.model.Eleccion;
import gei.id.tutelado.model.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.Exception;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test4_Consultas {

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
    public void t1_consultaInner() {

    	List<Eleccion> listaE;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Recupera a lista de eleccions nas que é votante o usuario con correo 'a1@udc.es'\n");

		listaE = eleDao.eleccionsUsuarioVotante("a1@udc.es");
		Assert.assertEquals(2, listaE.size());
		Assert.assertEquals(produtorDatos.e1, listaE.get(0));
		Assert.assertEquals(produtorDatos.e0, listaE.get(1));
    }
	
	@Test 
    public void t2_consultaOuter() {

    	List<Usuario> listaU;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Recupera a lista de usuarios que non son candidatos en ningunha elección\n");

		listaU = usuDao.nonCandidatos();
		Assert.assertEquals(2, listaU.size());
		Assert.assertEquals(produtorDatos.p0, listaU.get(0));
		Assert.assertEquals(produtorDatos.a1, listaU.get(1));
    }
	
	@Test 
    public void t3_subconsulta() {

    	List<Eleccion> listaE;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Recupera a lista de elecciones que tengan menos candidatos que a eleccion con nome 'Eleccion 1'\n");

		listaE = eleDao.tienenMenosCandidatos("Eleccion 1");
		Assert.assertEquals(1, listaE.size());
		Assert.assertEquals(produtorDatos.e0, listaE.get(0));
    }
	
	@Test 
    public void t4_consultaAgregacion() {

    	List<Long> listaE;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaEleccionesConCandidaturasYVotantes();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Para cada elección devolve a cantidade de votantes rexistrados\n");

		listaE = eleDao.contarVotantes();
		Assert.assertEquals(2, listaE.size());
		Assert.assertEquals(produtorDatos.e0.getVotantes().size(), listaE.get(0).intValue());
		Assert.assertEquals(produtorDatos.e1.getVotantes().size(), listaE.get(1).intValue());
    }
}