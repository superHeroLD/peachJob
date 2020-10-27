package com.ld.peach.job.core.anno;


import java.lang.annotation.*;


/**
 * annotation for method task handler
 *
 * @author lidong
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PeachTaskHandler {

    /**
     * define task handler name
     */
    String value();
}
