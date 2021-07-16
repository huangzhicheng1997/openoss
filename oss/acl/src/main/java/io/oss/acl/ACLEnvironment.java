package io.oss.acl;

import io.oss.kernel.environment.NamedEnvironment;

/**
 * @author zhicheng
 * @date 2021-05-07 15:07
 */
public class ACLEnvironment extends NamedEnvironment {

    public static final String ACL_ENVIRONMENT = "acl";

    public ACLEnvironment() {
        super(ACL_ENVIRONMENT);
    }
}

