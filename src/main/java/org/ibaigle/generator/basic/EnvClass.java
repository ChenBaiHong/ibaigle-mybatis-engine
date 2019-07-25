package org.ibaigle.generator.basic;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class EnvClass {

    private JavaPathEnv javaPathEnv;
    private ResourcesPathEnv resourcesPathEnv;
    private Class[] classes;

    public EnvClass(JavaPathEnv javaPathEnv,
                            ResourcesPathEnv resourcesPathEnv ,
                            Class ... classes) {
        this.javaPathEnv = javaPathEnv;
        this.resourcesPathEnv = resourcesPathEnv;
        this.classes = classes;
    }
}
