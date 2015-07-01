package org.synyx.sybil.brick;

import com.tinkerforge.IPConnection;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.runners.MockitoJUnitRunner;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import org.synyx.sybil.brick.database.BrickDomain;
import org.synyx.sybil.brick.database.BrickRepository;

import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class BrickServiceUnitTest {

    @Mock
    private BrickRepository brickRepository;

    @Mock
    private GraphDatabaseService graphDatabaseService;

    @Mock
    private Transaction transaction;

    @Mock
    BrickDomain brickDomain;

    @Mock
    List<BrickDomain> brickDomainList;

    @Mock
    IPConnection ipConnection;

    private BrickService brickService;

    @Before
    public void setup() {

        brickService = new BrickService(brickRepository, graphDatabaseService);

        Mockito.when(graphDatabaseService.beginTx()).thenReturn(transaction);

        Mockito.when(brickRepository.findByName("abc")).thenReturn(new BrickDomain("abc", "1234"));
    }


    @Test
    public void testGetDomain() throws Exception {

        brickService.getDomain("abc");

        Mockito.verify(brickRepository).findByName("abc");
    }


    @Test
    public void testGetAllDomains() throws Exception {

        brickService.getAllDomains();

        Mockito.verify(brickRepository).findAll();
    }


    @Test
    public void testDeleteAllDomains() throws Exception {

        brickService.deleteAllDomains();

        Mockito.verify(brickRepository).deleteAll();
    }


    @Test
    public void testSaveDomain() throws Exception {

        brickService.saveDomain(brickDomain);

        Mockito.verify(brickRepository).save(brickDomain);
    }


    @Test
    public void testSaveDomains() throws Exception {

        brickService.saveDomains(brickDomainList);

        Mockito.verify(brickRepository).save(brickDomainList);
    }
}
