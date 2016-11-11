package ai.api.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ai.api.test.compatibility.DefaultProtocolTest;
import ai.api.test.compatibility.V20150204ProtocolTest;

@RunWith(Suite.class)
@SuiteClasses({
	/* ProtocolDevTest.class, */
	ProtocolProdTest.class,
	DefaultProtocolTest.class,
	V20150204ProtocolTest.class
	})
public class AllFunctionalTests {

}
