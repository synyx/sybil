package org.synyx.sybil.config;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.synyx.sybil.out.OutputLEDStrip;
import org.synyx.sybil.out.SingleStatusOnLEDStrip;
import org.synyx.sybil.out.SingleStatusOutput;

import java.io.IOException;


/**
 * DevConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@Profile("dev")
public class SpringConfigDev {

    private static final Logger LOG = LoggerFactory.getLogger(SpringConfigDev.class);

    @Bean
    public IPConnection ipConnection() throws AlreadyConnectedException, IOException {

        String hostname = "localhost";
        int port = 4223;

        LOG.info("Creating IPConnection to {}:{}", hostname, port);

        IPConnection ipConnection = new IPConnection();
        ipConnection.connect(hostname, port);

        LOG.info("Successfully connected to {}:{}", hostname, port);

        return ipConnection;
    }


    @Bean
    public BrickletLEDStrip brickletLEDStrip(IPConnection ipConnection) throws TimeoutException, NotConnectedException {

        BrickletLEDStrip brickletLEDStrip = new BrickletLEDStrip("p3c", ipConnection);
        brickletLEDStrip.setFrameDuration(10);
        brickletLEDStrip.setChipType(2812);

        return brickletLEDStrip;
    }


    @Bean
    public OutputLEDStrip outputLEDStrip(BrickletLEDStrip brickletLEDStrip) {

        return new OutputLEDStrip(brickletLEDStrip, 30);
    }


    @Bean
    public SingleStatusOutput singleStatusOutput(OutputLEDStrip outputLEDStrip) {

        return new SingleStatusOnLEDStrip(outputLEDStrip);
    }
}
