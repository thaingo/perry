import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType
import org.apache.commons.lang3.StringUtils
import gov.ca.cwds.util.Utils

def cognitoUserAttribute = {name -> cognitoUser.attributes?.find {it.name.equalsIgnoreCase(name)}?.value}

result.id = nsUser.username
result.racfid = nsUser.racfid
result.phoneNumber = nsUser.phoneNumber
result.phoneExtensionNumber = nsUser.phoneExtensionNumber
result.lastLoginDateTime = nsUser.lastLoginTime
result.notes = nsUser.notes

result.userLastModifiedDate = lastDate(Utils.toDate(nsUser.lastModifiedTime), cognitoUser.userLastModifiedDate)

result.enabled = cognitoUser.enabled
result.userCreateDate = cognitoUser.userCreateDate
result.status = cognitoUser.userStatus
result.email = cognitoUserAttribute("email")

if(StringUtils.isNotBlank(cognitoUserAttribute("custom:locked"))) {
    result.locked = cognitoUserAttribute("custom:locked").toBoolean()
}

if(StringUtils.isNotBlank(cognitoUserAttribute("custom:Permission"))) {
    result.permissions = cognitoUserAttribute("custom:Permission").split('\\s*:\\s*') as HashSet
}

if(StringUtils.isNotBlank(cognitoUserAttribute("custom:Role"))) {
    result.roles = cognitoUserAttribute("custom:Role").split('\\s*:\\s*') as HashSet
}

if(cwsUser) {

    def governmentEntityType = GovernmentEntityType.findBySysId(cwsUser.cwsOffice?.governmentEntityType)

    result.startDate = cwsUser.staffPerson?.startDate
    result.endDate = cwsUser.staffPerson?.endDate
    result.countyName = governmentEntityType?.description
    result.firstName = cwsUser.staffPerson?.firstName
    result.lastName = cwsUser.staffPerson?.lastName
    result.officeId = cwsUser.cwsOffice?.officeId
    result.officePhoneNumber = cwsUser.cwsOffice?.primaryPhoneNumber
    result.officePhoneExtensionNumber = cwsUser.cwsOffice?.primaryPhoneExtensionNumber
    result.cwsPrivileges = cwsUser.cwsStaffPrivs
} else {
    result.countyName = cognitoUserAttribute("custom:County")
    result.firstName = cognitoUserAttribute("given_name")
    result.lastName = cognitoUserAttribute("family_name")
    result.officeId = cognitoUserAttribute("custom:Office")
}

static Date lastDate(Date firstDate, Date secondDate) {
    long firstDateMillis = firstDate?.time?:0
    long secondDateMillis = secondDate?.time?:0
    return firstDateMillis > secondDateMillis ? firstDate : secondDate
}
