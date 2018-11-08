package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.dto.UsersSearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IdmService {

  @PostAuthorize("@authorizationService.canViewUser(returnObject)")
  User findUser(String id);

  void updateUser(String id, UserUpdate updateUserDto);

  UserVerificationResult verifyIfUserCanBeCreated(String racfId, String email);

  UsersPage getUserPage(String paginationToken);

  String createUser(User user);

  List<User> searchUsers(UsersSearchCriteria usersSearchCriteria);

  List<UserAndOperation> getFailedOperations(LocalDateTime lastJobTime);

  @PreAuthorize("@authorizationService.canResendInvitationMessage(#id)")
  void resendInvitationMessage(@P("id") String id);
}
