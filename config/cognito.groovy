def attribute = {name -> idpToken.UserAttributes?.find {it.Name.equalsIgnoreCase(name)}?.Value}

def listAttribute = {
 name ->
     def result = attribute(name)?.split('\\s*:\\s*') as HashSet
     return result ? result : new HashSet<>()
}

def racfid = attribute("custom:racfid")?.toUpperCase()?.trim()

if(racfid) {
    universalUserToken.userId = racfid
    universalUserToken.parameters["perry.racfid"] = racfid
}
else {
    universalUserToken.userId = attribute("email")
}

universalUserToken.roles = listAttribute("custom:role")
universalUserToken.permissions = listAttribute("custom:permission");
