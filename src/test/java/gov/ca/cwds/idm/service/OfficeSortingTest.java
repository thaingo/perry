package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.OfficeService.OFFICE_NAME_COMPARATOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.idm.dto.Office;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class OfficeSortingTest {

  @Test
  public void testSorting() {
    List<Office> offices = new ArrayList<>();

    offices.add(office(null));
    offices.add(office("Banana"));
    offices.add(office("apple"));
    offices.add(office("ANANAS"));
    offices.add(office("2 apple"));
    offices.add(office("11 apple"));
    offices.add(office(""));
    offices.add(office("@"));

    offices.sort(OFFICE_NAME_COMPARATOR);

    assertThat(offices.get(0).getOfficeName(), is(""));
    assertThat(offices.get(1).getOfficeName(), is("11 apple"));
    assertThat(offices.get(2).getOfficeName(), is("2 apple"));
    assertThat(offices.get(3).getOfficeName(), is("@"));
    assertThat(offices.get(4).getOfficeName(), is("ANANAS"));
    assertThat(offices.get(5).getOfficeName(), is("apple"));
    assertThat(offices.get(6).getOfficeName(), is("Banana"));
    assertThat(offices.get(7).getOfficeName(), nullValue());
  }

  private static Office office(String officeName) {
    return new Office("officeId", officeName, (short)1, "Madera");
  }
}
