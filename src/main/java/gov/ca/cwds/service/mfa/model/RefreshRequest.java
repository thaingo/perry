
package gov.ca.cwds.service.mfa.model;


import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshRequest implements Serializable{

    private static final String REFRESH_AUTH_FLOW = "REFRESH_TOKEN_AUTH";

    @JsonProperty("AuthFlow")
    private String authFlow = REFRESH_AUTH_FLOW;
    @JsonProperty("AuthParameters")
    private AuthParameters authParameters;
    @JsonProperty("ClientId")
    private String clientId;

    public String getAuthFlow() {
        return authFlow;
    }

    public void setAuthFlow(String authFlow) {
        this.authFlow = authFlow;
    }

    public AuthParameters getAuthParameters() {
        return authParameters;
    }

    public void setAuthParameters(AuthParameters authParameters) {
        this.authParameters = authParameters;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
