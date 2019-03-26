package gov.ca.cwds.idm.service.search;

import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;

import gov.ca.cwds.idm.event.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class AuditEventIndexService extends BaseSearchIndexService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventIndexService.class);

  public <T extends AuditEvent> ResponseEntity<String> createAuditEventInIndex(T event) {
    final String eventId = event.getId();

    try {
      ResponseEntity<String> response =
          sendToIndex(event, eventId, CREATE, searchProperties.getAuditIndex());

      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(
            "Audit record, id:{} was successfully stored in Elastic Search index, Dora response string is:{}",
            eventId,
            response.getBody());
      }
      return response;
    } catch (Exception e) {
      LOGGER.error("Error while storing the audit event {} for user {}.",
          event.getEventType(),
          event.getUserLogin());
      throw e;
    }
  }
}
