import gov.ca.cwds.config.api.idm.Roles
import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def authorization = user.authorization
def token

//RACFID USER
if (authorization) {
    def privileges = []
    authorization.authorityPrivilege.findAll {
        it.authPrivilegeCode == "P" && it.endDate == null
    } each {
        privileges.push it.authPrivilegeTypeDesc
    }

    def supervisorAuthorities = ["S", "A", "T", "B"]

    def supervisor = authorization.unitAuthority != null && authorization.unitAuthority.size() > 0 && authorization.unitAuthority.every { a ->
        supervisorAuthorities.any {
            it == a.unitAuthorityCode
        }
    }

    def authorityCodes = []
    authorization.unitAuthority.each {
        authorityCodes.push it.unitAuthorityCode
    }

    def governmentEntityType = GovernmentEntityType.findBySysId(authorization.cwsOffice?.governmentEntityType)

    token =
            [user           : authorization.userId,
             first_name     : authorization.staffPerson?.firstName,
             last_name      : authorization.staffPerson?.lastName,
             email          : user.parameters["email"],
             roles          : user.roles + [supervisor ? "Supervisor" : "SocialWorker"],
             staffId        : authorization.staffPerson?.id,
             county_name    : governmentEntityType.description,
             county_code    : governmentEntityType.countyCd,
             county_cws_code: governmentEntityType.sysId,
             privileges     : privileges + user.permissions,
             authorityCodes : authorityCodes]

}
//NON-RACFID USER
else {
    def countyName = user.parameters["custom:county"]
    def cwsCounty = countyName ? GovernmentEntityType.findByDescription(countyName) : null

    token = [user           : user.userId,
             roles          : user.roles,
             first_name     : user.parameters["given_name"],
             last_name      : user.parameters["family_name"],
             email          : user.parameters["email"],
             county_code    : cwsCounty?.countyCd,
             county_cws_code: cwsCounty?.sysId,
             county_name    : countyName,
             privileges     : user.permissions]

    if (Roles.isNonRacfIdCalsUser(user)) {
        token.privileges += ["CWS Case Management System", "Resource Management"]
    }

}

//COMMON

//for this moment we set only admin's own office to the office ids list
if (Roles.isOfficeAdmin(user)) {
    token.admin_office_ids = [user.parameters["custom:office"]]
}

return token
