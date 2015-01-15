package org.synyx.sybil.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;


/**
 * Neo4jConfig.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@EnableNeo4jRepositories(basePackages = "org.synyx.sybil.database")
public class Neo4jConfig extends Neo4jConfiguration {

    public Neo4jConfig() {

        setBasePackage("org.synyx.sybil.domain");
    }

    @Profile("dev")
    @Bean
    public GraphDatabaseService graphDatabaseService() {

        return new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/sybildevdb");
    }
}
