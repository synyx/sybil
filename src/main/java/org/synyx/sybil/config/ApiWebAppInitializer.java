package org.synyx.sybil.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


/**
 * ApiWebAppInitializer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class ApiWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ApiWebAppInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {

        LOG.info("getRootConfigClasses run");

        return new Class<?>[] { SpringConfig.class };
    }


    @Override
    protected Class<?>[] getServletConfigClasses() {

        LOG.info("getServletConfigClasses run");

        return new Class<?>[] { WebConfig.class };
    }


    @Override
    protected String[] getServletMappings() {

        LOG.info("getServletMappings run");

        return new String[] { "/" };
    }
}
