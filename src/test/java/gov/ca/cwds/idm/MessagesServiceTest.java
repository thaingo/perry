package gov.ca.cwds.idm;

import static gov.ca.cwds.service.messages.MessageCode.IDM_MAPPING_SCRIPT_ERROR;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MessagesServiceTest extends BaseIdmIntegrationTest {

  @Test
  public void testGet() {
    assertThat(
        messagesService.getTechMessage(IDM_MAPPING_SCRIPT_ERROR),
        is("Error while running the IdmMappingScript"));
    assertThat(
        messagesService.getTechMessage(NO_USER_WITH_RACFID_IN_CWSCMS, "ABCDEF"),
        is("No user with RACFID: ABCDEF found in CWSCMS"));
  }
}
