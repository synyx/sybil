package org.synyx.sybil.bricklet.input.illuminance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceConfig;
import org.synyx.sybil.bricklet.input.illuminance.domain.IlluminanceDTO;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;


/**
 * IlluminanceDTOServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class IlluminanceDTOServiceUnitTest {

    private IlluminanceDTOService sut;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private Environment environmentMock;

    private List<IlluminanceConfig> illuminanceConfigs = new ArrayList<>();

    @Before
    public void setup() throws IOException {

        IlluminanceConfig one = new IlluminanceConfig();
        one.setName("one");
        one.setUid("abc");
        one.setThreshold(16);
        one.setMultiplier(.05);
        one.setBrick("abrick");

        IlluminanceConfig two = new IlluminanceConfig("two", "xyz", 32, 1.0, "anotherbrick");

        illuminanceConfigs.add(one);
        illuminanceConfigs.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/illuminances.json")),
                    any(TypeReference.class))).thenReturn(illuminanceConfigs);

        sut = new IlluminanceDTOService(objectMapperMock, environmentMock);
    }


    @Test
    public void getConfiguredDTO() throws Exception {

        IlluminanceDTO illuminanceDTO = sut.getDTO("two");

        assertThat(illuminanceDTO.getConfig(), is(illuminanceConfigs.get(1)));
    }


    @Test(expected = IlluminanceNotFoundException.class)
    public void getNonexistentIlluminanceDTO() throws Exception {

        sut.getDTO("does not exist");
    }


    @Test
    public void getAllDTOs() throws Exception {

        List<IlluminanceDTO> illuminanceDTOs = sut.getAllDTOs();
        assertThat(illuminanceDTOs.get(0).getConfig(), is(illuminanceConfigs.get(0)));

        IlluminanceConfig illuminanceConfig = illuminanceDTOs.get(1).getConfig();

        assertThat(illuminanceConfig.getName(), is("two"));
        assertThat(illuminanceConfig.getUid(), is("xyz"));
        assertThat(illuminanceConfig.getThreshold(), is(32));
        assertThat(illuminanceConfig.getMultiplier(), is(1.0));
        assertThat(illuminanceConfig.getBrick(), is("anotherbrick"));
    }
}
