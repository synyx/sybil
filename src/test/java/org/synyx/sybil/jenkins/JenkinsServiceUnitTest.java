package org.synyx.sybil.jenkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.AppDestroyer;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;
import org.synyx.sybil.jenkins.domain.ConfiguredJob;
import org.synyx.sybil.jenkins.domain.ConfiguredServer;
import org.synyx.sybil.jenkins.domain.JenkinsJob;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JenkinsServiceUnitTest {

    JenkinsService sut;

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    LEDStripDTOService ledStripDTOServiceMock;

    @Mock
    LEDStripService ledStripServiceMock;

    @Mock
    Environment environmentMock;

    @Mock
    RestTemplate restTemplateMock;

    @Spy
    LEDStripDTO ledStripDTOOneMock;

    @Spy
    LEDStripDTO ledStripDTOTwoMock;

    @Spy
    LEDStripDTO ledStripDTOThreeMock;

    @Mock
    AppDestroyer appDestroyer;

    @Before
    public void setUp() throws Exception {

        ConfiguredServer configuredServer = new ConfiguredServer("http://jenkins", "user", "key");
        List<ConfiguredServer> authorizations = new ArrayList<>();
        authorizations.add(configuredServer);

        when(objectMapperMock.readValue(eq(new File("jenkinsconfig.json")), any(TypeReference.class))).thenReturn(
            authorizations);

        ConfiguredJob job1 = new ConfiguredJob("jobokay", "ledstripone");
        ConfiguredJob job2 = new ConfiguredJob("jobokay", "ledstriptwo");
        ConfiguredJob job3 = new ConfiguredJob("jobokay", "ledstripthree");

        ConfiguredJob job4 = new ConfiguredJob("jobwarning", "ledstriptwo");
        ConfiguredJob job5 = new ConfiguredJob("jobwarning", "ledstripthree");

        ConfiguredJob job6 = new ConfiguredJob("jobcritical", "ledstripthree");

        List<ConfiguredJob> jobs = Arrays.asList(job1, job2, job3, job4, job5, job6);

        Map<String, List<ConfiguredJob>> configuredJobs = new HashMap<>();
        configuredJobs.put("http://jenkins", jobs);

        when(objectMapperMock.readValue(eq(new File("/path/jenkins.json")), any(TypeReference.class))).thenReturn(
            configuredJobs);

        when(ledStripDTOServiceMock.getDTO("ledstripone")).thenReturn(ledStripDTOOneMock);
        when(ledStripDTOServiceMock.getDTO("ledstriptwo")).thenReturn(ledStripDTOTwoMock);
        when(ledStripDTOServiceMock.getDTO("ledstripthree")).thenReturn(ledStripDTOThreeMock);

        List<LEDStripDTO> ledStripDTOs = Arrays.asList(ledStripDTOOneMock, ledStripDTOTwoMock, ledStripDTOThreeMock);
        when(ledStripDTOServiceMock.getAllDTOs()).thenReturn(ledStripDTOs);

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("/path/");
        when(environmentMock.getProperty("jenkins.configfile")).thenReturn("jenkinsconfig.json");

        JenkinsJob jobOkay = new JenkinsJob("jobokay", "blue");
        JenkinsJob jobWarning = new JenkinsJob("jobwarning", "yellow");
        JenkinsJob jobCritical = new JenkinsJob("jobcritical", "red");

        JenkinsProperties jenkinsProperties = new JenkinsProperties(
                new JenkinsJob[] { jobOkay, jobWarning, jobCritical });

        ResponseEntity<JenkinsProperties> responseEntity = new ResponseEntity<>(jenkinsProperties, HttpStatus.OK);

        when(restTemplateMock.exchange(eq("http://jenkins/api/json"), eq(HttpMethod.GET), any(HttpEntity.class),
                    eq(JenkinsProperties.class))).thenReturn(responseEntity);

        sut = new JenkinsService(objectMapperMock, ledStripServiceMock, ledStripDTOServiceMock, environmentMock,
                restTemplateMock, appDestroyer);
    }


    @Test
    public void runEveryMinute() throws TimeoutException, NotConnectedException {

        // execution
        sut.runEveryMinute();

        // verify
        InOrder inOrder = inOrder(ledStripServiceMock, ledStripDTOOneMock);

        inOrder.verify(ledStripDTOOneMock).setStatus(any(StatusInformation.class));
        assertThat(ledStripDTOOneMock.getStatus().getStatus(), is(Status.OKAY));
        inOrder.verify(ledStripServiceMock).handleStatus(ledStripDTOOneMock);

        verify(ledStripDTOTwoMock).setStatus(any(StatusInformation.class));
        assertThat(ledStripDTOTwoMock.getStatus().getStatus(), is(Status.WARNING));
        verify(ledStripServiceMock).handleStatus(ledStripDTOTwoMock);

        verify(ledStripDTOThreeMock).setStatus(any(StatusInformation.class));
        assertThat(ledStripDTOThreeMock.getStatus().getStatus(), is(Status.CRITICAL));
        verify(ledStripServiceMock).handleStatus(ledStripDTOThreeMock);
    }


    @Test
    public void destroyContext() throws Exception {

        // execution
        sut.destroyContext();

        verify(appDestroyer, times(1)).turnOffAllLEDStrips();
    }
}
