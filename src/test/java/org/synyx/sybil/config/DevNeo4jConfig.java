package org.synyx.sybil.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;


/**
 * Configures the Neo4j database via Spring.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Profile("dev")
@Configuration
@EnableNeo4jRepositories(
    basePackages = { "org.synyx.sybil.database", "org.synyx.sybil.brick.database" }
) // this is where the repositories are
public class DevNeo4jConfig extends Neo4jConfiguration {

    public DevNeo4jConfig() {

        setBasePackage("org.synyx.sybil.domain", "org.synyx.sybil.out", "org.synyx.sybil.brick.database"); // this is where the domain classes are
    }

    @Bean
    public GraphDatabaseService graphDatabaseService() {

        return new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/sybildevdb");
    }
}
