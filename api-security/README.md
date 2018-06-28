### API Authorization Library

#### Intro
API Authorization library is a Google Guice Module based on apache shiro security which provides ability 
to use complex authorization logic in a simple and straightforward way. It consists of set of shiro realms and 
security annotations. Also it provides flexible api for writing custom authorization logic.

#### Getting Started
In order to understand basics lets consider simple java example. 

```java
//Client entity which needs to be protected
class Client {
  private Long id;
  private String firstName;
  //...
}

//Service which needs to be secured
class ClientService {
  
  public void update(@Authorize("client:write:clientEntity") Client clientEntity) {
    //client update logic here
  }
}

```
Note that annotation should be applied to public access method

In the above example API authorization library will check can current user update client object. 
Annotation @Authorize tells authorization engine to run authorization rules on annotated method 
argument. Value of annotation is a string representation of permission which is required to perform "write" operation. 
Permission format: "\<Object Type\>:\<Operation\>\:\<Object Selector|Object ID Selector|Object ID>", so permission in the
above example can be explained as follows: Object type = "client", Operation = "write", Object Selector="clientEntity". 
Selector in this case is just object name. It can be any.
Next example will work exactly as previous one:

```java
//Service which needs to be secured
class ClientService {
  
  public void update(@Authorize("client:write:client") Client clientEntity) {
    //client update logic here
  }
}
```
Same annotation can be used for return value as well. In this case method should be annotated

```java
//Service which needs to be secured
class ClientService {
  
  @Authorize("client:read:result")
  //or @Authorize("client:write:client")
  public Client getClientByName(String name){
    //client update logic here
  }
}
```
If current user are not allowed to read returned client object - then authorization exception will be thrown.

Also Authorization library is able to filter collections in method arguments and resulting collections:

```java
//Service which needs to be secured
class ClientService {

  public void processCases(
      @Authorize("case:read:caseList") List<Case> caseList) {
    // caseList will contain only Cases that current user is authorized to read
  }
  
  @Authorize("client:read:result")
  //or @Authorize("client:write:client")
  public Collection<Client> getClientByName(String name){
    //client update logic here
  }
}
```

Collection in argument and resulting collection will contain only client objects which current user authorized to "read".
Next example will demonstrate how to use security annotation when secured entity inside of DTO object. In general we can
simply select inner objects using "." operator:


```java
//Client entity which needs to be protected
class Client {
  private Long id;
  private String firstName;
  //...
}

class ClientDTO {
  private Client client;
  private Collection<Client> clients;
  //...
}

//Service which needs to be secured
class ClientService {
  
  public void update(@Authorize({"client:write:clientDTO.client", "client:write:clientDTO.clients"}) ClientDTO clientDTO) {
    //client update logic here
  }
  
  public void processCaseDTOs(
      @Authorize("case:read:caseDTO.caseObject.id") List<CaseDTO> caseDTOList) {
    // caseDTOList will contain only DTO with Cases that current user is authorized to read
  }
}

```

In above example @Authorize annotation of the method update has 2 values, first will check if is user allowed to "write" "client" object which is 
inside clientDTO, and next value will check is user allowed to write ALL "client" objects in "clients" collection.

Also dot operation is supported for return values:
```java
//Service which needs to be secured
class ClientService {
  @Authorize({"client:read:clientDTO.client"}) 
  // or @Authorize({"client:read:clientDTO.client.id"}) 
  public Collection<ClientDTO> getAll() {
    //client update logic here
  }
}

```
And even more complicated example

```java
//Client entity which needs to be protected
class Client {
  private Long id;
  private String firstName;
  //...
}

class ClientDTO {
  private Collection<Client> clients;
  //...
}

class SomeDTO {
  private Collection<ClientDTO> clientDTOs;
  //...
}

//Service which needs to be secured
class ClientService {
  
  public void update(@Authorize({"client:write:someDTO.clientDTOs.clients"}) SomeDTO someDTO) {
    //client update logic here
  }
}

```
In above example write permission will be checked on ALL "clients" in ALL "clientDTOs" in "someDTO" object. If at least one
permission check will fail then authoriztion exception will be thrown.

