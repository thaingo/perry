import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType
import org.apache.commons.lang3.StringUtils

def attribute = {name -> cognitoUser.attributes?.find {it.name.equalsIgnoreCase(name)}?.value}

result.id = cognitoUser.username
result.enabled = cognitoUser.enabled
result.userCreateDate = cognitoUser.userCreateDate
result.userLastModifiedDate = cognitoUser.userLastModifiedDate
result.status = cognitoUser.userStatus
result.email = attribute("email")
result.racfid = attribute("CUSTOM:RACFID")

if(StringUtils.isNotBlank(attribute("custom:permission"))) {
    result.permissions = attribute("custom:permission").split('\\s*:\\s*') as HashSet
}

if(StringUtils.isNotBlank(attribute("custom:role"))) {
    result.roles = attribute("custom:role").split('\\s*:\\s*') as HashSet
}

if(cwsUser) {

    def governmentEntityType = GovernmentEntityType.findBySysId(cwsUser.cwsOffice?.governmentEntityType)

    result.startDate = cwsUser.staffPerson?.startDate
    result.endDate = cwsUser.staffPerson?.endDate
    result.countyName = governmentEntityType?.description
    result.firstName = cwsUser.staffPerson?.firstName
    result.lastName = cwsUser.staffPerson?.lastName
    result.officeId = cwsUser.cwsOffice?.officeId
    result.phoneExtensionNumber = cwsUser.cwsOffice?.primaryPhoneExtensionNumber
    result.phoneNumber = cwsUser.cwsOffice?.primaryPhoneNumber

} else {
    result.countyName = attribute("custom:County")
    result.firstName = attribute("given_name")
    result.lastName = attribute("family_name")
    result.officeId = attribute("custom:Office")
}


