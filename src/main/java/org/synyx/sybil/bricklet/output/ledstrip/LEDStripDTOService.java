package org.synyx.sybil.bricklet.output.ledstrip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDomain;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * LEDStripDTOService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Component
public class LEDStripDTOService {

    private final ObjectMapper objectMapper;
    private final String configDir;
    private final LEDStripService ledStripService;

    /**
     * Instantiates a new LED strip DTO service.
     *
     * @param  objectMapper  Jackson ObjectMapper
     * @param  environment  Spring Environment
     * @param  ledStripService  Sybil LED strip service
     */
    @Autowired
    public LEDStripDTOService(ObjectMapper objectMapper, Environment environment, LEDStripService ledStripService) {

        this.objectMapper = objectMapper;
        this.ledStripService = ledStripService;
        this.configDir = environment.getProperty("path.to.configfiles");
    }

    /**
     * Handle DTO with Status.
     *
     * @param  name  the name
     * @param  statusInformation  the status
     */
    public void handleStatus(String name, StatusInformation statusInformation) {

        LEDStripDTO ledStripDTO = getDTO(name);
        ledStripDTO.setStatus(statusInformation);
        ledStripService.handleStatus(ledStripDTO);
    }


    /**
     * Handle DTO with Sprite.
     *
     * @param  name  the name
     * @param  sprite1D  the sprite
     */
    public void handleSprite(String name, Sprite1D sprite1D) {

        LEDStripDTO ledStripDTO = getDTO(name);
        ledStripDTO.setSprite(sprite1D);
        ledStripService.handleSprite(ledStripDTO);
    }


    /**
     * Get pixels.
     *
     * @param  name  the name
     *
     * @return  List of Colors (pixels).
     */
    public List<Color> getPixels(String name) {

        LEDStripDTO ledStripDTO = getDTO(name);

        return ledStripService.getPixels(ledStripDTO);
    }


    /**
     * Turn off all LED strips.
     */
    public void turnOffAllLEDStrips() {

        List<LEDStripDTO> ledstrips = getAllDTOs();

        for (LEDStripDTO ledStripDTO : ledstrips) {
            ledStripService.turnOff(ledStripDTO);
        }
    }


    private LEDStripDTO getDTO(String name) {

        LEDStripDTO ledStripDTO = null;

        for (LEDStripDomain ledStripDomain : getLedStripDomains()) {
            if (ledStripDomain.getName().equals(name)) {
                ledStripDTO = new LEDStripDTO(ledStripDomain);
            }
        }

        if (ledStripDTO == null) {
            throw new LEDStripNotFoundException("LED strip " + name + " is not configured.");
        }

        return ledStripDTO;
    }


    private List<LEDStripDTO> getAllDTOs() {

        List<LEDStripDTO> ledStripDTOs = new ArrayList<>();

        for (LEDStripDomain ledStripDomain : getLedStripDomains()) {
            ledStripDTOs.add(new LEDStripDTO(ledStripDomain));
        }

        return ledStripDTOs;
    }


    private List<LEDStripDomain> getLedStripDomains() {

        List<LEDStripDomain> ledStripDomains;

        try {
            ledStripDomains = objectMapper.readValue(new File(configDir + "ledstrips.json"),
                    new TypeReference<List<LEDStripDomain>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading LED strips config file:", exception);
        }

        return ledStripDomains;
    }
}
