def attribute = {name -> idpToken.UserAttributes?.find {it.Name.equalsIgnoreCase(name)}?.Value}

universalUserToken.userId = attribute("CUSTOM:RACFID")?.toUpperCase()

universalUserToken.roles = attribute("zoneinfo")?.split('\\s*:\\s*') as HashSet

if(!universalUserToken.roles) {
    universalUserToken.roles = new HashSet<>()
    println "INFO: There are no IDP roles provided"
}

