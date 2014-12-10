package org.synyx.sybil.config;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import org.springframework.core.env.Environment;

import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.SingleStatusOnLEDStrip;
import org.synyx.sybil.out.SingleStatusOutput;

import java.io.IOException;


/**
 * Development Configuration.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Profile("dev")
@Configuration
@PropertySource("classpath:SpringConfigDev.properties")
public class SpringConfigDev {

    private static final Logger LOG = LoggerFactory.getLogger(SpringConfigDev.class);

    @Autowired
    Environment env;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IPConnection ipConnection() throws AlreadyConnectedException, IOException {

        String hostname = env.getRequiredProperty("ipconnection.hostname");
        int port = env.getProperty("ipconnection.port", Integer.class, 4223); // 4223 is the standard port

        LOG.info("Creating IPConnection to {}:{}", hostname, port);

        IPConnection ipConnection = new IPConnection();
        ipConnection.connect(hostname, port);

        LOG.info("Successfully connected to {}:{}", hostname, port);

        return ipConnection;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BrickletLEDStrip brickletLEDStrip(IPConnection ipConnection) throws TimeoutException, NotConnectedException {

        BrickletLEDStrip brickletLEDStrip = new BrickletLEDStrip(env.getRequiredProperty("brickletledstrip.uid"),
                ipConnection);
        brickletLEDStrip.setFrameDuration(10);
        brickletLEDStrip.setChipType(2812);

        return brickletLEDStrip;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OutputLEDStrip outputLEDStrip(BrickletLEDStrip brickletLEDStrip) {

        return new OutputLEDStrip(brickletLEDStrip, env.getRequiredProperty("outputledstrip.length", Integer.class));
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SingleStatusOutput singleStatusOutput(OutputLEDStrip outputLEDStrip) {

        return new SingleStatusOnLEDStrip(outputLEDStrip);
    }
}
