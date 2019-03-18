package gov.ca.cwds.rest.api.domain.auth;

import java.util.HashMap;
import java.util.Map;


/**
 * Enumerated types for Auth Privilege from xtools
 *
 * @author CWDS API Team
 */
public enum AuthPrivilege {
  /**
   * Staff Person Level of Auth Type
   */
  STAFF_PERSON_LEVEL_OF_AUTH_TYPE("L", "Staff Person Level of Auth Type"),

  /**
   * Staff Person Privilege Type
   */
  STAFF_PERSON_PRIVILEGE_TYPE("P", "Staff Person Privilege Type");

  private final String code;
  private final String description;
  private static final Map<String, AuthPrivilege> mapByAuthPrivilegeCode = new HashMap<>();

  private AuthPrivilege(String code, String description) {
    this.code = code;
    this.description = description;
  }


  public String getCode() {
    return this.code;
  }

  /**
   * Description
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }

  public static AuthPrivilege findByAuthPrivilegeCode(String code) {
    return mapByAuthPrivilegeCode.get(code);
  }

  static {
    for (AuthPrivilege e : AuthPrivilege.values()) {
      mapByAuthPrivilegeCode.put(e.code, e);
    }
  }

  public static String getAuthPrivilegeDescription(String type) {
    String description = "";

    description = findByAuthPrivilegeCode(type).getDescription();

    return description;
  }

}
