package gov.ca.cwds.security.authorizer;

import gov.ca.cwds.testapp.domain.Case;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CWDS Intake Team
 */
public class FilterOnlyAuthorizer extends BaseAuthorizer<Case, Long> {

  private boolean isAuthorized(Long id) {
    return id % 2 == 0;
  }

  @Override
  protected Collection<Long> filterIds(Collection<Long> ids) {
    Stream<Long> filteredStream = ids.stream().filter(Objects::nonNull).filter(this::isAuthorized);
    return ids instanceof Set ? filteredStream.collect(Collectors.toSet())
        : filteredStream.collect(Collectors.toList());
  }

  @Override
  protected Collection<Case> filterInstances(Collection<Case> instances) {
    Stream<Case> filteredStream = instances.stream().filter(Objects::nonNull)
        .filter(aCase -> isAuthorized(aCase.getId()));
    return instances instanceof Set ? filteredStream.collect(Collectors.toSet())
        : filteredStream.collect(Collectors.toList());
  }
}
