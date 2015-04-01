package org.synyx.sybil.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;


/**
 * Configures the Neo4j database via Spring.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Configuration
@EnableNeo4jRepositories(basePackages = "org.synyx.sybil.database") // this is where the repositories are
public class Neo4jConfig extends Neo4jConfiguration {

    public Neo4jConfig() {

        setBasePackage("org.synyx.sybil.domain", "org.synyx.sybil.out"); // this is where the domain classes are
    }

    // Development Profile Database
    // @Profile("dev")
    // TODO Profiles & files!
    @Bean
    public GraphDatabaseService graphDatabaseService() {

        return new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/sybildevdb");
    }
}
