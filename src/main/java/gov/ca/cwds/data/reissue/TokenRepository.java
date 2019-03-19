package gov.ca.cwds.data.reissue;

import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by TPT2 on 10/26/2017.
 */
@Repository
public interface TokenRepository extends JpaRepository<PerryTokenEntity, String> {
  List<PerryTokenEntity> findByAccessCode(String accessCode);

  @Modifying
  long deleteByCreatedDateBefore(Timestamp date);

  @Modifying
  long deleteByLastUsedDateBeforeOrLastUsedDateIsNull(Timestamp date);

}
