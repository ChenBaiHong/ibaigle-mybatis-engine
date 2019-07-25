package org.ibaigle.generator.anno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
/**
 *  实体描述
 */
public @interface BeanDescription {

    String value() default "";

    String busseID() default "";
}
