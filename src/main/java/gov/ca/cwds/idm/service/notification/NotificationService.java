package gov.ca.cwds.idm.service.notification;

import static gov.ca.cwds.service.messages.MessageCode.IDM_NOTIFY_UNSUPPORTED_OPERATION;

import gov.ca.cwds.idm.dto.IdmUserNotification;
import gov.ca.cwds.idm.dto.NotificationType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.AuditEventService;
import gov.ca.cwds.idm.service.UserService;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.search.UserSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuditEventService auditService;

  @Autowired
  private ExceptionFactory exceptionFactory;

  @Autowired
  private UserSearchService userSearchService;

  public void processUserNotification(IdmUserNotification notification) {

    String notificationStr = notification.getActionType();
    LOGGER.info("notification of type {} is received", notificationStr);
    NotificationType notificationType = NotificationType.forString(notificationStr);

    if(notificationType == null) {
      throw exceptionFactory
          .createOperationNotSupportedException(IDM_NOTIFY_UNSUPPORTED_OPERATION, notificationStr);
    }
    User user = userService.getUser(notification.getUserId());
    auditService.saveAuditEvent(notificationType.createAuditEvent(user));
    userSearchService.updateUserInSearch(user);
  }
}
