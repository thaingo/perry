package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.Office;
import gov.ca.cwds.idm.persistence.cwscms.repository.OfficeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class OfficeService {

  @Autowired
  private OfficeRepository officeRepository;

  @Transactional(value = "transactionManager", readOnly = true)
  public List<Office> getOffices() {
    return officeRepository.findOffices();
  }

}
