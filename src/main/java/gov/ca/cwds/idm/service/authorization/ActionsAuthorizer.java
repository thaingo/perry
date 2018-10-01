package gov.ca.cwds.idm.service.authorization;

interface ActionsAuthorizer {

  boolean canFindUser();

  boolean canCreateUser();

  boolean canUpdateUser();

  boolean canResendInvitationMessage();

}
