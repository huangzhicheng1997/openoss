package io.oss.framework.remoting.protocol;

import java.lang.annotation.*;

/**
 * @author zhicheng
 * @date 2021-01-25 17:51
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String name() default "";
}
