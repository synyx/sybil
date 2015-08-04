package org.synyx.sybil.bricklet.input.illuminance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.synyx.sybil.bricklet.input.illuminance.domain.Illuminance;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;


/**
 * IlluminanceDTOServiceUnitTest.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@RunWith(MockitoJUnitRunner.class)
public class IlluminanceRepositoryUnitTest {

    private IlluminanceRepository sut;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private Environment environmentMock;

    private List<Illuminance> illuminances = new ArrayList<>();

    @Before
    public void setup() throws IOException {

        Illuminance one = new Illuminance();
        one.setName("one");
        one.setUid("abc");
        one.setThreshold(16);
        one.setMultiplier(.05);
        one.setBrick("abrick");

        Illuminance two = new Illuminance("two", "xyz", 32, 1.0, "anotherbrick");

        illuminances.add(one);
        illuminances.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/illuminances.json")),
                    any(TypeReference.class))).thenReturn(illuminances);

        sut = new IlluminanceRepository(objectMapperMock, environmentMock);
    }


    @Test
    public void get() throws Exception {

        Illuminance illuminance = sut.get("two");

        assertThat(illuminance, is(illuminances.get(1)));
    }


    @Test
    public void getNonexistentIlluminance() throws Exception {

        // execution
        Illuminance illuminance = sut.get("does_not_exist");

        assertThat(illuminance, is(nullValue()));
    }


    @Test
    public void getAll() throws Exception {

        List<Illuminance> illuminanceList = sut.getAll();
        assertThat(illuminanceList.get(0), is(illuminances.get(0)));

        Illuminance illuminance = illuminanceList.get(1);

        assertThat(illuminance.getName(), is("two"));
        assertThat(illuminance.getUid(), is("xyz"));
        assertThat(illuminance.getThreshold(), is(32));
        assertThat(illuminance.getMultiplier(), is(1.0));
        assertThat(illuminance.getBrick(), is("anotherbrick"));
    }
}