#### Writing Authorizers

In previous examples we considered "client:read" and "client:write" permissions. In order to write authorization logic for
these permissions abstract class gov.ca.cwds.security.authorizer.BaseAuthorizer must be extended. Lets consider "client:read"
permission.

```java
class Client {
  private Long id;
  private String county;
  
  public String getCounty() {
    return county;
  }
  //...
}


class ClientReadAuthorizer extends BaseAuthorizer<Client, Long> {
  public boolean checkInstance(Client client) {
    return client.getCounty().equals("Sonoma");
  }
}
```
In order to make this authorizer visible for authorization library it should be registered in 
gov.ca.cwds.security.module.SecurityModule which is Google Guice Module. Then Security module 
should be installed during Dropwizard application initialization process:

```java
public class MyApplication extends BaseApiApplication<MyAppConfiguratoin> {
 //...
  @Override
  public Module applicationModule(Bootstrap<MyAppConfiguratoin> bootstrap) {
    
    return new ApplicationModule<MyAppConfiguratoin>(bootstrap) {

      @Override
      protected void configure() {
        //...
        install(new SecurityModule(BaseApiApplication::getInjector)
          .addAuthorizer("client:read", ClientReadAuthorizer.class)
        );
      }
    };
  }
  //...
}

```

In above example SecurityModule takes gov.ca.cwds.security.module.InjectorProvider as constructor argument. 
Google Guice Injector is required for usage in apache shiro library because shiro instances like realm, permission resolver and so on
do not belong to Application's Injector and being created by shiro library directly.
Authroizer is registered by calling addAuthorizer method. First parameter "client:read" is a permission name,
second one is Authorizer class which is responsible for permission handling.  

In some cases instance id is only available instead of instance like in following example:
```java

class ClientService {
  
  public void updateClientName(@Authorize("client:write:clientId") Long clientId, String name) {
    //client update logic here
  }
}


```  
In order to be able to check permission by object id it is required to override checkId method of BaseAuthorizer:
Below is simple example:
```java
class ClientWriteAuthorizer extends BaseAuthorizer<Client, Long> {
  @Inject
  private ClientDao clientDao;
  
  public boolean checkId(Long clientId) {
    Client client = clientDao.getById(clientId);
    return client.getCounty().equals("Sonoma");
  }
}
```
In above example we first load client instance from DB, then check client county. 
In case of DB access make sure that method call is in transactional context.

You can also check permissions using standard shiro api:

```java
class ClientService {
  
  public void updateClientName(Long clientId, String name) {
    SecurityUtils.getSubject().checkPermission("client:write:" + clientId);
    //client update logic here
  }
}
```

In this case you have to implement both stringToId and checkId methods:
```java
class ClientWriteAuthorizer extends BaseAuthorizer<Client, Long> {
  @Inject
  private ClientDao clientDao;
  
  public boolean checkId(Long clientId) {
    Client client = clientDao.getById(clientId);
    return client.getCounty().equals("Sonoma");
  }
  
  public Long stringToId(String id) {
    return Long.valueOf(id);
  }
}
```

There could be cases when filtering collections in arguments or return values is slow,
for example, when authorizer executes DB queries per each checkId or checkInstance method.

In such case it is better to implement filterIds or filterInstances method or both:
```java
class CaseAuthorizer extends BaseAuthorizer<Case, Long> {
  
  @Override
  protected Collection<Long> filterIds(Collection<Long> ids) {
    // pre-load data that will be needed for authorization of every id in the collection
    Collection<Long> filteredIds = ids instanceof Set ? new HashSet() : new ArrayList();
    for (Long id : ids) {
      // evaluate authorization for an id using pre-loaded data
      if (isAuthorized) {
        filteredIds.add(id);
      }
    }
    return filteredIds;
  }

  @Override
  protected Collection<Case> filterInstances(Collection<Case> instances) {
    // pre-load data that will be needed for authorization of every case in the collection
    Collection<Long> filteredCases = instances instanceof Set ? new HashSet() : new ArrayList();
    for (Case caseObject : instances) {
      // evaluate authorization for a caseObject using pre-loaded data
      if (isAuthorized) {
        filteredCases.add(caseObject);
      }
    }
    return filteredCases;
  }
}
```

