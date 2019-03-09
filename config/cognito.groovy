import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.*
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.*
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.*

import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute

def cognitoAttribute = {UserAttribute attr -> idpToken.UserAttributes?.find {it.Name.equalsIgnoreCase(attr.name)}?.Value}

def racfid = nsUser.racfid?.toUpperCase()?.trim()

if(racfid) {
    universalUserToken.userId = racfid
}
else {
    universalUserToken.userId = cognitoAttribute(EMAIL)
}

universalUserToken.roles = nsUser.roles
universalUserToken.permissions = nsUser.permissions

idpToken.UserAttributes?.each {
    universalUserToken.parameters[it.Name.toLowerCase()] = it.Value
}

universalUserToken.parameters[USER_NAME] = idpToken.Username
universalUserToken.parameters[ROLES.name.toLowerCase()] = nsUser.roles
universalUserToken.parameters[PERMISSIONS.name.toLowerCase()] = nsUser.permissions
universalUserToken.parameters[PHONE_NUMBER.name.toLowerCase()] = nsUser.phoneNumber