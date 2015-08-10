package org.synyx.sybil.jenkins.service;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.synyx.sybil.LoadFailedException;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripConnectionException;
import org.synyx.sybil.bricklet.output.ledstrip.service.LEDStripService;
import org.synyx.sybil.jenkins.persistence.JenkinsConfigRepository;
import org.synyx.sybil.jenkins.persistence.JobConfig;
import org.synyx.sybil.jenkins.persistence.ServerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ JenkinsService.class, LoggerFactory.class })
public class JenkinsServiceUnitTest {

    private static Logger loggerMock;

    private JenkinsService sut;

    @Mock
    LEDStripService ledStripServiceMock;

    @Mock
    JenkinsConfigRepository jenkinsConfigRepositoryMock;

    @Mock
    RestTemplate restTemplateMock;

    @BeforeClass
    public static void staticSetup() {

        mockStatic(LoggerFactory.class);

        loggerMock = mock(Logger.class);

        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
    }


    @Before
    public void setUp() throws Exception {

        List<ServerConfig> authorizations = Arrays.asList(new ServerConfig("http://jenkins", "user", "key"));

        when(jenkinsConfigRepositoryMock.loadServerConfigs()).thenReturn(authorizations);

        JobConfig job0 = new JobConfig("jobundefined", "ledstripone");
        JobConfig job1 = new JobConfig("jobokay", "ledstripone");
        JobConfig job2 = new JobConfig("jobokay", "ledstriptwo");
        JobConfig job3 = new JobConfig("jobokay", "ledstripthree");

        JobConfig job4 = new JobConfig("jobwarning", "ledstriptwo");
        JobConfig job5 = new JobConfig("jobwarning", "ledstripthree");

        JobConfig job6 = new JobConfig("jobcritical", "ledstripthree");

        JobConfig job7 = new JobConfig("job_blink_warning", "ledstripfour");
        JobConfig job8 = new JobConfig("job_blink_critical", "ledstripfive");

        List<JobConfig> jobs = Arrays.asList(job0, job1, job2, job3, job6, job5, job4, job7, job8);

        Map<String, List<JobConfig>> configuredJobs = new HashMap<>();
        configuredJobs.put("http://jenkins", jobs);

        when(jenkinsConfigRepositoryMock.loadJobConfigs()).thenReturn(configuredJobs);

        JenkinsJob jobOkay = new JenkinsJob("jobokay", "blue");
        JenkinsJob jobWarning = new JenkinsJob("jobwarning", "yellow");
        JenkinsJob jobCritical = new JenkinsJob("jobcritical", "red");
        JenkinsJob jobUndefined = new JenkinsJob("jobundefined", "grey");
        JenkinsJob jobBlinkWarning = new JenkinsJob("job_blink_warning", "yellow_anime");
        JenkinsJob jobBlinkCritical = new JenkinsJob("job_blink_critical", "red_anime");

        JenkinsProperties jenkinsProperties = new JenkinsProperties(
                new JenkinsJob[] { jobOkay, jobWarning, jobCritical, jobUndefined, jobBlinkCritical, jobBlinkWarning });

        ResponseEntity<JenkinsProperties> responseEntity = new ResponseEntity<>(jenkinsProperties, HttpStatus.OK);

        when(restTemplateMock.exchange(eq("http://jenkins/api/json"), eq(HttpMethod.GET), any(HttpEntity.class),
                    eq(JenkinsProperties.class))).thenReturn(responseEntity);

        sut = new JenkinsService(ledStripServiceMock, restTemplateMock, jenkinsConfigRepositoryMock);
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

        verify(ledStripServiceMock).handleStatus(eq("ledstripfour"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(Status.WARNING));

        verify(ledStripServiceMock).handleStatus(eq("ledstripfive"), argumentCaptor.capture());
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


    @Test
    public void turnOffAllLEDStripsWithLoadFailedException() {

        doThrow(new LoadFailedException("Test 1")).when(ledStripServiceMock).turnOffAllLEDStrips();

        // Should log "Error turning off LED strips:"
        sut.turnOffAllLEDStrips();
    }


    @Test
    public void turnOffAllLEDStripsWithLEDStripConnectionException() {

        doThrow(new LEDStripConnectionException("Test 2")).when(ledStripServiceMock).turnOffAllLEDStrips();

        // Should log "Error turning off LED strips:"
        sut.turnOffAllLEDStrips();
    }


    @Test(expected = NullPointerException.class)
    public void turnOffAllLEDStripsWithNullPointerException() throws Exception {

        doThrow(new NullPointerException("Test 3")).when(ledStripServiceMock).turnOffAllLEDStrips();

        // Should not log anything but pass exception up
        sut.turnOffAllLEDStrips();
    }


    @Test
    public void getJobsFromJenkins() {

        when(restTemplateMock.exchange(eq("http://jenkins/api/json"), eq(HttpMethod.GET), any(HttpEntity.class),
                    eq(JenkinsProperties.class))).thenThrow(new RestClientException("Test 6"));

        // Should log "Error retrieving jobs from Jenkins:"
        sut.runScheduled();
    }


    @Test
    public void applyStatusesWithLoadFailedException() {

        doThrow(new LoadFailedException("Test 7")).when(ledStripServiceMock)
            .handleStatus(any(String.class), any(StatusInformation.class));

        // Should log "Error turning off LED strips:"
        sut.runScheduled();
    }


    @Test
    public void applyStatusesWithLEDStripConnectionException() {

        doThrow(new LEDStripConnectionException("Test 8")).when(ledStripServiceMock)
            .handleStatus(any(String.class), any(StatusInformation.class));

        // Should log "Error turning off LED strips:"
        sut.runScheduled();
    }


    @Test(expected = NullPointerException.class)
    public void applyStatusesWithNullPointerException() throws Exception {

        doThrow(new NullPointerException("Test 9")).when(ledStripServiceMock)
            .handleStatus(any(String.class), any(StatusInformation.class));

        // Should not log anything but pass exception up
        sut.runScheduled();
    }


    @Test
    public void noJobsConfigured() throws Exception {

        Map<String, List<JobConfig>> configuredJobs = new HashMap<>();

        when(jenkinsConfigRepositoryMock.loadJobConfigs()).thenReturn(configuredJobs);

        sut.runScheduled();

        // No jobs configured = no LED Strip interaction
        verifyNoMoreInteractions(ledStripServiceMock);
    }


    @Test
    public void noJobsConfiguredForLEDStrips() throws Exception {

        Map<String, List<JobConfig>> configuredJobs = new HashMap<>();

        List<JobConfig> jobs = new ArrayList<>();
        configuredJobs.put("http://jenkins", jobs);

        when(jenkinsConfigRepositoryMock.loadJobConfigs()).thenReturn(configuredJobs);

        sut.runScheduled();

        // No jobs configured for any of the configured LED strips = no LED Strip interaction
        verifyNoMoreInteractions(ledStripServiceMock);
    }


    @AfterClass
    public static void verifyStatic() {

        // Only two of the three exceptions thrown with this error message should be logged!
        verify(loggerMock, times(2)).error(eq("Error turning off LED strips:"), any(RuntimeException.class));

        verify(loggerMock).error(eq("Error retrieving jobs from Jenkins:"), any(RuntimeException.class));

        // Only 2 of the 3 exceptions should be thrown, but each 1 time for each of 5 ledstrips, so 10 times altogether
        verify(loggerMock, times(10)).error(eq("Error setting status on LED strip:"), any(RuntimeException.class));
    }
}
