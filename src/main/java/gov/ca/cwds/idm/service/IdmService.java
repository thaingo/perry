package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.CreateUserDto;
import gov.ca.cwds.idm.dto.UpdateUserDto;
import gov.ca.cwds.idm.dto.User;

import java.util.List;

public interface IdmService {
  List<User> getUsers(String lastName);

  User findUser(String id);

  void updateUser(String id, UpdateUserDto updateUserDto);

  String createUser(CreateUserDto createUserDto);
}
