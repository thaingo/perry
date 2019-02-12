package gov.ca.cwds.idm.service.mapper;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;

public class NsUserMapper {

  public NsUser toNsUser(User user) {
    if(user == null) {
      return null;
    }

    NsUser nsUser = new NsUser();

    nsUser.setUsername(user.getId());
    nsUser.setRacfid(user.getRacfid());
    nsUser.setNotes(user.getNotes());
    nsUser.setPhoneNumber(user.getPhoneNumber());
    nsUser.setPhoneExtensionNumber(user.getPhoneExtensionNumber());
    nsUser.setFirstName(user.getFirstName());

    return nsUser;
  }
}
