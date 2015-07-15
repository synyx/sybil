package org.synyx.sybil.brick;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.synyx.sybil.AttributeEmptyException;
import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.brick.domain.BrickDTO;
import org.synyx.sybil.brick.domain.BrickDomain;

import java.io.IOException;

import java.util.List;

import javax.annotation.PostConstruct;


/**
 * BrickService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Service
public class BrickService {

    private static final Logger LOG = LoggerFactory.getLogger(BrickService.class);

    BrickDTOService brickDTOService;

    @Autowired
    public BrickService(BrickDTOService brickDTOService) {

        this.brickDTOService = brickDTOService;
    }

    public IPConnection connect(BrickDTO brickDTO) throws AlreadyConnectedException, IOException {

        BrickDomain brickDomain = brickDTO.getDomain();
        IPConnection ipConnection = new IPConnection();
        ipConnection.connect(brickDomain.getHostname(), brickDomain.getPort());

        return ipConnection;
    }


    @PostConstruct
    public void resetAllBricks() {

        try {
            List<BrickDTO> brickDTOs = brickDTOService.getAllDTOs();

            for (BrickDTO brickDTO : brickDTOs) {
                reset(brickDTO);
            }
        } catch (NotConnectedException | TimeoutException | AlreadyConnectedException | IOException
                | AttributeEmptyException | LoadFailedException exception) {
            LOG.error("Failed to reset bricks:", exception);
        }
    }


    private void reset(BrickDTO brickDTO) throws IOException, AlreadyConnectedException, TimeoutException,
        NotConnectedException {

        BrickDomain brickDomain = brickDTO.getDomain();

        IPConnection ipConnection = connect(brickDTO);

        BrickMaster brickMaster = new BrickMaster(brickDomain.getUid(), ipConnection);

        brickMaster.reset();

        ipConnection.disconnect();
    }
}
