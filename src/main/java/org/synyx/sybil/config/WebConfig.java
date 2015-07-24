package org.synyx.sybil.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * WebConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@ComponentScan(
    basePackages = { "org.synyx.sybil.api", "org.synyx.sybil.brick.api", "org.synyx.sybil.bricklet.*.*.api" }
)
@EnableWebMvc
public class WebConfig {
}
