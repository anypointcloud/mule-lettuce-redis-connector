package cloud.anypoint.redis.operation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class LettuceRedisOperationsTestCase extends MuleArtifactFunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  @Test
  public void placeholder() {

  }
}
