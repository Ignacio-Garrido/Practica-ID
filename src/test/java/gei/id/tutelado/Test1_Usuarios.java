package gei.id.tutelado;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
//import org.apache.log4j.Logger;
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
import gei.id.tutelado.dao.UsuarioDao;
import gei.id.tutelado.dao.UsuarioDaoJPA;
import gei.id.tutelado.model.Alumno;
import gei.id.tutelado.model.Profesor;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test1_Usuarios {

    private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProdutorDatosProba produtorDatos = new ProdutorDatosProba();
    
    private static Configuracion cfg;
    private static UsuarioDao usuDao;
    
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
    	usuDao.setup(cfg);
    	
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
  
		produtorDatos.creaUsuariosSoltos();
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de gravación na BD de novo profesor\n");
    	
    	// Situación de partida:
    	// p0 transitorio    	
    	
    	Assert.assertNull(produtorDatos.p0.getIdUsuario());
    	usuDao.almacena(produtorDatos.p0);    	
    	Assert.assertNotNull(produtorDatos.p0.getIdUsuario());
    	
    	log.info("");
    	log.info("Obxectivo: Proba de gravación na BD de novo alumno\n");
    	
    	// Situación de partida:
    	// a0 transitorio    	
    	
    	Assert.assertNull(produtorDatos.a0.getIdUsuario());
    	usuDao.almacena(produtorDatos.a0);    	
    	Assert.assertNotNull(produtorDatos.a0.getIdUsuario());
    }
    
    @Test 
    public void t2_CRUD_TestRecupera() {
    	
    	Profesor p;
    	Alumno a;
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
    	produtorDatos.gravaUsuarios();
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de recuperación desde a BD de profesor (sen candidaturas en eleccions) por correo\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Recuperación por correo existente\n"
    			+ "\t\t\t\t a.1) Recuperación de alumno con colección (LAZY) de titulacions \n"
    			+ "\t\t\t\t a.2) Carga forzada de colección LAZY da dita coleccion\n"
    			+ "\t\t\t\t b) Recuperacion por correo inexistente\n");

    	// Situación de partida:
    	// p0 desligado    	

    	log.info("Probando recuperacion de profesor por correo EXISTENTE --------------------------------------------------");
    	
    	p = (Profesor) usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo());
    	Assert.assertEquals(produtorDatos.p0.getCorreo(),       p.getCorreo());
    	Assert.assertEquals(produtorDatos.p0.getNome(),         p.getNome());
    	Assert.assertEquals(produtorDatos.p0.getApelido1(),     p.getApelido1());
    	Assert.assertEquals(produtorDatos.p0.getApelido2(),     p.getApelido2());
    	Assert.assertEquals(produtorDatos.p0.getDespacho(),     p.getDespacho());

    	log.info("");	
		log.info("Probando recuperacion de profesor por correo INEXISTENTE -----------------------------------------------");
    	
    	p = (Profesor) usuDao.recuperaPorCorreo("dvsdabdsbdsfbvsagvssdg@udc.es");
    	Assert.assertNull (p);
    	
    	
    	log.info("");
    	log.info("Obxectivo: Proba de recuperación desde a BD de alumno (sen candidaturas en eleccions) por correo\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Recuperación por correo existente\n"
    			+ "\t\t\t\t b) Recuperacion por correo inexistente\n");

    	// Situación de partida:
    	// a0 desligado    	

    	log.info("Probando recuperacion de alumno por correo EXISTENTE --------------------------------------------------");
    	
    	a = (Alumno) usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo());
    	Assert.assertEquals(produtorDatos.a0.getCorreo(),       a.getCorreo());
    	Assert.assertEquals(produtorDatos.a0.getNome(),         a.getNome());
    	Assert.assertEquals(produtorDatos.a0.getApelido1(),     a.getApelido1());
    	Assert.assertEquals(produtorDatos.a0.getApelido2(),     a.getApelido2());

    	log.info("");
    	log.info("Probando (excepcion tras) recuperacion LAZY ---------------------------------------------------------------------");
    	
    	try	{
        	Assert.assertEquals(produtorDatos.a0.getTitulacions(),     a.getTitulacions());
        	excepcion=false;
    	} catch (LazyInitializationException ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	};    	
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
    	log.info("Probando carga forzada de coleccion LAZY ------------------------------------------------------------------------");
    	
    	a = (Alumno) usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo());   // Alumno a con proxy sen inicializar
    	a = (Alumno) usuDao.restauraTitulacions(a);						// Alumno a con proxy xa inicializado

    	Assert.assertEquals(produtorDatos.a0.getCorreo(),       a.getCorreo());
    	Assert.assertEquals(produtorDatos.a0.getNome(),         a.getNome());
    	Assert.assertEquals(produtorDatos.a0.getApelido1(),     a.getApelido1());
    	Assert.assertEquals(produtorDatos.a0.getApelido2(),     a.getApelido2());
    	Assert.assertEquals(produtorDatos.a0.getTitulacions(),     a.getTitulacions());
    	
    	log.info("");	
		log.info("Probando recuperacion de alumno por correo INEXISTENTE -----------------------------------------------");
    	
		a = (Alumno) usuDao.recuperaPorCorreo("iwbvydvdsbvsdabvfdsb@udc.es");
    	Assert.assertNull (a);

    } 	

    @Test 
    public void t3_CRUD_TestElimina() {
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
    	produtorDatos.gravaUsuarios();

    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación da BD de profesor sen candidaturas en eleccions\n");   
 
    	// Situación de partida:
    	// p0 desligado  

    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo()));
    	usuDao.elimina(produtorDatos.p0);    	
    	Assert.assertNull(usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo()));
    	
    	
    	log.info("");
    	log.info("Obxectivo: Proba de eliminación da BD de alumno sen candidaturas en eleccions\n");   
    	 
    	// Situación de partida:
    	// a0 desligado  

    	Assert.assertNotNull(usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo()));
    	usuDao.elimina(produtorDatos.a0);    	
    	Assert.assertNull(usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo()));
    } 	

    @Test 
    public void t4_CRUD_TestModifica() {
    	
    	Profesor p1, p2;
    	Alumno a1, a2;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
    	produtorDatos.gravaUsuarios();

    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de modificación da información básica dun profesor sen candidaturas en eleccions\n");

    	// Situación de partida:
    	// p0 desligado  

		String novoNomeP = new String ("Nome profe novo");
		String novoApelido1P = new String ("Apelido1 profe novo");

		p1 = (Profesor) usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo());
		Assert.assertNotEquals(novoNomeP, p1.getNome());
		Assert.assertNotEquals(novoApelido1P, p1.getApelido1());
    	p1.setNome(novoNomeP);
    	p1.setApelido1(novoApelido1P);

    	usuDao.modifica(p1);    	
    	
    	p2 = (Profesor) usuDao.recuperaPorCorreo(produtorDatos.p0.getCorreo());
		Assert.assertEquals (novoNomeP, p2.getNome());
		Assert.assertEquals (novoApelido1P, p2.getApelido1());
		
		
		log.info("");
		log.info("Obxectivo: Proba de modificación da información básica dun alumno sen candidaturas en eleccions\n");

    	// Situación de partida:
    	// a0 desligado  

		String novoNomeA = new String ("Nome alumno novo");
		String novoApelido2A = new String ("Apelido2 alumno novo");

		a1 = (Alumno) usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo());
		Assert.assertNotEquals(novoNomeA, a1.getNome());
		Assert.assertNotEquals(novoApelido2A, a1.getApelido2());
    	a1.setNome(novoNomeA);
    	a1.setApelido2(novoApelido2A);

    	usuDao.modifica(a1);    	
    	
    	a2 = (Alumno) usuDao.recuperaPorCorreo(produtorDatos.a0.getCorreo());
		Assert.assertEquals (novoNomeA, a2.getNome());
		Assert.assertEquals (novoApelido2A, a2.getApelido2());

    } 	
    
    
    @Test
    public void t5_CRUD_TestExcepcions() {
    	
    	Boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		produtorDatos.creaUsuariosSoltos();
		usuDao.almacena(produtorDatos.p0);
    	usuDao.almacena(produtorDatos.a0);
    	
    	log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violación de restricións not null e unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Gravación de profesor con correo duplicado\n"
    			+ "\t\t\t\t b) Gravación de profesor con correo nulo\n"
    			+ "\t\t\t\t c) Gravación de alumno con correo duplicado\n"
    			+ "\t\t\t\t d) Gravación de alumno con nome nulo\n");

    	// Situación de partida:
    	// p0 y a0 desligado, p1 y a1 transitorio
    	
		log.info("Probando gravacion de profesor con correo duplicado -----------------------------------------------");
    	produtorDatos.p1.setCorreo(produtorDatos.p0.getCorreo());
    	try {
        	usuDao.almacena(produtorDatos.p1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");	
		log.info("Probando gravacion de profesor con correo nulo ----------------------------------------------------");
    	produtorDatos.p1.setCorreo(null);
    	try {
        	usuDao.almacena(produtorDatos.p1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
		log.info("Probando gravacion de alumno con correo duplicado -----------------------------------------------");
    	produtorDatos.a1.setCorreo(produtorDatos.a0.getCorreo());
    	try {
        	usuDao.almacena(produtorDatos.a1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");	
		log.info("Probando gravacion de alumno con nome nulo ----------------------------------------------------");
    	produtorDatos.a1.setNome(null);
    	try {
        	usuDao.almacena(produtorDatos.a1);
        	excepcion=false;
    	} catch (Exception ex) {
    		excepcion=true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    } 	
    
}