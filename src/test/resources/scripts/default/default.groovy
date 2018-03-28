import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def authorization = user.authorization

if (authorization == null) {
    return [user : user.userId,
            roles: user.roles]
}

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

[user           : authorization.userId,
 first_name     : authorization.staffPerson?.firstName,
 last_name      : authorization.staffPerson?.lastName,
 roles          : user.roles + [supervisor ? "Supervisor" : "SocialWorker"],
 staffId        : authorization.staffPerson?.id,
 county_name    : governmentEntityType.description,
 county_code    : governmentEntityType.countyCd,
 county_cws_code: governmentEntityType.sysId,
 privileges     : privileges,
 authorityCodes : authorityCodes]

