package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.ExecutionStatus.FAIL;
import static gov.ca.cwds.idm.service.ExecutionStatus.SUCCESS;
import static gov.ca.cwds.idm.service.ExecutionStatus.WAS_NOT_EXECUTED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;
import static gov.ca.cwds.service.messages.MessageCode.DUPLICATE_USERID_FOR_RACFID_IN_CWSCMS;
import static gov.ca.cwds.service.messages.MessageCode.ERROR_UPDATE_USER_ENABLED_STATUS;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_CREATE_IDM_USER_IN_ES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_PURGE_PROCESSED_USER_LOGS;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_IDM_USER_IN_ES;
import static gov.ca.cwds.service.messages.MessageCode.USER_CREATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_CREATE_SAVE_TO_SEARCH_ERROR;
import static gov.ca.cwds.service.messages.MessageCode.USER_NOTHING_UPDATED;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_UPDATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS;
import static gov.ca.cwds.service.messages.MessageCode.USER_UPDATE_SAVE_TO_SEARCH_ERROR;
import static gov.ca.cwds.util.Utils.URL_DATETIME_FORMATTER;
import static gov.ca.cwds.util.Utils.isRacfidUser;
import static gov.ca.cwds.util.Utils.toLowerCase;
import static gov.ca.cwds.util.Utils.toUpperCase;
import static java.util.stream.Collectors.toSet;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.idm.dto.RegistrationResubmitResponse;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.dto.UserIdAndOperation;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import gov.ca.cwds.idm.event.EmailChangedEvent;
import gov.ca.cwds.idm.event.NotesChangedEvent;
import gov.ca.cwds.idm.event.PermissionsChangedEvent;
import gov.ca.cwds.idm.event.UserCreatedEvent;
import gov.ca.cwds.idm.event.UserEnabledStatusChangedEvent;
import gov.ca.cwds.idm.event.UserRegistrationResentEvent;
import gov.ca.cwds.idm.event.UserRoleChangedEvent;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.attribute.DatabaseDiffMapBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute;
import gov.ca.cwds.idm.service.cognito.attribute.UpdatedAttributesBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUserPage;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil;
import gov.ca.cwds.idm.service.diff.BooleanDiff;
import gov.ca.cwds.idm.service.diff.Diff;
import gov.ca.cwds.idm.service.diff.Differencing;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.execution.OptionalExecution;
import gov.ca.cwds.idm.service.execution.PutInSearchExecution;
import gov.ca.cwds.idm.service.validation.ValidationService;
import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
@SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
public class IdmServiceImpl implements IdmService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdmServiceImpl.class);

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  @Autowired
  private CwsUserInfoService cwsUserInfoService;

  @Autowired
  private MessagesService messages;

  @Autowired
  private UserLogService userLogService;

  @Autowired
  private SearchService searchService;

  @Autowired
  private AuthorizationService authorizeService;

  @Autowired
  private MappingService mappingService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private NsUserService nsUserService;

  @Autowired
  private ExceptionFactory exceptionFactory;

  @Autowired
  private AuditLogService auditLogService;

  @Autowired
  private DictionaryProvider dictionaryProvider;

  @Autowired
  private UserService userService;

  @Override
  public User findUser(String id) {
    User user = getUser(id);
    authorizeService.checkCanViewUser(user);
    return user;
  }

  private User getUser(String id) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(id);
    return mappingService.toUser(cognitoUser);
  }

  @Override
  public void updateUser(String userId, UserUpdate updateUserDto) {
    User existedUser = getUser(userId);

    authorizeService.checkCanUpdateUser(existedUser, updateUserDto);
    validationService.validateUserUpdate(existedUser, updateUserDto);

    UserUpdateRequest userUpdateRequest =
        prepareUserUpdateRequest(existedUser, updateUserDto);

    ExecutionStatus updateAttributesStatus = updateUserAttributes(userUpdateRequest);

    OptionalExecution<BooleanDiff, Void> updateUserEnabledExecution =
        updateUserEnabledStatus(userUpdateRequest);
    if (updateAttributesStatus == WAS_NOT_EXECUTED
        && updateUserEnabledExecution.getExecutionStatus() == FAIL) {
      throw (RuntimeException) updateUserEnabledExecution.getException();
    }

    PutInSearchExecution<String> doraExecution = null;
    if (doesElasticSearchNeedUpdate(updateAttributesStatus, updateUserEnabledExecution)) {
      doraExecution = updateUserInSearch(userId);
    } else {
      LOGGER.info(messages.getTechMessage(USER_NOTHING_UPDATED, userId));
    }

    handleUpdatePartialSuccess(
        userId, updateAttributesStatus, updateUserEnabledExecution, doraExecution);
  }

  private UserUpdateRequest prepareUserUpdateRequest(User existedUser, UserUpdate updateUserDto) {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

    userUpdateRequest.setUserId(existedUser.getId());
    userUpdateRequest.setExistedUser(existedUser);

    Map<UserAttribute, UserAttributeDiff> cognitoDiffMap =
        new UpdatedAttributesBuilder(existedUser, updateUserDto).buildUpdatedAttributesMap();
    userUpdateRequest.setCognitoDiffMap(cognitoDiffMap);

    Map<UserAttribute, Diff> databaseDiffMap =
        new DatabaseDiffMapBuilder(existedUser, updateUserDto).build();
    userUpdateRequest.setDatabaseDiffMap(databaseDiffMap);

    userUpdateRequest.setDifferencing(new Differencing(existedUser, updateUserDto));

    return userUpdateRequest;
  }

  private OptionalExecution<BooleanDiff, Void> updateUserEnabledStatus(
      UserUpdateRequest userUpdateRequest) {
    OptionalExecution<BooleanDiff, Void> updateUserEnabledExecution;

    Optional<BooleanDiff> optEnabledDiff = userUpdateRequest.getDifferencing().getEnabledDiff();
    User existedUser = userUpdateRequest.getExistedUser();

    if(optEnabledDiff.isPresent()) {
      updateUserEnabledExecution = executeUpdateEnableStatusOptionally(existedUser,
          optEnabledDiff.get());
    } else {
      updateUserEnabledExecution = NoUpdateExecution.INSTANCE;
    }

//    if (userUpdateRequest.isAttributeChanged(ENABLED_STATUS)) {
//      updateUserEnabledExecution = executeUpdateEnableStatusOptionally(userUpdateRequest);
//    } else {
//      updateUserEnabledExecution = NoUpdateExecution.INSTANCE;
//    }
    return updateUserEnabledExecution;
  }

  private boolean doesElasticSearchNeedUpdate(ExecutionStatus updateAttributesStatus,
      OptionalExecution<BooleanDiff, Void> updateUserEnabledExecution) {
    return updateAttributesStatus == SUCCESS
        || updateUserEnabledExecution.getExecutionStatus() == SUCCESS;
  }

  @Override
  public String createUser(User user) {
    CwsUserInfo cwsUser = getCwsUserData(user);
    enrichUserByCwsData(user, cwsUser);

    user.setEmail(toLowerCase(user.getEmail()));
    String racfId = toUpperCase(user.getRacfid());
    user.setRacfid(racfId);

    validationService.validateUserCreate(user, cwsUser != null);
    authorizeService.checkCanCreateUser(user);

    UserType userType = cognitoServiceFacade.createUser(user);
    String userId = userType.getUsername();
    user.setId(userId);
    auditLogService.createAuditLogRecord(new UserCreatedEvent(user));
    PutInSearchExecution doraExecution = createUserInSearch(userType);
    handleCreatePartialSuccess(userId, doraExecution);
    return userId;
  }

  @Override
  public UsersPage getUserPage(String paginationToken) {
    CognitoUserPage userPage =
        cognitoServiceFacade.searchPage(
            CognitoUsersSearchCriteriaUtil.composeToGetPage(paginationToken));
    List<User> users = enrichCognitoUsers(userPage.getUsers());
    return new UsersPage(users, userPage.getPaginationToken());
  }

  @Override
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

  @Override
  public List<UserAndOperation> getFailedOperations(LocalDateTime lastJobTime) {

    deleteProcessedLogs(lastJobTime);

    List<UserIdAndOperation> dbList = userLogService.getUserIdAndOperations(lastJobTime);

    return filterIdAndOperationList(dbList)
        .stream()
        .map(e -> new UserAndOperation(getUser(e.getId()), e.getOperation()))
        .collect(Collectors.toList());
  }

  @Override
  public RegistrationResubmitResponse resendInvitationMessage(String userId) {
    User user =  getUser(userId);
    authorizeService.checkCanResendInvitationMessage(user);
    cognitoServiceFacade.resendInvitationMessage(userId);
    auditLogService.createAuditLogRecord(new UserRegistrationResentEvent(user));
    return new RegistrationResubmitResponse(userId);
  }

  @Override
  @SuppressWarnings({"squid:S1166"})//Validation exceptions are already logged by ValidationService
  public UserVerificationResult verifyIfUserCanBeCreated(String racfId, String email) {
    User user = new User();
    user.setEmail(toLowerCase(email));
    user.setRacfid(toUpperCase(racfId));
    user.setRoles(Utils.toSet(CWS_WORKER));

    CwsUserInfo cwsUser = getCwsUserData(user);
    enrichUserByCwsData(user, cwsUser);

    try {
      validationService.validateVerifyIfUserCanBeCreated(user, cwsUser != null);
      authorizeService.checkCanCreateUser(user);
    } catch (UserValidationException | AdminAuthorizationException e) {
      return buildUserVerificationErrorResult(e.getErrorCode(), e.getUserMessage());
    }

    return UserVerificationResult.Builder.anUserVerificationResult().withUser(user)
        .withVerificationPassed().build();
  }

  private UserVerificationResult buildUserVerificationErrorResult(MessageCode code, String msg) {
    return UserVerificationResult.Builder.anUserVerificationResult()
        .withVerificationFailed(code.getValue(), msg).build();
  }

  private CwsUserInfo getCwsUserData(User user) {
    if (isRacfidUser(user)) {
      return cwsUserInfoService.getCwsUserByRacfId(user.getRacfid());
    } else {
      return null;
    }
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

  private ExecutionStatus updateUserAttributes(UserUpdateRequest userUpdateRequest) {
    ExecutionStatus updateAttributesStatus = WAS_NOT_EXECUTED;

    if(userService.updateUserAttributes(userUpdateRequest)) {
      updateAttributesStatus = SUCCESS;
      publishUpdateAttributesEvents(userUpdateRequest);
    }
    return updateAttributesStatus;
  }

  private void publishUpdateAttributesEvents(UserUpdateRequest userUpdateRequest) {
    User existedUser = userUpdateRequest.getExistedUser();
    Differencing differencing = userUpdateRequest.getDifferencing();

    publishUpdateRolesEvent(existedUser, differencing.getRolesDiff());
    publishUpdatePermissionsEvent(existedUser, differencing.getPermissionsDiff());
    publishUpdateEmailEvent(existedUser, differencing.getEmailDiff());
    publishUpdateNotesEvent(existedUser, differencing.getNotesDiff());
  }

  private void publishUpdateEmailEvent(User existedUser, Optional<StringDiff> optEmailDiff) {
    optEmailDiff.ifPresent(emailDiff ->
        auditLogService.createAuditLogRecord(new EmailChangedEvent(existedUser, emailDiff)));
  }

  private void publishUpdateNotesEvent(User existedUser, Optional<StringDiff> optNotesDiff) {
    optNotesDiff.ifPresent(notesDiff ->
        auditLogService.createAuditLogRecord(new NotesChangedEvent(existedUser, notesDiff)));
  }

  private void publishUpdatePermissionsEvent(User existedUser,
      Optional<StringSetDiff> optPermissionsDiff) {
    List<Permission> permissions = dictionaryProvider.getPermissions();

    optPermissionsDiff.ifPresent(permissionsDiff ->
        auditLogService.createAuditLogRecord(
            new PermissionsChangedEvent(existedUser, permissionsDiff, permissions)));
  }

  private void publishUpdateRolesEvent(User existedUser, Optional<StringSetDiff> optRolesDiff) {
    optRolesDiff.ifPresent(rolesDiff ->
        auditLogService.createAuditLogRecord(new UserRoleChangedEvent(existedUser, rolesDiff)));
  }

  private void handleUpdatePartialSuccess(
      String userId,
      ExecutionStatus updateAttributesStatus,
      OptionalExecution<BooleanDiff, Void> updateUserEnabledExecution,
      PutInSearchExecution<String> doraExecution) {

    ExecutionStatus updateEnableStatus = updateUserEnabledExecution.getExecutionStatus();
    Exception updateEnableException = updateUserEnabledExecution.getException();

    ExecutionStatus doraStatus = WAS_NOT_EXECUTED;
    Exception doraException = null;
    ExecutionStatus logDbStatus = WAS_NOT_EXECUTED;
    Exception logDbException = null;

    if (doraExecution != null) {
      doraStatus = doraExecution.getExecutionStatus();
      doraException = doraExecution.getException();

      OptionalExecution<String, UserLog> userLogExecution = doraExecution.getUserLogExecution();
      if (userLogExecution != null) {
        logDbStatus = userLogExecution.getExecutionStatus();
        logDbException = userLogExecution.getException();
      }
    }

    if (updateAttributesStatus == SUCCESS && updateEnableStatus == FAIL) { // partial Cognito update
      handleUpdatePartialSuccessWithCognitoFail(
          userId, updateEnableException, doraStatus, doraException, logDbStatus, logDbException);
    } else { // no Cognito partial update
      handleUpdatePartialSuccessNoCognitoFail(
          userId, doraStatus, doraException, logDbStatus, logDbException);
    }
  }

  private void handleUpdatePartialSuccessWithCognitoFail(
      String userId,
      Exception updateEnableException,
      ExecutionStatus doraStatus,
      Exception doraException,
      ExecutionStatus logDbStatus,
      Exception logDbException) {

    if (doraStatus == SUCCESS) {
      throwPartialSuccessException(userId, UPDATE, USER_PARTIAL_UPDATE, updateEnableException);

    } else if (doraStatus == FAIL && logDbStatus == SUCCESS) {
      throwPartialSuccessException(
          userId,
          UPDATE,
          USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_ERRORS,
          updateEnableException,
          doraException);

    } else if (doraStatus == FAIL && logDbStatus == FAIL) {
      throwPartialSuccessException(
          userId,
          UPDATE,
          USER_PARTIAL_UPDATE_AND_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS,
          updateEnableException,
          doraException,
          logDbException);
    }
  }

  private void handleUpdatePartialSuccessNoCognitoFail(
      String userId,
      ExecutionStatus doraStatus,
      Exception doraException,
      ExecutionStatus logDbStatus,
      Exception logDbException) {

    if (doraStatus == FAIL && logDbStatus == SUCCESS) {
      throwPartialSuccessException(userId, UPDATE, USER_UPDATE_SAVE_TO_SEARCH_ERROR, doraException);

    } else if (doraStatus == FAIL && logDbStatus == FAIL) {
      throwPartialSuccessException(
          userId, UPDATE, USER_UPDATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS, doraException,
          logDbException);
    }
  }

  private OptionalExecution<BooleanDiff, Void> executeUpdateEnableStatusOptionally(
      User existedUser, BooleanDiff enabledDiff) {
    return new OptionalExecution<BooleanDiff, Void>(enabledDiff) {
      @Override
      protected Void tryMethod(BooleanDiff enabledDiff) {
        cognitoServiceFacade.changeUserEnabledStatus(existedUser, enabledDiff.getNewValue());
        auditLogService
            .createAuditLogRecord(new UserEnabledStatusChangedEvent(existedUser, enabledDiff));
        return null;
      }

      @Override
      protected void catchMethod(Exception e) {
        LOGGER.error(
            messages.getTechMessage(ERROR_UPDATE_USER_ENABLED_STATUS, existedUser.getId()), e);
      }
    };
  }

  private void handleCreatePartialSuccess(String userId, PutInSearchExecution doraExecution) {
    if (doraExecution.getExecutionStatus() == FAIL) {
      OptionalExecution dbLogExecution = doraExecution.getUserLogExecution();

      if (dbLogExecution.getExecutionStatus() == SUCCESS) {
        throwPartialSuccessException(userId, CREATE, USER_CREATE_SAVE_TO_SEARCH_ERROR,
            doraExecution.getException());
      } else { // logging in db failed
        throwPartialSuccessException(userId, CREATE, USER_CREATE_SAVE_TO_SEARCH_AND_DB_LOG_ERRORS,
            doraExecution.getException(), dbLogExecution.getException());
      }
    }
  }

  private void deleteProcessedLogs(LocalDateTime lastJobTime) {
    int deletedCount = 0;
    try {
      deletedCount = userLogService.deleteProcessedLogs(lastJobTime);
    } catch (Exception e) {
      LOGGER.error(messages.getTechMessage(UNABLE_TO_PURGE_PROCESSED_USER_LOGS,
          lastJobTime.format(URL_DATETIME_FORMATTER)), e);
    }
    if (deletedCount > 0) {
      LOGGER.info("{} processed user log records are deleted", deletedCount);
    }
  }

  private void throwPartialSuccessException(
      String userId, OperationType operationType, MessageCode errorCode, Exception... causes) {
    throw exceptionFactory.createPartialSuccessException(userId, operationType, errorCode,
        causes);
  }

  private static List<UserIdAndOperation> filterIdAndOperationList(
      List<UserIdAndOperation> inputList) {
    Map<String, OperationType> idAndOperationMap = new HashMap<>();

    for (UserIdAndOperation userIdAndOperation : inputList) {
      String userId = userIdAndOperation.getId();
      OperationType operation = userIdAndOperation.getOperation();
      OperationType existedOperation = idAndOperationMap.get(userId);

      if (existedOperation == null || (existedOperation == CREATE && operation == UPDATE)) {
        idAndOperationMap.put(userId, operation);
      }
    }

    return idAndOperationMap
        .entrySet()
        .stream()
        .map(e -> new UserIdAndOperation(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  static Set<String> transformSearchValues(Set<String> values, StandardUserAttribute searchAttr) {
    if (searchAttr == RACFID_STANDARD) {
      values = applyFunctionToValues(values, Utils::toUpperCase);
    } else if (searchAttr == EMAIL) {
      values = applyFunctionToValues(values, Utils::toLowerCase);
    }
    return values;
  }

  private static Set<String> applyFunctionToValues(Set<String> values,
      Function<String, String> function) {
    return values.stream().map(function).collect(toSet());
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

  private PutInSearchExecution<String> updateUserInSearch(String id) {
    return new PutInSearchExecution<String>(id) {
      @Override
      protected ResponseEntity<String> tryMethod(String id) {
        User updatedUser = findUser(id);
        return searchService.updateUser(updatedUser);
      }

      @Override
      protected void catchMethod(Exception e) {
        String msg = messages.getTechMessage(UNABLE_UPDATE_IDM_USER_IN_ES, id);
        LOGGER.error(msg, e);
        setUserLogExecution(userLogService.logUpdate(id));
      }
    };
  }

  private PutInSearchExecution createUserInSearch(UserType userType) {
    return new PutInSearchExecution<UserType>(userType) {
      @Override
      protected ResponseEntity<String> tryMethod(UserType userType) {
        User user = mappingService.toUser(userType);
        return searchService.createUser(user);
      }

      @Override
      protected void catchMethod(Exception e) {
        String msg = messages.getTechMessage(UNABLE_CREATE_IDM_USER_IN_ES, userType.getUsername());
        LOGGER.error(msg, e);
        setUserLogExecution(userLogService.logCreate(userType.getUsername()));
      }
    };
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setCognitoServiceFacade(
      CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  public void setNsUserService(NsUserService nsUserService) {
    this.nsUserService = nsUserService;
  }

  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }

  public void setUserLogService(UserLogService userLogService) {
    this.userLogService = userLogService;
  }

  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }

  public void setAuthorizeService(AuthorizationService authorizeService) {
    this.authorizeService = authorizeService;
  }

  public void setMappingService(MappingService mappingService) {
    this.mappingService = mappingService;
  }

  public void setValidationService(ValidationService validationService) {
    this.validationService = validationService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  private static class NoUpdateExecution<T> extends OptionalExecution<T, Void> {

    private static final NoUpdateExecution INSTANCE = new NoUpdateExecution();

    private NoUpdateExecution() {
      super(null);
    }

    @Override
    protected Void tryMethod(T input) {
      return null;
    }

    @Override
    protected void catchMethod(Exception e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
      return ExecutionStatus.WAS_NOT_EXECUTED;
    }
  }
}
