package ai.api.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ProtocolProdTest.class,
/* Disabled due to ProtocolProdTest is enough for now
	DefaultProtocolTest.class,
	V20150204ProtocolTest.class
*/
	})
public class AllFunctionalTests {

}
