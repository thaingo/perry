package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.IdmServiceImpl.transformSearchValues;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;
import static gov.ca.cwds.service.messages.MessageCode.DUPLICATE_USERID_FOR_RACFID_IN_CWSCMS;
import static gov.ca.cwds.util.Utils.isRacfidUser;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUserPage;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil;
import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  private CwsUserInfoService cwsUserInfoService;

  @Autowired
  private MessagesService messages;

  @Autowired
  private NsUserService nsUserService;

  @Autowired
  private MappingService mappingService;


  public User getUser(String id) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(id);
    String userId = cognitoUser.getUsername();
    String racfId = getRACFId(cognitoUser);

    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    NsUser nsUser = nsUserService.findByUsername(userId).orElse(null);

    return mappingService.toUser(cognitoUser, cwsUser, nsUser);
  }

  public UsersPage getUserPage(String paginationToken) {
    CognitoUserPage userPage =
        cognitoServiceFacade.searchPage(
            CognitoUsersSearchCriteriaUtil.composeToGetPage(paginationToken));
    List<User> users = enrichCognitoUsers(userPage.getUsers());
    return new UsersPage(users, userPage.getPaginationToken());
  }

  public User enrichWithCwsData(final User userDto) {
    CwsUserInfo cwsUser = getCwsUserData(userDto);
    enrichUserByCwsData(userDto, cwsUser);
    return userDto;
  }

  public User createUser(User userDto) {
    UserType cognitoUser = cognitoServiceFacade.createUser(userDto);
    if (cognitoUser != null) {
      userDto.setId(cognitoUser.getUsername());
      userDto.setUserCreateDate(cognitoUser.getUserCreateDate());
      userDto.setUserLastModifiedDate(cognitoUser.getUserLastModifiedDate());
    }
    return userDto;
  }

  public List<User> searchUsers(UsersSearchCriteria criteria) {
    StandardUserAttribute searchAttr = criteria.getSearchAttr();
    Set<String> values = transformSearchValues(criteria.getValues(), searchAttr);

    List<UserType> cognitoUsers = new ArrayList<>();

    for (String value : values) {
      CognitoUsersSearchCriteria cognitoSearchCriteria =
          CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByAttribute(searchAttr, value);
      cognitoUsers.addAll(cognitoServiceFacade.searchAllPages(cognitoSearchCriteria));
    }
    return enrichCognitoUsers(cognitoUsers);
  }

  private List<User> enrichCognitoUsers(Collection<UserType> cognitoUsers) {
    if (CollectionUtils.isEmpty(cognitoUsers)) {
      return Collections.emptyList();
    }
    Map<String, String> userNameToRacfId = new HashMap<>(cognitoUsers.size());
    for (UserType userType : cognitoUsers) {
      userNameToRacfId.put(userType.getUsername(), getRACFId(userType));
    }
    Set<String> userNames = userNameToRacfId.keySet();
    Collection<String> racfIds = userNameToRacfId.values();

    Map<String, CwsUserInfo> racfidToCmsUser = cwsUserInfoService.findUsers(racfIds)
        .stream().collect(
            Collectors.toMap(CwsUserInfo::getRacfId, e -> e, (user1, user2) -> {
              LOGGER.warn(messages
                  .getTechMessage(DUPLICATE_USERID_FOR_RACFID_IN_CWSCMS, user1.getRacfId()));
              return user1;
            }));

    Map<String, NsUser> usernameToNsUser =
        nsUserService.findByUsernames(userNames).stream()
            .collect(Collectors.toMap(NsUser::getUsername, e -> e));
    return cognitoUsers
        .stream()
        .map(userType -> mappingService.toUser(
            userType,
            racfidToCmsUser.get(userNameToRacfId.get(userType.getUsername())),
            usernameToNsUser.get(userType.getUsername())
            )
        ).collect(Collectors.toList());
  }

  private void enrichUserByCwsData(User user, CwsUserInfo cwsUser) {
    if (cwsUser != null) {
      enrichDataFromCwsOffice(cwsUser.getCwsOffice(), user);
      enrichDataFromStaffPerson(cwsUser.getStaffPerson(), user);
    }
  }

  private void enrichDataFromStaffPerson(StaffPerson staffPerson, final User user) {
    if (staffPerson != null) {
      user.setFirstName(staffPerson.getFirstName());
      user.setLastName(staffPerson.getLastName());
      user.setEndDate(staffPerson.getEndDate());
      user.setStartDate(staffPerson.getStartDate());
      user.setPhoneNumber(staffPerson.getPhoneNumber());
      user.setPhoneExtensionNumber(staffPerson.getPhoneExtensionNumber());
    }
  }

  private void enrichDataFromCwsOffice(CwsOffice office, final User user) {
    if (office != null) {
      user.setOfficeId(office.getOfficeId());
      Optional.ofNullable(office.getPrimaryPhoneNumber())
          .ifPresent(e -> user.setOfficePhoneNumber(e.toString()));
      Optional.ofNullable(office.getPrimaryPhoneExtensionNumber())
          .ifPresent(user::setOfficePhoneExtensionNumber);
      Optional.ofNullable(office.getGovernmentEntityType())
          .ifPresent(
              x -> user.setCountyName((GovernmentEntityType.findBySysId(x)).getDescription()));
    }
  }

  private CwsUserInfo getCwsUserData(User user) {
    if (isRacfidUser(user)) {
      return cwsUserInfoService.getCwsUserByRacfId(user.getRacfid());
    } else {
      return null;
    }
  }

  public void setCognitoServiceFacade(
      CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }

  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }

  public void setNsUserService(NsUserService nsUserService) {
    this.nsUserService = nsUserService;
  }
}
