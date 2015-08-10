package org.synyx.sybil.jenkins.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;

import org.springframework.stereotype.Repository;

import org.synyx.sybil.LoadFailedException;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;


/**
 * JenkinsConfigRepository.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Repository
public class JenkinsConfigRepository {

    private String configDirectory;
    private String jenkinsServerConfigFile;
    private ObjectMapper objectMapper;

    @Autowired
    public JenkinsConfigRepository(ObjectMapper objectMapper, Environment environment) {

        this.objectMapper = objectMapper;
        configDirectory = environment.getProperty("path.to.configfiles");
        jenkinsServerConfigFile = environment.getProperty("jenkins.configfile");
    }

    public Map<String, List<JobConfig>> loadJobConfigs() {

        try {
            return objectMapper.readValue(new File(configDirectory + "jenkins.json"),
                    new TypeReference<Map<String, List<JobConfig>>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading jenkins config:", exception);
        }
    }


    public List<ServerConfig> loadServerConfigs() {

        try {
            return objectMapper.readValue(new File(jenkinsServerConfigFile), new TypeReference<List<ServerConfig>>() {
                    });
        } catch (IOException exception) {
            throw new LoadFailedException("Error loading jenkins server config:", exception);
        }
    }
}
