package gov.ca.cwds.data.auth;


import gov.ca.cwds.data.persistence.auth.StaffUnitAuthority;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for {@link StaffUnitAuthority}.
 *
 * @author CWDS API Team
 */

@Repository
public interface StaffUnitAuthorityDao extends ReadOnlyRepository<StaffUnitAuthority, String> {

  @Query("SELECT S FROM StaffUnitAuthority S WHERE S.staffPersonId = :staffPersonId"
      + " AND S.endDate is null")
  List<StaffUnitAuthority> findByStaffPersonId(@Param("staffPersonId") String staffPersonId);

}
