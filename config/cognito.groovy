universalUserToken.userId = idpToken.UserAttributes?.find {
    it.Name.equalsIgnoreCase("CUSTOM:RACFID")
}?.Value?.toUpperCase()

