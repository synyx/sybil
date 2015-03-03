package org.synyx.sybil.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.hateoas.config.EnableHypermediaSupport;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * WebConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@ComponentScan(basePackages = "org.synyx.sybil.api")
@EnableWebMvc
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
public class WebConfig {
}
