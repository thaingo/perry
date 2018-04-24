package gov.ca.cwds.web;

import java.util.Optional;
import java.util.function.Function;

public interface LogoutUrlProvider extends Function<String, Optional<String>>{
}
