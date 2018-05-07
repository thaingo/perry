package gov.ca.cwds.idm.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DictionaryProvider {
  public List<String> getPermissions() {
    return Collections.emptyList();
  }
}
