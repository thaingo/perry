# CWDS CANS API TESTS

This image can be used to run a set of tests against CANS API or CANS Rails layer 

# Configuration
## Application Configuration Parameters
### Smoke and Functional tests
| Parameter | Description  | Required  | Possible values | Default value | Example |
| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| **TEST_TYPE** | The type of test to be started in the container | - | smoke; functional; performance | smoke | functional |
| **CANS_API_URL**  | URL of the cans-api instance to test | + |   |   | http://cans.dev.cwds.io:8089/ |

### Performance tests
| Parameter | Description  | Required  | Possible values | Default value | Example |
| :------------ | :------------ | :------------ | :------------ | :------------ | :------------ |
| **TEST_TYPE** | The type of test to be started in the container | + | smoke; functional; performance | smoke | performance |
| **JM_TARGET**  | The target application to be tested (Api java app or Rails app) | - | api; rails | api | rails |
| **JM_PERRY_MODE** | The mode the Perry runs on the environment the tests will be run against | - | DEV; PROD | PROD | PROD |
| **JM_USERS_CSV_PATH** | File path to the csv file containing users | - | any file path | /opt/cans-api-perf-test/assets/users.csv | /path/to/file.csv |
| **JM_USERS_COUNT** | How many simultaneous users will be imitated by tests | - | Any number like 10 or 2000. A bigger amount of users may lead to issues with running a test | 1 | 100 |
| **JM_UPDATE_REQUESTS_PER_USER** | How many request will make each user | - | Any number like 10 or 2000 | 1 | 100 |
| **JM_PERRY_PROTOCOL** | The protocol for the Perry connection | + | http; https |  | https |
| **JM_PERRY_HOST** | The host for the Perry connection | + | any host |  | web.dev.cwds.io |
| **JM_PERRY_PORT** | The port for the Perry connection | + | Any port like 80 (for http) or 443 (for https) |  | 443 |
| **JM_CANS_API_PROTOCOL** | The protocol for application to be tested | + | http; https |  | https |
| **JM_CANS_API_HOST** | The host for application to be tested | + | any host |  | cansapi.dev.cwds.io |
| **JM_CANS_API_PORT** | The port for application to be tested | + | Any port like 80 (for http) or 443 (for https) |  | 443 |


# Example commands to run:
### Run smoke test example
```
docker run -e "CANS_API_URL=http://cans.dev.cwds.io:8089/" -e "TEST_TYPE=smoke" -it cwds/cans-api-test
```

### Run functional test example
```
docker run -e "CANS_API_URL=http://cans.dev.cwds.io:8080/" \
	-e "TEST_TYPE=functional" \
	-e "DB_NS_JDBC_URL=jdbc:postgresql://postgres.dev.cwds.io:5432/postgres_data" \
	-e "DB_NS_SCHEMA=CANS" \
	-e "DB_NS_USER=postgres_data" \
	-e "DB_MS_PASSWORD=postgres_data" \
	-it cwds/cans-api-test
```

### Run performance test example
```
docker run -e "JM_TARGET=api" \
  -e "JM_PERRY_MODE=DEV" \
  -e "TEST_TYPE=performance" \
  -e "JM_USERS_COUNT=50" \
  -e "JM_UPDATE_REQUESTS_PER_USER=100" \
  -e "JM_PERRY_PROTOCOL=https" \
  -e "JM_PERRY_HOST=web.dev.cwds.io" \
  -e "JM_PERRY_PORT=443" \
  -e "JM_CANS_API_PROTOCOL=https" \
  -e "JM_CANS_API_HOST=cans.dev.cwds.io" \
  -e "JM_CANS_API_PORT=443" \
  -it --rm cwds/cans-api-test
```

# Result
The container returns 0 if tests finished successfully and 1 if tests failed.
