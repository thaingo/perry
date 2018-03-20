package gov.ca.cwds.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gov.ca.cwds.security.module.InjectorProvider;
import gov.ca.cwds.security.module.SecurityModule;
import gov.ca.cwds.testapp.module.TestModule;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.lang.reflect.Field;

/**
 * Created by dmitry.rudenko on 10/6/2017.
 */
public class AbstractApiSecurityTest {
  private static ThreadState subjectThreadState;

  @BeforeClass
  public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
    initShiro();
  }

  void initInjector() throws Exception {
    Injector injector = Guice.createInjector(new TestModule());
    injector.injectMembers(this);
    Field field = SecurityModule.class.getDeclaredField("injectorProvider");
    field.setAccessible(true);
    field.set(null, (InjectorProvider) () -> injector);
  }

  private static void initShiro() {
    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    setSecurityManager(factory.getInstance());
    Subject subjectUnderTest = new Subject.Builder(getSecurityManager())
            .authenticated(true)
            .principals(new SimplePrincipalCollection("user", "realm"))
            .buildSubject();
    //2. Bind the subject to the current thread:
    setSubject(subjectUnderTest);
  }

  /**
   * Allows subclasses to set the currently executing {@link Subject} instance.
   *
   * @param subject the Subject instance
   */
  protected static void setSubject(Subject subject) {
    clearSubject();
    subjectThreadState = createThreadState(subject);
    subjectThreadState.bind();
  }

  protected static ThreadState createThreadState(Subject subject) {
    return new SubjectThreadState(subject);
  }

  /**
   * Clears Shiro's thread state, ensuring the thread remains clean for future test execution.
   */
  protected static void clearSubject() {
    doClearSubject();
  }

  private static void doClearSubject() {
    if (subjectThreadState != null) {
      subjectThreadState.clear();
      subjectThreadState = null;
    }
  }

  protected static void setSecurityManager(SecurityManager securityManager) {
    SecurityUtils.setSecurityManager(securityManager);
  }

  protected static SecurityManager getSecurityManager() {
    return SecurityUtils.getSecurityManager();
  }

  @AfterClass
  public static void tearDownShiro() {
    doClearSubject();
    try {
      SecurityManager securityManager = getSecurityManager();
      LifecycleUtils.destroy(securityManager);
    } catch (UnavailableSecurityManagerException e) {
      //we don't care about this when cleaning up the test environment
      //(for example, maybe the subclass is a unit test and it didn't
      // need a SecurityManager instance because it was using only
      // mock Subject instances)
    }
    setSecurityManager(null);
  }
}