You can use any combinations of BaseAuthorizer method depending on specific case. 

#### Static Authorization

In some cases authorization logic is not complicated and doesn't depend on specific entity. 
Lets consider following authorization rule: 
"If user has role ADMIN then user can call AdminService.createUser method". 
Here we don't have to analyze method parameters and run some extra authorization logic. 
We just have to check if current user can call specific method. In other words shiro security Subject should have 
ADMIN role in order to call AdminService.createUser method. Subject roles or permissions for such kind of authorization can be 
pre-populated during user authorization process. For this purpose we have to implement and register following interface:
```java
gov.ca.cwds.security.authorizer.StaticAuthorizer
```

Example:

```java

public class RolesStaticAuthorizer implements StaticAuthorizer {
  @Override
  public void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo authorizationInfo) {
     if(perryAccount.getPrivileges().contains("SUPER USER")) {
          simpleAuthorizationInfo.addRole("ADMIN");
     }
  }
}  

```

Then we can use standard shiro annotations on specific method:

```java
public class AdminService {
  @Override
  @RequiresRoles("ADMIN")
  public void createUser(User user) {
     //...
  }
}  

```

Static authorizer must be registered by calling ``` SecurityModule.addStaticAuthrizer``` method.

```java
 install(new SecurityModule(BaseApiApplication::getInjector)
          .addAuthorizer("client:read", ClientReadAuthorizer.class)
          .addStaticAuthorizer(RolesStaticAuthorizer.class)
        );
```

#### Sample shiro.ini configuration:

```properties
[main]

allowAllCredentialsMatcher = org.apache.shiro.authc.credential.AllowAllCredentialsMatcher
globalPermissionResolver = gov.ca.cwds.security.permission.AbacPermissionResolver
securityManager.authorizer.permissionResolver = $globalPermissionResolver
# -------------
# Perry Realm
# -------------
perryRealm = gov.ca.cwds.security.realm.JwtRealm
perryRealm.keyStorePath=config/enc.jceks
perryRealm.keyStoreAlias=test
perryRealm.keyStorePassword=test
perryRealm.keyStoreKeyPassword=test
perryRealm.tokenIssuer=perry
perryRealm.headlessToken=true

#encryption

perryRealm.useEncryption=true
perryRealm.encKeyAlias=enc128
perryRealm.encKeyPassword=test
perryRealm.encryptionMethod=A128GCM

perryRealm.credentialsMatcher = $allowAllCredentialsMatcher

# ------------------------------
# Perry Authenticating Filter
# ------------------------------
perry = gov.ca.cwds.security.web.PerryAuthenticatingFilter

noSession = org.apache.shiro.web.filter.session.NoSessionCreationFilter

cacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager
securityManager.cacheManager = $cacheManager
securityManager.realm = $perryRealm

[urls]
/application = noSession, anon
/static/** = noSession, anon
/swagger* = noSession, anon
#/** = noSession, anon
#enable security
/** = noSession, perry
```

#### Disable Authorization
To disable Authorization you need to add a Guice provider for SecurityConfiguration with "authorizationEnabled" field set to false. This configuration can be set up via yaml configuration file. By default, the authorization is turned on. If no security configuration provided in yaml file the authorization is on.

```java
// ApplicationModule.java
@Provides
public SecurityConfiguration provideSecurityConfiguration(T applicationConfiguration) {
  return applicationConfiguration.getSecurityConfiguration();
}
```

```properties
#application-configuration.yml
security:
  authorization: false
```
