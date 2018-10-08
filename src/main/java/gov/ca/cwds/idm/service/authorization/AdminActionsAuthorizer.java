package gov.ca.cwds.idm.service.authorization;

interface AdminActionsAuthorizer {

  boolean canViewUser();

  boolean canCreateUser();

  boolean canUpdateUser();

  boolean canResendInvitationMessage();

}
