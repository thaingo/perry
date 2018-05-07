package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CognitoIdmService implements IdmService {

  @Override
  public List<User> getUsers() {
    List<User> resultList = new ArrayList<>(20);
    for (int i = 0; i < 20; i++) {
      resultList.add(createUser(i));
    }
    return resultList;
  }

  @Override
  public User findUser(String id) {
    User mocked = createUser(0);
    mocked.setId(id);
    return mocked;
  }

  private User createUser(int i) {
    User user = new User();

    user.setCountyName("MyCounty");
    user.setId("userName" + i);
    user.setEnabled(i % 2 == 0);
    user.setEmail("email" + i + "@test.com");
    user.setOffice("Office " + i);
    user.setPhoneNumber("+1916999999" + i % 10);
    user.setEndDate(new Date());
    user.setStartDate(new Date());
    user.setFirstName("Firstname" + i);
    user.setLastName("Lastname" + i);
    user.setRacfid("RACFID" + i);
    user.setUserCreateDate(new Date());
    user.setUserLastModifiedDate(new Date());
    user.setStatus("userStatus" + i);
    user.setLastLoginDateTime(LocalDateTime.now().minusHours(i).plusMinutes(i).minusDays(i + 5L));
    return user;
  }

}
