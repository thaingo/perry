package gov.ca.cwds.idm.service.authorization;

public interface AdminActionsAuthorizer {

  void canViewUser();

  boolean canCreateUser();

  boolean canUpdateUser();

  boolean canResendInvitationMessage();

  boolean canEditRoles();

}
