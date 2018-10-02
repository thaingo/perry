package gov.ca.cwds.idm.service.authorization;

interface AdminActionsAuthorizer {

  boolean canFindUser();

  boolean canCreateUser();

  boolean canUpdateUser();

  boolean canResendInvitationMessage();

}
