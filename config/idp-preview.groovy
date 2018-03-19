import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def String countyCode = idpToken["county_code"]

universalUserToken.userId = idpToken["user"]
universalUserToken.roles = idpToken["roles"]
universalUserToken.setParameter("staffId", idpToken["staffId"])
universalUserToken.setParameter("county_name", idpToken["county_name"])
universalUserToken.setParameter("county_code", countyCode)
universalUserToken.setParameter("county_cws_code", GovernmentEntityType.findByCountyCd(countyCode).sysId)
universalUserToken.setParameter("privileges", idpToken["privileges"])