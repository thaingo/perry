import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def authorization = user.authorization

//NON-RACFID CALS USER
if (user.roles?.contains("External CALS")) {
    return [user           : user.userId,
            roles          : user.roles,
            county_code    : "99",
            county_cws_code: 1126,
            county_name    : "State of California",
            privileges     : ["CWS Case Management System", "Resource Management"]]
}

//NON-RACFID USER
if (authorization == null) {
    return [user : user.userId,
            roles: user.roles]
}

//RACFID USER
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

def governmentEntityType = GovernmentEntityType.findBySysId(authorization.cwsOffice?.governmentEntityType)

[user       : authorization.userId,
 roles      : user.roles + [supervisor ? "Supervisor" : "SocialWorker"],
 staffId    : authorization.staffPerson?.id,
 county_name: governmentEntityType.description,
 county_code: governmentEntityType.countyCd,
 county_cws_code: governmentEntityType.sysId,
 privileges : privileges]

