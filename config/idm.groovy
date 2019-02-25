import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.*
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.*

import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType
import gov.ca.cwds.util.Utils
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute
import org.apache.commons.lang3.StringUtils

def cognitoUserAttribute = {UserAttribute attr -> cognitoUser.attributes?.find {it.name.equalsIgnoreCase(attr.name)}?.value}

result.id = nsUser.username
result.racfid = nsUser.racfid
result.phoneNumber = nsUser.phoneNumber
result.phoneExtensionNumber = nsUser.phoneExtensionNumber
result.lastLoginDateTime = nsUser.lastLoginTime
result.notes = nsUser.notes
result.roles = nsUser.roles
result.permissions = nsUser.permissions

result.userLastModifiedDate = lastDate(Utils.toDate(nsUser.lastModifiedTime), cognitoUser.userLastModifiedDate)

result.enabled = cognitoUser.enabled
result.userCreateDate = cognitoUser.userCreateDate
result.status = cognitoUser.userStatus
result.email = cognitoUserAttribute(EMAIL)

if(StringUtils.isNotBlank(cognitoUserAttribute(IS_LOCKED))) {
    result.locked = cognitoUserAttribute(IS_LOCKED).toBoolean()
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
    result.firstName = nsUser.firstName
    result.lastName = nsUser.lastName
    result.countyName = cognitoUserAttribute(COUNTY)
    result.officeId = cognitoUserAttribute(OFFICE)
}

static Date lastDate(Date firstDate, Date secondDate) {
    long firstDateMillis = firstDate?.time?:0
    long secondDateMillis = secondDate?.time?:0
    return firstDateMillis > secondDateMillis ? firstDate : secondDate
}
