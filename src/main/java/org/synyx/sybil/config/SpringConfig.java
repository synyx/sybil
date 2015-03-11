package org.synyx.sybil.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Spring Configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@PropertySource(value = { "classpath:config.properties" })
@EnableScheduling
@ComponentScan(basePackages = "org.synyx.sybil") // scan for annotated classes, like @Service, @Configuration, etc.
public class SpringConfig {
}
