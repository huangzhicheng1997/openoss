package io.oss.kernel.environment;

import java.util.Properties;

/**
 * @Author zhicheng
 * @Date 2021/4/10 2:09 下午
 * @Version 1.0
 */
public interface Environment {

   public Properties getProperties();

   public void putAll(Properties properties);

   public void addProperty(String name, String value);


}
