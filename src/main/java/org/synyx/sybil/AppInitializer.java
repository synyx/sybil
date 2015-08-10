package org.synyx.sybil;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import org.synyx.sybil.config.SpringConfig;
import org.synyx.sybil.config.WebConfig;


/**
 * AppInitializer.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class[] getRootConfigClasses() {

        return new Class[] { SpringConfig.class };
    }


    @Override
    protected Class[] getServletConfigClasses() {

        return new Class[] { WebConfig.class };
    }


    @Override
    protected String[] getServletMappings() {

        return new String[] { "/" };
    }
}
