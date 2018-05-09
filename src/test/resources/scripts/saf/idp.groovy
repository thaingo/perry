import groovy.json.JsonSlurper

def parse = { text ->
    if (text == null || "" == text) {
        return null
    }
    new JsonSlurper().parseText(text);
}
universalUserToken.userId = idpToken["safid.racfid"]?.toUpperCase()
def userProperties = parse(idpToken.basicprofile)?.User_Properties

if(!universalUserToken.userId?.trim()) {
    universalUserToken.userId = userProperties?.UserName
}
try {
    universalUserToken.roles = parse(userProperties?.Roles)?.Selections?.keySet() as HashSet
}catch (ignored) {}
if(!universalUserToken.roles) {
    universalUserToken.roles = new HashSet<>()
    println "INFO: There are no IDP roles provided"
}
