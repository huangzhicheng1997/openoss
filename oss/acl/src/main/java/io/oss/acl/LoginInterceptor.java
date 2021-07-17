package io.oss.acl;

import io.netty.util.internal.StringUtil;
import io.oss.protocol.exception.AuthenticationException;
import io.oss.kernel.environment.EnvironmentAware;
import io.oss.kernel.environment.IsolatedEnvironment;
import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.kernel.support.processor.AbstractProcessorInterceptor;
import io.oss.kernel.support.processor.HandlerChainContext;
import io.oss.protocol.Command;
import io.oss.util.util.KVPair;
import io.oss.util.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhicheng
 * @date 2021-05-06 18:25
 */
public class LoginInterceptor extends AbstractProcessorInterceptor implements EnvironmentAware, WheelTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String privateKey;

    private String publicKey;

    private boolean isOpen;

    private static final String USER_INFO = "userInfo";

    //修改过密码等操作，导致accessToken也会发生更改，所以问题不大
    private ConcurrentMap<String/*accessToken*/, KVPair<String/*decryptResult*/, Long>> accessCache = new ConcurrentHashMap<>();

    private final Long cacheEffectTime = 2 * 3600 * 1000L;


    @Override
    public boolean preHandle(Command request, HandlerChainContext context) {
        String accessToken = request.getHeader().accessToken();
        //缓存存在则已认证过
        if (isOpen && !accessCache.containsKey(accessToken)) {
            try {
                String userInfo = RSAUtil.decrypt(accessToken, privateKey);
                accessCache.put(accessToken, new KVPair<>(userInfo, System.currentTimeMillis()));
                context.addAttr(USER_INFO, userInfo);
            } catch (Exception e) {
                logger.warn("user authentication failed,accessToken=" + accessToken);
                throw new AuthenticationException("user authentication failed,accessToken=" + accessToken);
            }
        }
        return true;
    }

    @Override
    public Command afterHandle(Command response, HandlerChainContext context) {
        return response;
    }

    @Override
    protected void excludeUri(Set<String> excludeUri) {
        excludeUri.add("/login");
    }

    @Override
    protected void matchURIInit(Set<String> matchUri) {
        matchUri.add("/**");
    }

    @Override
    public void afterInit() {
        super.afterInit();
        if (StringUtil.isNullOrEmpty(privateKey) || StringUtil.isNullOrEmpty(publicKey)) {
            throw new IllegalStateException("privateKey or publicKey is illegal please check!");
        }
    }

    @Override
    public void setEnvironment(IsolatedEnvironment environment) {
        this.privateKey = environment.getPrivateProperty(ACLEnvironment.ACL_ENVIRONMENT, "user.authentication.rsa2.privateKey");
        this.publicKey = environment.getPrivateProperty(ACLEnvironment.ACL_ENVIRONMENT, "user.authentication.rsa2.publicKey");
        this.isOpen = Boolean.parseBoolean(environment.getPrivateProperty(ACLEnvironment.ACL_ENVIRONMENT, "user.authentication.open"));

    }

    @Override
    public void execute() {
        accessCache.forEach((k, v) -> {
            Long currentTime = v.getV();
            if (System.currentTimeMillis() - currentTime >= cacheEffectTime) {
                accessCache.remove(k);
            }
        });
    }

    @Override
    public Long delayMillSeconds() {
        return cacheEffectTime;
    }
}
