package gov.ca.cwds.testapp.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import gov.ca.cwds.security.authorizer.CaseAuthorizer;
import gov.ca.cwds.security.authorizer.FilterOnlyAuthorizer;
import gov.ca.cwds.security.authorizer.TestStaticAuthorizer;
import gov.ca.cwds.security.module.SecurityModule;
import gov.ca.cwds.testapp.service.TestService;
import gov.ca.cwds.testapp.service.TestServiceImpl;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.authz.aop.PermissionAnnotationMethodInterceptor;

import java.lang.reflect.Method;


/**
 * Created by dmitry.rudenko on 10/6/2017.
 */
public class TestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestService.class).to(TestServiceImpl.class);
    install(new SecurityModule(null)
        .addAuthorizer("case:read", CaseAuthorizer.class)
        .addAuthorizer("case:filter", FilterOnlyAuthorizer.class)
        .addStaticAuthorizer(TestStaticAuthorizer.class)
    );

    bindInterceptor(Matchers.any(), Matchers.any(), new MethodInterceptor() {
      PermissionAnnotationMethodInterceptor permissionAnnotationMethodInterceptor = new PermissionAnnotationMethodInterceptor();
      @Override
      public Object invoke(MethodInvocation invocation) throws Throwable {
        return permissionAnnotationMethodInterceptor.invoke(new org.apache.shiro.aop.MethodInvocation() {
          @Override
          public Object proceed() throws Throwable {
            return invocation.proceed();
          }

          @Override
          public Method getMethod() {
            return invocation.getMethod();
          }

          @Override
          public Object[] getArguments() {
            return invocation.getArguments();
          }

          @Override
          public Object getThis() {
            return invocation.getThis();
          }
        });
      }
    });
  }
}
