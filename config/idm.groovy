import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def attribute = {name -> cognitoUser.attributes?.find {it.name.equalsIgnoreCase(name)}?.value}

if(cwsUser) {

    def governmentEntityType = GovernmentEntityType.findBySysId(cwsUser.cwsOffice?.governmentEntityType)

    result.id = cognitoUser.username
    result.enabled = cognitoUser.enabled
    result.userCreateDate = cognitoUser.userCreateDate
    result.userLastModifiedDate = cognitoUser.userLastModifiedDate
    result.status = cognitoUser.userStatus
    result.racfid = attribute("CUSTOM:RACFID")
    result.email = attribute("email")
    result.permissions = attribute("custom:permission")?.split('\\s*:\\s*') as HashSet
    result.roles = attribute("custom:role")?.split('\\s*:\\s*') as HashSet

    result.startDate = cwsUser.staffPerson?.startDate
    result.endDate = cwsUser.staffPerson?.endDate
    result.countyName = governmentEntityType?.description
    result.firstName = cwsUser.staffPerson?.firstName
    result.lastName = cwsUser.staffPerson?.lastName
    result.officeId = cwsUser.cwsOffice?.officeId
    result.phoneExtensionNumber = cwsUser.cwsOffice?.primaryPhoneExtensionNumber
    result.phoneNumber = cwsUser.cwsOffice?.primaryPhoneNumber

}
else {
    result.id = cognitoUser.username
    result.enabled = cognitoUser.enabled
    result.userCreateDate = cognitoUser.userCreateDate
    result.userLastModifiedDate = cognitoUser.userLastModifiedDate
    result.status = cognitoUser.userStatus
    result.countyName = attribute("custom:County")
    result.firstName = attribute("given_name")
    result.lastName = attribute("family_name")
    result.racfid = attribute("CUSTOM:RACFID")
    result.email = attribute("email")
    result.permissions = attribute("custom:permission")?.split('\\s*:\\s*') as HashSet
    result.roles = attribute("custom:role")?.split('\\s*:\\s*') as HashSet
    result.officeId = attribute("custom:Office")

}


