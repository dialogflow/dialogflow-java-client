package ai.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ai.api.model.AIResponseTest;
import ai.api.util.ParametersConverterTest;

@RunWith(Suite.class)
@SuiteClasses({
	AIConfigurationTest.class,
	AIResponseTest.class,
	ParametersConverterTest.class,
	})
public class AllUnitTests {

}
