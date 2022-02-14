package gei.id.tutelado;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ Test1_Usuarios.class, Test2_Eleccions.class, Test3_Usuarios_Eleccions.class, Test4_Consultas.class } )
public class AllTests {

}
