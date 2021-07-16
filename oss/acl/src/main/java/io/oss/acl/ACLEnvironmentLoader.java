package io.oss.acl;

import io.oss.kernel.environment.NamedEnvironment;
import io.oss.kernel.spi.plugins.AbstractEnvironmentLoader;

import java.util.Properties;

import static io.oss.acl.ACLEnvironment.ACL_ENVIRONMENT;

/**
 * @author zhicheng
 * @date 2021-05-07 15:04
 */
public class ACLEnvironmentLoader extends AbstractEnvironmentLoader {

    public ACLEnvironmentLoader() {
        super("acl.properties");
    }

    @Override
    public String getNameSpace() {
        return ACL_ENVIRONMENT;
    }

    @Override
    protected NamedEnvironment buildEnvironment(Properties properties) {
        ACLEnvironment aclEnvironment = new ACLEnvironment();
        aclEnvironment.putAll(properties);
        return aclEnvironment;
    }
}
