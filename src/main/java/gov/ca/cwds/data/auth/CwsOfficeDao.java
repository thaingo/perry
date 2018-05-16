package gov.ca.cwds.data.auth;


import gov.ca.cwds.data.persistence.auth.CwsOffice;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * DAO for {@link CwsOffice}.
 *
 * @author CWDS API Team
 */

@Repository
public interface CwsOfficeDao extends ReadOnlyRepository<CwsOffice, String> {

  Iterable<CwsOffice> findByOfficeIdIn(Collection<String> ids);
}
