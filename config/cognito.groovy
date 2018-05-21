def attribute = {name -> idpToken.UserAttributes?.find {it.Name.equalsIgnoreCase(name)}?.Value}

def multiValueAttribute = {
 name ->
     def result = attribute(name)?.split('\\s*:\\s*') as HashSet
     return result ? result : new HashSet<>()
}

def racfid = attribute("custom:racfid")?.toUpperCase()?.trim()

if(racfid) {
    universalUserToken.userId = racfid
    universalUserToken.parameters["racfid"] = racfid
}
else {
    universalUserToken.userId = attribute("email")
}

universalUserToken.roles = multiValueAttribute("custom:role")
universalUserToken.permissions = multiValueAttribute("custom:permission")

universalUserToken.parameters["first_name"] = attribute("given_name")
universalUserToken.parameters["last_name"] = attribute("family_name")
universalUserToken.parameters["email"] = attribute("email")
universalUserToken.parameters["county_name"] = attribute("custom:County")
