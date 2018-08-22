package gov.ca.cwds.idm.service.execution;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.service.ExecutionStatus;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class OptionalExecutionTest {

  @Test
  public void testSuccess() {
    OptionalExecution<String, String> execution =
        new OptionalExecution<String, String>("abc") {

          @Override
          protected String tryMethod(String str) {
            return str.toUpperCase();
          }

          @Override
          protected void catchMethod(Exception e) {
            System.out.println(e.getMessage());
          }
        };

    assertThat(execution.getExecutionStatus(), CoreMatchers.is(ExecutionStatus.SUCCESS));
    assertThat(execution.getException(), nullValue());
    assertThat(execution.getResult(), is("ABC"));
  }

  @Test
  public void testFail() {

    OptionalExecution<Integer, Integer> execution =
        new OptionalExecution<Integer, Integer>(0) {

          @Override
          protected Integer tryMethod(Integer i) {
            return 1 / i;
          }

          @Override
          protected void catchMethod(Exception e) {
            System.out.println(e.getMessage());
          }
        };

    assertThat(execution.getExecutionStatus(), is(ExecutionStatus.FAIL));
    assertThat(execution.getException(), notNullValue());
    assertTrue(execution.getException() instanceof ArithmeticException);
    assertThat(execution.getResult(), nullValue());
  }
}
