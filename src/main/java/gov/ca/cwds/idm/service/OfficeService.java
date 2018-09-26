package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.isCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isOfficeAdmin;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_GET_MANAGED_OFFICES_LIST;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.Office;
import gov.ca.cwds.idm.persistence.cwscms.repository.OfficeRepository;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class OfficeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OfficeService.class);

  private OfficeRepository officeRepository;

  private MessagesService messagesService;

  @Transactional(value = "transactionManager")
  public List<Office> getOffices() {
    return officeRepository.findOffices();
  }

  @Transactional(value = "transactionManager")
  public List<Office> getOfficesByAdmin() {
    UniversalUserToken admin = getCurrentUser();

    if (isMostlyStateAdmin(admin)) {
      return officeRepository.findOffices();

    } else if (isOfficeAdmin(admin) || isCountyAdmin(admin)) {
      String adminCountyName = getCountyName(admin);
      return officeRepository.findCountyOffices(adminCountyName);

    } else {
      String msg = messagesService
          .get(NOT_AUTHORIZED_TO_GET_MANAGED_OFFICES_LIST, admin.getUserId(), admin.getRoles());
      LOGGER.error(msg);
      throw new AccessDeniedException(msg);
    }
  }

  @Autowired
  public void setOfficeRepository(
      OfficeRepository officeRepository) {
    this.officeRepository = officeRepository;
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }
}
