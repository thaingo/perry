package gov.ca.cwds.idm.service.mapper;

import static gov.ca.cwds.data.persistence.auth.CmsUserAuthPrivilege.UserAuthPriv;

import gov.ca.cwds.data.persistence.auth.CmsUserAuthPrivilege;
import gov.ca.cwds.data.persistence.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.idm.dto.CwsStaffPrivilege;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps input StaffAuthorityPrivilege collection to a set of category/privilege pairs in the form of
 * CwsStaffPrivilege class.
 */
public class CwsStaffPrivilegeMapper {
  private final CmsUserAuthPrivilege cmsUserAuthPrivilege = CmsUserAuthPrivilege.getInstance();

  public Set<CwsStaffPrivilege> toCwsStaffPrivilege(Set<StaffAuthorityPrivilege> privileges) {
    return Optional.ofNullable(privileges).orElseGet(Collections::emptySet).stream()
        .filter(priv -> "P".equals(priv.getLevelOfAuthPrivilegeCode()))
        .filter(priv -> priv.getEndDate() == null)
        .filter(priv -> Objects.nonNull(priv.getLevelOfAuthPrivilegeType()))
        .filter(priv -> isActiveUserAuthPriv(priv.getLevelOfAuthPrivilegeType()))
        .map(this::mapToCwsStaffPrivilegeService)
        .collect(Collectors.toSet());
  }

  protected UserAuthPriv getUserAuthPriv(Short authPrivilegeType) {
    return cmsUserAuthPrivilege.findUserPrivBySysId(authPrivilegeType);
  }

  protected boolean isActiveUserAuthPriv(Short authPrivilegeType) {
    return getUserAuthPriv(authPrivilegeType).isActive();
  }

  protected String getPrivilegeCategory(Short authPrivilegeType) {
    return cmsUserAuthPrivilege.findSysIdDescription(
        getUserAuthPriv(authPrivilegeType).getCategoryId());
  }

  protected String getAuthPrivilegeTypeDesc(Short authPrivilegeType) {
    return cmsUserAuthPrivilege.findSysIdDescription(authPrivilegeType);
  }

  private CwsStaffPrivilege mapToCwsStaffPrivilegeService(StaffAuthorityPrivilege priv) {
    return new CwsStaffPrivilege(
        getPrivilegeCategory(priv.getLevelOfAuthPrivilegeType()),
        getAuthPrivilegeTypeDesc(priv.getLevelOfAuthPrivilegeType()));
  }
}
