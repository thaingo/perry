# Perry

Perry is responsible for user authentication, authorization, identity mapping, single sign on and security attributes transmission

## Wiki

The development team is actively using the [Github Wiki](https://github.com/ca-cwds/perry/wiki).

## Documentation

Main concepts and configuration are described in [Developer's guide.](https://osicagov.sharepoint.com/:w:/r/sites/TechnologyPlatformTeam2/_layouts/15/WopiFrame2.aspx?sourcedoc=%7BEB8E8C6A-481B-49E3-A855-09DA49CF976B%7D)

The development team uses [Swagger](http://swagger.io/) for documenting the API.

## Testing

### Unit Tests
To run all unit tests, run `./gradlew test`. If the build is successful, all tests passed. If a test fails, you will see more output. If no files have changed, the test run may be very fast.

### Functional Tests
After merge to master dev pipeline runs functional tests for 3 perry modes: Dev, Cognito and Cognito with MFA. Check Jenkinsfile-complete.groovy in root of the project for details. 

## Development
### Libraries and Frameworks
Perry project is based on spring. It currently supports 3 main running modes which are separated by spring profiles(for more information about spring profiles see: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html): 

1. Dev mode: for local testing and development, it doesn't require configuration and db connection. It uses in-memory H2 db, also form based authentication where you can use Service provider JSON token as user name.
Active spring profile: `dev` 
2. OAuth2.0 Mode. This profile is reserved for integration with OAuth2.0 authorization server and isn't used currently. For more info check https://projects.spring.io/spring-security-oauth/docs/oauth2.html.
Active spring profiles: `prod`+`oauth2`.
3. Cognito MFA mode: Integration with Cognito Identity Provider. Please check [Developer's guide.](https://osicagov.sharepoint.com/:w:/r/sites/TechnologyPlatformTeam2/_layouts/15/WopiFrame2.aspx?sourcedoc=%7BEB8E8C6A-481B-49E3-A855-09DA49CF976B%7D) for more information.
Active spring profiles: `prod`+`cognito`+`mfa`+`cognito_refresh`. 

### Running the Application in DEV mode
Perry dev mode doesn't require databases running and specific configuration. 
`java -jar ./build/libs/perry-<version>.jar -Dspring.profiles.active=dev -Dspring.config.location=config/perry-dev.yml` 
It will listen on 8080 port by default. To change it set HTTP_PORT env variable or change server.http.port option in config/perry-dev.yml. 

### Running the Application in Cognito MFA mode
_Make sure you have the Postgres, DB2 containers are running. `docker-compose up postgresql_data cals_db2_data`. Also make sure that all OAuth2.0 as well as db connection properties are specified properly. For more information check Perry Dev Guide._

`java -jar ./build/libs/perry-<version>.jar -Dspring.profiles.active=cognito,prod,mfa,cognito_refresh -Dspring.config.location=config/perry-prod.yml`

## License Information
The legal folder contains csv file listing the licenses for application dependencies.

### Generate license report
To generate license report or update existing report run:

```./gradlew libLicenseReport```

# Questions

If you have any questions regarding the contents of this repository, please email the Office of Systems Integration at FOSS@osi.ca.gov.
