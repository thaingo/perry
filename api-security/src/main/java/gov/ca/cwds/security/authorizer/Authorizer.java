package gov.ca.cwds.security.authorizer;

import java.util.Collection;

/**
 * Created by TPT2 on 10/11/2017.
 */
public interface Authorizer {

  boolean check(Object o);

  Collection<Object> filter(Collection<Object> c);
}
