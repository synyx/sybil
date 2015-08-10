package org.synyx.sybil.jenkins.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.core.env.Environment;

import java.io.File;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JenkinsConfigRepositoryUnitTest {

    @Mock
    Environment environmentMock;

    @Mock
    ObjectMapper objectMapperMock;

    JenkinsConfigRepository sut;

    List<JobConfig> jobs;
    List<ServerConfig> authorizations;

    @Before
    public void setUp() throws Exception {

        when(environmentMock.getProperty("path.to.configfiles")).thenReturn("/path/");
        when(environmentMock.getProperty("jenkins.configfile")).thenReturn("jenkinsconfig.json");

        authorizations = Arrays.asList(new ServerConfig("http://jenkins", "user", "key"));

        when(objectMapperMock.readValue(eq(new File("jenkinsconfig.json")), any(TypeReference.class))).thenReturn(
            authorizations);

        JobConfig job0 = new JobConfig("jobundefined", "ledstripone");
        JobConfig job1 = new JobConfig("jobokay", "ledstripone");
        JobConfig job2 = new JobConfig("jobokay", "ledstriptwo");
        JobConfig job3 = new JobConfig("jobokay", "ledstripthree");

        JobConfig job4 = new JobConfig("jobwarning", "ledstriptwo");
        JobConfig job5 = new JobConfig("jobwarning", "ledstripthree");

        JobConfig job6 = new JobConfig("jobcritical", "ledstripthree");

        JobConfig job7 = new JobConfig("job_blink_warning", "ledstripfour");
        JobConfig job8 = new JobConfig("job_blink_critical", "ledstripfive");

        jobs = Arrays.asList(job0, job1, job2, job3, job6, job5, job4, job7, job8);

        Map<String, List<JobConfig>> configuredJobs = new HashMap<>();
        configuredJobs.put("http://jenkins", jobs);

        when(objectMapperMock.readValue(eq(new File("/path/jenkins.json")), any(TypeReference.class))).thenReturn(
            configuredJobs);

        sut = new JenkinsConfigRepository(objectMapperMock, environmentMock);
    }


    @Test
    public void loadJobConfigs() throws Exception {

        Map<String, List<JobConfig>> result = sut.loadJobConfigs();

        assertThat(result.get("http://jenkins"), is(jobs));
    }


    @Test
    public void loadServerConfigs() throws Exception {

        List<ServerConfig> result = sut.loadServerConfigs();

        assertThat(result, is(authorizations));
    }
}
