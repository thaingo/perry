def attribute = {name -> cognitoUser.userAttributes?.find {it.name.equalsIgnoreCase(name)}?.value}


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

result.startDate = cwsUser?.staffPerson?.startDate
result.endDate = cwsUser?.staffPerson?.endDate
result.office = cwsUser?.cwsOffice?.cwsOfficeName
result.phoneExtensionNumber = cwsUser?.cwsOffice?.primaryPhoneExtensionNumber
result.phoneNumber = cwsUser?.cwsOffice?.primaryPhoneNumber

