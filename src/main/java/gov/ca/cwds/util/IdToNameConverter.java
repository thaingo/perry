package gov.ca.cwds.util;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alexander Serbin on 1/23/2019
 */
public class IdToNameConverter {

  private Map<String, String> idNameHash;

  public IdToNameConverter(Map<String, String> idNameHash) {
    this.idNameHash = idNameHash;
  }

  public String getNameById(String id) {
    return idNameHash.get(id);
  }

  public Set<String> getNamesByIds(Set<String> ids) {
    return ids.stream().map(idNameHash::get).sorted()
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
