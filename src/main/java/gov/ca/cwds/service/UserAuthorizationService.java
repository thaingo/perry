package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.*;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.auth.CwsOffice;
import gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.rest.api.domain.auth.StaffUnitAuthority;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.rest.services.CrudsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Business layer object to work on {@link UserAuthorization}
 *
 * @author CWDS API Team
 */
@Transactional("transactionManager")
@Service
public class UserAuthorizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationService.class);

  @Autowired
  private UserIdDao userIdDao;
  @Autowired
  private StaffAuthorityPrivilegeDao staffAuthorityPrivilegeDao;
  @Autowired
  private StaffUnitAuthorityDao staffUnitAuthorityDao;
  @Autowired
  private CwsOfficeDao cwsOfficeDao;
  @Autowired
  private AssignmentUnitDao assignmentUnitDao;
  @Autowired
  private StaffPersonDao staffPersonDao;


  /**
   * {@inheritDoc}
   *
   * @see CrudsService#find(Serializable)
   */
  public UserAuthorization find(Serializable primaryKey) {
    assert primaryKey instanceof String;
    LOGGER.info(primaryKey.toString());

    final String userId = ((String) primaryKey).trim();
    List<UserId> userList = userIdDao.findActiveByLogonId(userId);


    if (userList != null && !userList.isEmpty()) {
      final UserId user = userList.get(0);
      String userIdentifier = user.getId();
      String staffPersonIdentifier = user.getStaffPersonId();
      boolean socialWorker = !staffAuthorityPrivilegeDao.findSocialWorkerPrivileges(userIdentifier).isEmpty();

      Set<StaffAuthorityPrivilege> userAuthPrivs = getStaffAuthorityPriveleges(userIdentifier);

      Set<StaffUnitAuthority> setStaffUnitAuths = getStaffUnitAuthorities(staffPersonIdentifier);

      Set<CwsOffice> setCwsOffices = getCwsOffices(staffPersonIdentifier);

      StaffPerson staffPerson = getStaffPerson(staffPersonIdentifier);

      return new UserAuthorization(user.getLogonId(), user.getStaffPersonId(),
              socialWorker, false, true, userAuthPrivs, setStaffUnitAuths, setCwsOffices, staffPerson);
    } else {
      LOGGER.warn("No user id found for {}", primaryKey);
    }

    return null;
  }

  /**
   * Gets the {@link CwsOffice} for a Staff Person
   *
   * @param staffPersonId The Staff Person Id
   * @return Set of CwsOffice for the Staff Person
   */
  private Set<CwsOffice> getCwsOffices(String staffPersonId) {
    return this.cwsOfficeDao.findByStaffPersonId(staffPersonId).
            stream().
            map(cwsOffice ->
                    new CwsOffice(cwsOffice.getOfficeId(),
                            cwsOffice.getGovernmentEntityType().toString(),
                            cwsOffice.getCountySpecificCode())).collect(Collectors.toSet());
  }

  private StaffPerson getStaffPerson(String staffPersonId) {
    return staffPersonDao.findOne(staffPersonId);
  }

  /**
   * Gets the {@link StaffUnitAuthority} for a StaffPerson
   *
   * @param staffPersonId The Staff Person Id
   * @return Set of StaffUnitAuthority for the Staff Person
   */
  private Set<StaffUnitAuthority> getStaffUnitAuthorities(String staffPersonId) {
    return this.staffUnitAuthorityDao.findByStaffPersonId(staffPersonId).
            stream().
            map(staffUnitAuth -> {
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
            }).collect(Collectors.toSet());
  }

  /**
   * Gets the {@link StaffAuthorityPrivilege} for a User
   *
   * @param userId the User Identifier
   * @return Set of StaffAuthorityPrivilege for the User
   */
  private Set<StaffAuthorityPrivilege> getStaffAuthorityPriveleges(String userId) {
    return this.staffAuthorityPrivilegeDao.findByUserId(userId).
            stream().
            map(priv ->
                    new StaffAuthorityPrivilege(
                            priv.getLevelOfAuthPrivilegeType().toString(),
                            priv.getLevelOfAuthPrivilegeCode(),
                            priv.getCountySpecificCode(),
                            DomainChef.cookDate(priv.getEndDate()))).
            collect(Collectors.toSet());
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
