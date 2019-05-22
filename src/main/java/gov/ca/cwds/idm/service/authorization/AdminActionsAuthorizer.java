package gov.ca.cwds.idm.service.authorization;

public interface AdminActionsAuthorizer {

  void checkCanViewUser();

  void checkCanCreateUser();

  void checkCanUpdateUser();

  boolean canUpdateUser();

  void checkCanResendInvitationMessage();
}
