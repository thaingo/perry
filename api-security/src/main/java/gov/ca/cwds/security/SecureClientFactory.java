package gov.ca.cwds.security;

import gov.ca.cwds.security.realm.PerrySubject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;

public class SecureClientFactory {

  public static Client createSecureClient() {
    Client client = ClientBuilder.newClient();
    client.register((ClientRequestFilter) requestContext -> requestContext.getHeaders()
        .add("Authorization", PerrySubject.getToken()));
    return client;
  }
}
