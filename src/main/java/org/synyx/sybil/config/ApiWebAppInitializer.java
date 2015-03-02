package org.synyx.sybil.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


/**
 * ApiWebAppInitializer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class ApiWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {

        return new Class<?>[] { SpringConfig.class };
    }


    @Override
    protected Class<?>[] getServletConfigClasses() {

        return new Class<?>[] { WebConfig.class };
    }


    @Override
    protected String[] getServletMappings() {

        return new String[] { "/" };
    }
}
