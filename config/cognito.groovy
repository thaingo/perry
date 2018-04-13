counter = 0

print "!!!!!TOKEN: $idpToken"

while (counter < idpToken.Attributes.size()) {
    if(idpToken.Attributes[counter].Name?.toUpperCase().equals("CUSTOM:RACFID")) {
		universalUserToken.userId = idpToken.Attributes[counter].Value?.toUpperCase()
		break;    
    }
    counter++
}
