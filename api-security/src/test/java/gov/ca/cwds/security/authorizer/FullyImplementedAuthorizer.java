package gov.ca.cwds.security.authorizer;

import gov.ca.cwds.testapp.domain.Case;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CWDS Intake Team
 */
public class FullyImplementedAuthorizer extends BaseAuthorizer<Case, Long> {

  public static final Map<String, Integer> callsCounts = new HashMap<>();

  private static void incrementCallCount(String methodName) {
    callsCounts.put(methodName, callsCounts.getOrDefault(methodName, 0) + 1);
  }

  private boolean isAuthorized(Long id) {
    return id == 2L || id == 3L;
  }

  @Override
  public boolean checkId(Long id) {
    incrementCallCount("checkId");
    return isAuthorized(id);
  }

  @Override
  public boolean checkInstance(Case instance) {
    incrementCallCount("checkInstance");
    return isAuthorized(instance.getId());
  }

  @Override
  public Long stringToId(String id) {
    return Long.valueOf(id);
  }

  @Override
  protected Collection<Long> filterIds(Collection<Long> ids) {
    incrementCallCount("filterIds");
    Stream<Long> filteredStream = ids.stream().filter(Objects::nonNull).filter(this::isAuthorized);
    return ids instanceof Set ? filteredStream.collect(Collectors.toSet())
        : filteredStream.collect(Collectors.toList());
  }

  @Override
  protected Collection<Case> filterInstances(Collection<Case> instances) {
    incrementCallCount("filterInstances");
    Stream<Case> filteredStream = instances.stream().filter(Objects::nonNull)
        .filter(aCase -> isAuthorized(aCase.getId()));
    return instances instanceof Set ? filteredStream.collect(Collectors.toSet())
        : filteredStream.collect(Collectors.toList());
  }
}
