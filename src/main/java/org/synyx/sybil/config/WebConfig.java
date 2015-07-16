package org.synyx.sybil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;

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
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
public class WebConfig {

    @Bean
    public CurieProvider curieProvider() {

        return new DefaultCurieProvider("sybil", new UriTemplate("http://doc.sybil.synyx.coffee/rels/{rel}"));
    }
}
