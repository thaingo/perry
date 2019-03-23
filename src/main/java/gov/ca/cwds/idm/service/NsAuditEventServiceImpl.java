package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;

import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.idm.persistence.ns.repository.NsAuditEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("idm")
@Service
public class NsAuditEventServiceImpl implements NsAuditEventService {

  @Autowired
  private NsAuditEventRepository nsAuditEventRepository;

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public NsAuditEvent save(NsAuditEvent nsAuditEvent) {
    return nsAuditEventRepository.save(nsAuditEvent);
  }

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public NsAuditEvent markAsUnprocessed(String id) {
    NsAuditEvent event = nsAuditEventRepository.findOne(id);
    event.setProcessed(false);
    return nsAuditEventRepository.save(event);
  }
}
