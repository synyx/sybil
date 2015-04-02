package org.synyx.sybil.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.synyx.sybil.webconfig.WebConfig;


/**
 * Integration Test Spring Configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Profile("dev")
@Configuration
@PropertySource(value = { "classpath:devconfig.properties" })
@EnableScheduling
@ComponentScan(
    basePackages = { "org.synyx.sybil" }, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebConfig.class)
    }
) // scan for annotated classes, like @Service, @Configuration, etc. - while ignoring the WebConfig, which is loaded by the ApiWebAppInitializer
public class DevSpringConfig {
}
