package org.synyx.sybil.jenkins;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.jenkins.domain.JenkinsJob;
import org.synyx.sybil.jenkins.domain.JenkinsProperties;
import org.synyx.sybil.jenkins.domain.JobConfig;
import org.synyx.sybil.jenkins.domain.ServerConfig;
import org.synyx.sybil.jenkins.domain.Status;
import org.synyx.sybil.jenkins.domain.StatusInformation;

import java.io.File;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JenkinsServiceUnitTest {

    private JenkinsService sut;

    @Mock
    ObjectMapper objectMapperMock;

    @Mock
    LEDStripService ledStripServiceMock;

    @Mock
    Environment environmentMock;

    @Mock
    RestTemplate restTemplateMock;

    @Before
    public void setUp() throws Exception {

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("/path/");
        when(environmentMock.getProperty("jenkins.configfile")).thenReturn("jenkinsconfig.json");

        List<ServerConfig> authorizations = Arrays.asList(new ServerConfig("http://jenkins", "user", "key"));

        when(objectMapperMock.readValue(eq(new File("jenkinsconfig.json")), any(TypeReference.class))).thenReturn(
            authorizations);

        JobConfig job0 = new JobConfig("jobundefined", "ledstripone");
        JobConfig job1 = new JobConfig("jobokay", "ledstripone");
        JobConfig job2 = new JobConfig("jobokay", "ledstriptwo");
        JobConfig job3 = new JobConfig("jobokay", "ledstripthree");

        JobConfig job4 = new JobConfig("jobwarning", "ledstriptwo");
        JobConfig job5 = new JobConfig("jobwarning", "ledstripthree");

        JobConfig job6 = new JobConfig("jobcritical", "ledstripthree");

        List<JobConfig> jobs = Arrays.asList(job0, job1, job2, job3, job6, job5, job4);

        Map<String, List<JobConfig>> configuredJobs = new HashMap<>();
        configuredJobs.put("http://jenkins", jobs);

        when(objectMapperMock.readValue(eq(new File("/path/jenkins.json")), any(TypeReference.class))).thenReturn(
            configuredJobs);

        JenkinsJob jobOkay = new JenkinsJob("jobokay", "blue");
        JenkinsJob jobWarning = new JenkinsJob("jobwarning", "yellow");
        JenkinsJob jobCritical = new JenkinsJob("jobcritical", "red");
        JenkinsJob jobUndefined = new JenkinsJob("jobundefined", "grey");

        JenkinsProperties jenkinsProperties = new JenkinsProperties(
                new JenkinsJob[] { jobOkay, jobWarning, jobCritical, jobUndefined });

        ResponseEntity<JenkinsProperties> responseEntity = new ResponseEntity<>(jenkinsProperties, HttpStatus.OK);

        when(restTemplateMock.exchange(eq("http://jenkins/api/json"), eq(HttpMethod.GET), any(HttpEntity.class),
                    eq(JenkinsProperties.class))).thenReturn(responseEntity);

        sut = new JenkinsService(objectMapperMock, ledStripServiceMock, environmentMock, restTemplateMock);
    }


    @Test
    public void runScheduled() throws Exception {

        // execution
        sut.runScheduled();

        // verification
        ArgumentCaptor<StatusInformation> argumentCaptor = ArgumentCaptor.forClass(StatusInformation.class);

        verify(ledStripServiceMock).handleStatus(eq("ledstripone"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(Status.OKAY));

        verify(ledStripServiceMock).handleStatus(eq("ledstriptwo"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(Status.WARNING));

        verify(ledStripServiceMock).handleStatus(eq("ledstripthree"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(Status.CRITICAL));

        verifyNoMoreInteractions(ledStripServiceMock);
    }


    @Test
    public void turnOffAllLEDStrips() throws Exception {

        // execution
        sut.turnOffAllLEDStrips();

        // verification
        verify(ledStripServiceMock).turnOffAllLEDStrips();
    }
}
