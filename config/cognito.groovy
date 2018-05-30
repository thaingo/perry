def isMultiValueAttribute = {String name-> ["custom:permission", "custom:role"].find {it.equalsIgnoreCase(name)}}

def getValues = {String value ->
    def result = value?.split('\\s*:\\s*') as HashSet
    return result ? result : new HashSet<>()
}

def getValue = {String name, String value ->
    if(isMultiValueAttribute(name)) {
        return getValues(value)
    }
    else {
        return value
    }
}

def attribute = {name ->
    getValue(name, idpToken.UserAttributes?.find {it.Name.equalsIgnoreCase(name)}?.Value)
}

def racfid = attribute("custom:racfid")?.toUpperCase()?.trim()

if(racfid) {
    universalUserToken.userId = racfid
}
else {
    universalUserToken.userId = attribute("email")
}

universalUserToken.roles = attribute("custom:role")
universalUserToken.permissions = attribute("custom:permission")

idpToken.UserAttributes?.each {
    universalUserToken.parameters[it.Name.toLowerCase()] = getValue(it.Name, it.Value)
}

universalUserToken.parameters["userName"] = idpToken.Username