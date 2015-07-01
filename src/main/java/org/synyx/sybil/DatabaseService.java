package org.synyx.sybil;

import java.util.List;


/**
 * DatabaseService.
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */
public interface DatabaseService {

    DeviceDomain saveDomain(DeviceDomain domainToSave);


    List<DeviceDomain> saveDomains(List<DeviceDomain> domainsToSave);


    DeviceDomain getDomain(String domainName);


    List<DeviceDomain> getAllDomains();


    void deleteDomain(DeviceDomain domainToDelete);


    void deleteAllDomains();
}
