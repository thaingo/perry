package gov.ca.cwds.idm.service.trycatch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.service.ResultType;
import gov.ca.cwds.idm.service.trycatch.TryCatchExecution;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class TryCatchExecutionTest {

  @Test
  public void testSuccess() {
    TryCatchExecution<String> execution =
        new TryCatchExecution<String>("abc") {

          @Override
          protected void tryMethod(String str) {
            str.toUpperCase();
          }

          @Override
          protected void catchMethod(Exception e) {
            System.out.println(e.getMessage());
          }
        };

    assertThat(execution.getResultType(), CoreMatchers.is(ResultType.SUCCESS));
    assertThat(execution.getException(), nullValue());
  }

  @Test
  public void testFail() {

    TryCatchExecution<Integer> execution =
        new TryCatchExecution<Integer>(0) {

          @Override
          protected void tryMethod(Integer i) {
            Integer r = 1 / i;
          }

          @Override
          protected void catchMethod(Exception e) {
            System.out.println(e.getMessage());
          }
        };

    assertThat(execution.getResultType(), is(ResultType.FAIL));
    assertThat(execution.getException(), notNullValue());
    assertTrue(execution.getException() instanceof ArithmeticException);
  }
}
