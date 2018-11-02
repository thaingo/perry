package gov.ca.cwds.idm.persistence.cwscms.repository;

import gov.ca.cwds.data.auth.ReadOnlyRepository;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.idm.dto.Office;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("idm")
@Repository
@SuppressWarnings({"squid:S1214"})//implementation details are queries and they are put here by Spring Data design
public interface OfficeRepository extends ReadOnlyRepository<CwsOffice, String> {

  String GET_ALL_OFFICES_QUERY =
      "select new gov.ca.cwds.idm.dto.Office(office.officeId, trim(office.cwsOfficeName),"
      + " office.governmentEntityType, trim(county.shortDescription)) from CwsOffice office,"
      + " County county where office.governmentEntityType = county.systemId";

  String ACTIVE_OFFICE_CONDITION = " and office.inactiveIndicator = 'N'";

  String GET_ALL_ACTIVE_OFFICES_QUERY = GET_ALL_OFFICES_QUERY + ACTIVE_OFFICE_CONDITION;

  String COUNTY_NAME = "countyName";

  String COUNTY_NAME_CONDITION = " and county.shortDescription = :" + COUNTY_NAME;

  @Query(GET_ALL_OFFICES_QUERY)
  List<Office> findOffices();

  @Query(GET_ALL_ACTIVE_OFFICES_QUERY)
  List<Office> findActiveOffices();

  @Query(GET_ALL_ACTIVE_OFFICES_QUERY + COUNTY_NAME_CONDITION)
  List<Office> findCountyActiveOffices(@Param(COUNTY_NAME) String countyName);
}
