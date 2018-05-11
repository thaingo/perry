package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.AssignmentUnitDao;
import gov.ca.cwds.data.auth.CwsOfficeDao;
import gov.ca.cwds.data.auth.StaffAuthorityPrivilegeDao;
import gov.ca.cwds.data.auth.StaffPersonDao;
import gov.ca.cwds.data.auth.StaffUnitAuthorityDao;
import gov.ca.cwds.data.auth.UserIdDao;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.rest.api.domain.auth.StaffUnitAuthority;
import gov.ca.cwds.service.dto.CwsUserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional("transactionManager")
public class CwsUserInfoService {

  private static final int RACFID_MAX_LENGTH = 8;

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsUserInfoService.class);

  @Autowired private UserIdDao userIdDao;
  @Autowired private StaffAuthorityPrivilegeDao staffAuthorityPrivilegeDao;
  @Autowired private StaffUnitAuthorityDao staffUnitAuthorityDao;
  @Autowired private CwsOfficeDao cwsOfficeDao;
  @Autowired private AssignmentUnitDao assignmentUnitDao;
  @Autowired private StaffPersonDao staffPersonDao;

  public CwsUserInfo composeForUserAuthorization(Serializable primaryKey) {
    Optional<UserId> userId = findUserId(primaryKey, true);
    if (!userId.isPresent()) {
      LOGGER.warn("No RACFID found for {}", primaryKey);
      return null;
    }
    UserId user = userId.get();
    String userIdentifier = user.getId();
    String staffPersonIdentifier = user.getStaffPersonId();
    boolean socialWorker =
        !staffAuthorityPrivilegeDao.findSocialWorkerPrivileges(userIdentifier).isEmpty();
    Set<StaffAuthorityPrivilege> userAuthPrivs = getStaffAuthorityPriveleges(userIdentifier);
    Set<StaffUnitAuthority> staffUnitAuths = getStaffUnitAuthorities(staffPersonIdentifier);
    StaffPerson staffPerson = staffPersonDao.findOne(staffPersonIdentifier);
    if (staffPerson == null) {
      LOGGER.warn("No staff person found for {}", staffPersonIdentifier);
      return null;
    }
    CwsOffice cwsOffice = cwsOfficeDao.findOne(staffPerson.getCwsOffice());
    if (cwsOffice == null) {
      LOGGER.warn("No cws office found for {}", staffPerson.getCwsOffice());
      return null;
    }
    return composeUserInfo(
        user, cwsOffice, staffPerson, socialWorker, userAuthPrivs, staffUnitAuths);
  }

  public CwsUserInfo composeForIdm(Serializable primaryKey) {
    Optional<UserId> userId = findUserId(primaryKey, false);
    if (!userId.isPresent()) {
      LOGGER.warn("No RACFID found for {}", primaryKey);
      return null;
    }
    UserId user = userId.get();
    String staffPersonIdentifier = user.getStaffPersonId();

    StaffPerson staffPerson = staffPersonDao.findOne(staffPersonIdentifier);
    CwsOffice cwsOffice = null;
    if (staffPerson != null) {
      cwsOffice = cwsOfficeDao.findOne(staffPerson.getCwsOffice());
      if (cwsOffice == null) {
        LOGGER.warn("No cws office found for {}", staffPerson.getCwsOffice());
      }
    } else {
      LOGGER.warn("No staff person found for {}", staffPersonIdentifier);
    }
    return composeUserInfo(user, cwsOffice, staffPerson, false, null, null);
  }

  //codeclimate fix refactoring..
  private CwsUserInfo composeUserInfo(
      UserId user,
      CwsOffice cwsOffice,
      StaffPerson staffPerson,
      boolean socialWorker,
      Set<StaffAuthorityPrivilege> userAuthPrivs,
      Set<StaffUnitAuthority> staffUnitAuths) {
    return CwsUserInfo.UserInfoDTOBuilder.anUserInfoDTO()
        .withUser(user)
        .withCwsOffice(cwsOffice)
        .withStaffUnitAuths(staffUnitAuths)
        .withSocialWorker(socialWorker)
        .withUserAuthPrivs(userAuthPrivs)
        .withStaffPerson(staffPerson)
        .build();
  }

  private Optional<UserId> findUserId(Serializable primaryKey, boolean activeOnly) {
    LOGGER.info("Trying to find RACFID for user id {}", primaryKey);
    String logonId = ((String) primaryKey).trim();
    if (logonId.length() > RACFID_MAX_LENGTH) {
      return Optional.empty();
    }
    List<UserId> userList;
    if (activeOnly) {
      userList = userIdDao.findActiveByLogonId(logonId);
    } else {
      userList = userIdDao.findByLogonId(logonId);
    }
    if (CollectionUtils.isEmpty(userList)) {
      return Optional.empty();
    }
    return Optional.of(userList.get(0));
  }

  /**
   * Gets the {@link StaffUnitAuthority} for a StaffPerson
   *
   * @param staffPersonId The Staff Person Id
   * @return Set of StaffUnitAuthority for the Staff Person
   */
  private Set<StaffUnitAuthority> getStaffUnitAuthorities(String staffPersonId) {
    return this.staffUnitAuthorityDao
        .findByStaffPersonId(staffPersonId)
        .stream()
        .map(
            staffUnitAuth -> {
              String endDate = DomainChef.cookDate(staffUnitAuth.getEndDate());
              String assignedUnitKey = staffUnitAuth.getFkasgUnit().trim();
              String assignedUnitEndDate = "";
              if (StringUtils.isNotBlank(assignedUnitKey)) {
                final gov.ca.cwds.data.persistence.auth.AssignmentUnit assignmentUnit =
                    this.assignmentUnitDao.findOne(assignedUnitKey);
                assignedUnitEndDate = DomainChef.cookDate(assignmentUnit.getEndDate());
              }
              return new StaffUnitAuthority(
                  staffUnitAuth.getAuthorityCode(),
                  assignedUnitKey,
                  assignedUnitEndDate,
                  staffUnitAuth.getCountySpecificCode(),
                  endDate);
            })
        .collect(Collectors.toSet());
  }

  /**
   * Gets the {@link StaffAuthorityPrivilege} for a User
   *
   * @param userId the User Identifier
   * @return Set of StaffAuthorityPrivilege for the User
   */
  private Set<StaffAuthorityPrivilege> getStaffAuthorityPriveleges(String userId) {
    return this.staffAuthorityPrivilegeDao
        .findByUserId(userId)
        .stream()
        .map(
            priv ->
                new StaffAuthorityPrivilege(
                    priv.getLevelOfAuthPrivilegeType().toString(),
                    priv.getLevelOfAuthPrivilegeCode(),
                    priv.getCountySpecificCode(),
                    DomainChef.cookDate(priv.getEndDate())))
        .collect(Collectors.toSet());
  }

  public void setUserIdDao(UserIdDao userIdDao) {
    this.userIdDao = userIdDao;
  }

  public void setStaffAuthorityPrivilegeDao(StaffAuthorityPrivilegeDao staffAuthorityPrivilegeDao) {
    this.staffAuthorityPrivilegeDao = staffAuthorityPrivilegeDao;
  }

  public void setStaffUnitAuthorityDao(StaffUnitAuthorityDao staffUnitAuthorityDao) {
    this.staffUnitAuthorityDao = staffUnitAuthorityDao;
  }

  public void setCwsOfficeDao(CwsOfficeDao cwsOfficeDao) {
    this.cwsOfficeDao = cwsOfficeDao;
  }

  public void setAssignmentUnitDao(AssignmentUnitDao assignmentUnitDao) {
    this.assignmentUnitDao = assignmentUnitDao;
  }

  public void setStaffPersonDao(StaffPersonDao staffPersonDao) {
    this.staffPersonDao = staffPersonDao;
  }
}
