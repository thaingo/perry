package gov.ca.cwds.idm.service;

import static gov.ca.cwds.service.messages.MessageCode.IDM_MAPPING_SCRIPT_ERROR;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessagesService;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class MappingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MappingService.class);

  private PerryProperties configuration;

  private MessagesService messages;

  public User toUser(UserType cognitoUser, CwsUserInfo cwsUser) {
    try {
      return configuration.getIdentityManager().getIdmMapping().map(cognitoUser, cwsUser);
    } catch (ScriptException e) {
      LOGGER.error(messages.get(IDM_MAPPING_SCRIPT_ERROR));
      throw new PerryException(e.getMessage(), e);
    }
  }

  public User toUserWithoutCwsData(UserType cognitoUser) {
    return toUser(cognitoUser, null);
  }

  @Autowired
  public void setConfiguration(PerryProperties configuration) {
    this.configuration = configuration;
  }

  @Autowired
  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }
}
