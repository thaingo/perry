package gov.ca.cwds.rest.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.config.Constants;
import gov.ca.cwds.service.LoginService;
import gov.ca.cwds.service.WhiteList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by TPT2 on 11/15/2017.
 */
@Controller
public class LoginResource {

  private LoginService loginService;
  private PerryProperties properties;

  private WhiteList whiteList;

  @GET
  @RequestMapping(value = Constants.LOGIN_SERVICE_URL, method = RequestMethod.GET)
  @ApiOperation(
      value = "Login. Applications should direct users to this endpoint for login.  When authentication complete, user will be redirected back to callback with auth 'token' as a query parameter",
      code = 200)
  @SuppressFBWarnings("UNVALIDATED_REDIRECT")//white list usage right before redirect

  public void login(@NotNull @Context final HttpServletResponse response,
      @ApiParam(name = "callback", value = "URL to send the user back to after authentication")
      @RequestParam(name = Constants.CALLBACK_PARAM, required = false) String callback,
      @ApiParam(name = "sp_id", value = "Service provider id")
      @RequestParam(name = "sp_id", required = false) String spId) throws Exception {
    String accessCode = loginService.issueAccessCode(spId);
    if (StringUtils.isBlank(callback)) {
      callback = properties.getHomePageUrl();
    } else {
      whiteList.validate("callback", callback);
    }
    String redirectUrl = addAccessCode(callback, accessCode);
    response.sendRedirect(redirectUrl);
  }

  @Autowired
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }

  @Autowired
  public void setWhiteList(WhiteList whiteList) {
    this.whiteList = whiteList;
  }

  protected String addAccessCode(String callback, String accessCode) throws Exception {
    return new URIBuilder(callback)
        .setParameter("accessCode", accessCode)
        .build()
        .toString();
  }
}
