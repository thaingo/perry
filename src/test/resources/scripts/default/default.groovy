import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType

def authorization = user.authorization
def token

def cwsCaseManagementSystem = "CWS Case Management System"
//RACFID USER
if (authorization) {
    def privileges = []
    authorization.authorityPrivilege.findAll {
        it.authPrivilegeCode == "P" && it.endDate == null
    } each {
        privileges.push it.authPrivilegeTypeDesc.trim()
    }

    def authorityCodes = []
    authorization.unitAuthority.findAll {
        it.endDate == null
    } each {
        authorityCodes.push it.unitAuthorityCode
    }

    def governmentEntityType = GovernmentEntityType.findBySysId(authorization.cwsOffice?.governmentEntityType)

    // Populate case carrying Social Worker permission
    def caseCarryingSocialWorkerPermissions = [
            "CANS-staff-person-clients-read",
            "CANS-client-read",
            "CANS-client-search",
            "CANS-assessment-read",
            "CANS-assessment-create",
            "CANS-assessment-in-progress-update",
            "CANS-assessment-completed-update",
            "CANS-assessment-completed-delete",
            "CANS-assessment-in-progress-delete",
            "CANS-assessment-complete"
    ]

    def isCaseCarryingWorker = privileges.contains(cwsCaseManagementSystem) && authorization.hasAssignment

    if (isCaseCarryingWorker) {
        privileges += caseCarryingSocialWorkerPermissions
    }

    def cansSupervisorPermissions = [
            "CANS-staff-person-subordinates-read",
            "CANS-staff-person-read",
            "CANS-staff-person-clients-read",
            "CANS-client-read",
            "CANS-client-search",
            "CANS-assessment-read",
            "CANS-assessment-create",
            "CANS-assessment-in-progress-update",
            "CANS-assessment-completed-update",
            "CANS-assessment-completed-delete",
            "CANS-assessment-in-progress-delete",
            "CANS-assessment-complete"]

    def isSupervisor = authorityCodes.size() > 0 && authorityCodes.contains("S")

    if (isSupervisor) {
        privileges += cansSupervisorPermissions
    }

    // Populate non case carrying Social Worker permission
    def nonCaseCarryingSocialWorkerPermissions = [
            "CANS-client-read",
            "CANS-client-search",
            "CANS-assessment-read",
            "CANS-assessment-create",
            "CANS-assessment-in-progress-update",
            "CANS-assessment-in-progress-delete",
            "CANS-assessment-complete"
    ]

    def overridePrivileges = ["Countywide Read", "Countywide Read/Write", "Statewide Read",
                              "Officewide Read", "Officewide Read/Write"]

    def overrideAuthorities = ["U", "R", "A", "T", "B"]

    def isNonCaseCarryingWorker = !authorization.hasAssignment &&
            (
                    !Collections.disjoint(authorityCodes, overrideAuthorities) ||
                            !Collections.disjoint(privileges, overridePrivileges)
            )

    if (isNonCaseCarryingWorker) {
        privileges += nonCaseCarryingSocialWorkerPermissions
    }

    def minimalDefaultPermissions = [
            "CANS-client-read",
            "CANS-client-search",
            "CANS-assessment-read"
    ]

    if (!isSupervisor && !isNonCaseCarryingWorker && !isCaseCarryingWorker) {
        privileges += minimalDefaultPermissions
    }

    token =
            [user           : authorization.userId,
             first_name     : authorization.staffPerson?.firstName,
             last_name      : authorization.staffPerson?.lastName,
             email          : user.parameters["email"],
             roles          : user.roles + [isSupervisor ? "Supervisor" : "SocialWorker"],
             staffId        : authorization.staffPerson?.id,
             county_name    : governmentEntityType.description,
             county_code    : governmentEntityType.countyCd,
             county_cws_code: governmentEntityType.sysId,
             privileges     : (privileges + user.permissions).unique(),
             authorityCodes : authorityCodes]

    //for this moment we set only admin's own office to the office ids list
    if (user.roles.contains("Office-admin")) {
        token.admin_office_ids = [authorization.cwsOffice?.officeId]
    }

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

    if (user.roles.contains("CALS-external-worker")) {
        token.privileges += [cwsCaseManagementSystem, "Resource Management"]
    }

}

//COMMON
token.userName = user.parameters["userName"]

return token
