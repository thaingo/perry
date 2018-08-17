package gov.ca.cwds.idm.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TryCatchExecutionTest {

  @Test
  public void testSuccess() {
    TryCatchExecution<String> execution =
        new TryCatchExecution<>(
            "abc",
            str -> {
              str.toUpperCase();
            },
            e -> {
              System.out.println(e.getMessage());
            });

    assertThat(execution.getResultType(), is(ResultType.SUCCESS));
    assertThat(execution.getException(), nullValue());
  }

  @Test
  public void testFail() {

    TryCatchExecution<Integer> execution =
        new TryCatchExecution<>(
            0,
            i -> {
              Integer r = 1/i;
            },
            e -> {
              System.out.println(e.getMessage());
            });

    assertThat(execution.getResultType(), is(ResultType.FAIL));
    assertThat(execution.getException(), notNullValue());
    assertTrue(execution.getException() instanceof ArithmeticException);
  }
}
