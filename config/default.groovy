def authorization = user.authorization
if (authorization == null) {
    return [user : user.userId,
            roles: user.roles]
}
privileges = []
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


[user       : authorization.userId,
 roles      : user.roles + [supervisor ? "Supervisor" : "SocialWorker"],
 staffId    : authorization.staffPersonId,
 county_name: authorization.countyName,
 county_code: authorization.countyCode,
 county_cws_code: authorization.countyCwsCode,
 privileges : privileges]

