package gov.ca.cwds.idm.service.rule;

import java.util.LinkedList;
import java.util.List;

public class ErrorRuleList {

  private List<ErrorRule> rules = new LinkedList<>();

  public ErrorRuleList addRule(ErrorRule rule) {
    rules.add(rule);
    return this;
  }

  public void check() {
    for (ErrorRule rule : rules) {
      rule.check();
    }
  }

  public boolean hasError() {
    for (ErrorRule rule : rules) {
      if (rule.hasError()) {
        return true;
      }
    }
    return false;
  }
}