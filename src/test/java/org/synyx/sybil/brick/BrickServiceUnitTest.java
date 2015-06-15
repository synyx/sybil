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
    }


    @Test
    public void testGetBrickDomain() throws Exception {

        brickService.getBrickDomain("abc");

        Mockito.verify(brickRepository).findByName("abc");
    }


    @Test
    public void testGetAllBrickDomains() throws Exception {

        brickService.getAllBrickDomains();

        Mockito.verify(brickRepository).findAll();
    }


    @Test
    public void testDeleteAllBrickDomains() throws Exception {

        brickService.deleteAllBrickDomains();

        Mockito.verify(brickRepository).deleteAll();
    }


    @Test
    public void testSaveBrickDomain() throws Exception {

        brickService.saveBrickDomain(brickDomain);

        Mockito.verify(brickRepository).save(brickDomain);
    }


    @Test
    public void testSaveBrickDomains() throws Exception {

        brickService.saveBrickDomains(brickDomainList);

        Mockito.verify(brickRepository).save(brickDomainList);
    }
}
