package gov.ca.cwds.idm.event;

import static gov.ca.cwds.util.AuditLogsUtil.composeAuditEvent;

import gov.ca.cwds.idm.dto.AuditEvent;
import gov.ca.cwds.idm.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Profile("idm")
public class ChangeLogEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeLogEventListener.class);


  @Autowired
  private AuditLogService auditLogService;

  @EventListener
  @Async
  public void handleChangeLogEvent(UserCreatedEvent event) {

    AuditEvent auditEvent = composeAuditEvent(event);

    try {
      auditLogService.createAuditLogRecord(auditEvent);
    } catch (Exception e) {
      LOGGER.error("Error while storing the audit event {} for user {}.", auditEvent.getEventType(),
          event.getUser().getId());
    }
  }

}
