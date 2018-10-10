package gov.ca.cwds.idm.service.authorization;

public interface AdminActionsAuthorizer {

  boolean canViewUser();

  boolean canCreateUser();

  boolean canUpdateUser();

  boolean canResendInvitationMessage();

}
