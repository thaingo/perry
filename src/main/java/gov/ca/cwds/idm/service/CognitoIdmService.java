package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CognitoIdmService implements IdmService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoIdmService.class);

  @Autowired CognitoServiceFacade cognitoService;

  @Override
  public List<User> getUsers() {
    List<User> resultList = new ArrayList<>(20);
    for (int i = 0; i < 20; i++) {
      resultList.add(createUser(i));
    }
    // ** TMP FOR TEST
    LOGGER.info(cognitoService.getById("24051d54-9321-4dd2-a92f-6425d6c455be"));

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
