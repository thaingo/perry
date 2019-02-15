package gov.ca.cwds.idm.persistence.ns.repository;

import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
public interface NsAuditEventRepository extends
    CrudRepository<NsAuditEvent, String> {

}
