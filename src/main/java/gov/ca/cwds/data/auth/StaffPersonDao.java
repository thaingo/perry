package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.StaffPerson;

import java.util.Collection;

/** Created by dmitry.rudenko on 8/21/2017. */
public interface StaffPersonDao extends ReadOnlyRepository<StaffPerson, String> {

  Iterable<StaffPerson> findByIdIn(Collection<String> ids);
}
