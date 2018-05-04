package gov.ca.cwds.idm;

import gov.ca.cwds.idm.dto.User;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class IdmService {

  public List<User> getUsers() {
    List<User> resultList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      resultList.add(createUser(i));
    }
    return resultList;
  }

  public List getRoles() {
    return Collections.emptyList();
  }

  private User createUser(int i) {
    User user = new User();

    user.setCountyName("MyCounty");
    user.setUsername("userName" + i);
    user.setEnabled(i % 2 == 0);
    user.setEmail("email"+i+"@test.com");
    user.setOffice("Office address "+i);
    user.setPhoneNumber("+1916999999"+i%10);
    user.setEndDate(new Date());
    user.setStartDate(new Date());
    user.setFirstName("Firstname" + i);
    user.setLastName("Lastname" + i);
    user.setRacfid("RACFID" + i);
    user.setUserCreateDate(new Date());
    user.setUserLastModifiedDate(new Date());
    user.setStatus("userStatus" + i);
    user.setLastLoginDateTime(LocalDateTime.now().minusHours(i).plusMinutes(i).minusDays(i+5));
    return user;
  }

}
