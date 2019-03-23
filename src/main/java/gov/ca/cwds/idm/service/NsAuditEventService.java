package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;

public interface NsAuditEventService {

  NsAuditEvent save(NsAuditEvent nsAuditEvent);

  NsAuditEvent markAsUnprocessed(String id);
}
