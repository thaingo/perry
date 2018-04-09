import groovy.json.JsonSlurper

def parse = { text ->
    if (text == null || "" == text) {
        return null
    }
    new JsonSlurper().parseText(text);
}

counter = 0
while (counter <= idpToken.UserAttributes.size()) {
    if(idpToken.UserAttributes[counter].Name?.toUpperCase().equals("CUSTOM:RACFID")) {
		universalUserToken.userId = idpToken.UserAttributes[counter].Value?.toUpperCase()
		break;    
    }
    counter++
}
