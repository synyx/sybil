package org.synyx.sybil.bricklet.output.ledstrip.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LEDStripRepositoryUnitTest {

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    Environment environmentMock;

    private LEDStripRepository sut;

    private List<LEDStrip> ledStrips = new ArrayList<>();

    @Before
    public void setup() throws Exception {

        LEDStrip one = new LEDStrip("one", "abc", 42, "abrick");
        LEDStrip two = new LEDStrip("two", "xyz", 23, "anotherbrick");

        ledStrips.add(one);
        ledStrips.add(two);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("path/to/config/files/");
        when(objectMapperMock.readValue(eq(new File("path/to/config/files/ledstrips.json")), any(TypeReference.class)))
            .thenReturn(ledStrips);

        sut = new LEDStripRepository(objectMapperMock, environmentMock);
    }


    @Test
    public void get() throws Exception {

        LEDStrip ledStrip = sut.get("one");

        assertThat(ledStrip, is(ledStrips.get(0)));
    }


    @Test
    public void getNone() throws Exception {

        LEDStrip ledStrip = sut.get("none");

        assertThat(ledStrip, is(nullValue()));
    }


    @Test
    public void getAll() throws Exception {

        List<LEDStrip> result = sut.getAll();

        assertThat(result, is(ledStrips));
    }
}
