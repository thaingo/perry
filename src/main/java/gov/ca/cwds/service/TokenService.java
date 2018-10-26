package gov.ca.cwds.service;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.TokenRepository;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.rest.api.domain.PerryException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by TPT2 on 10/27/2017.
 */
@Service
@Transactional(value = "tokenTransactionManager", noRollbackFor = PerryException.class)
public class TokenService {

  private static final String ACCESS_CODE = "Access Code: ";

  private PerryProperties properties;
  private TokenRepository tokenRepository;
  private RandomValueStringGenerator generator = new RandomValueStringGenerator();

  public String issueAccessCode(UniversalUserToken userToken, String ssoToken, String jsonToken, Serializable securityContext) {
    String accessCode = generator.generate();
    PerryTokenEntity perryTokenEntity = new PerryTokenEntity();
    perryTokenEntity.setUser(userToken.getUserId());
    perryTokenEntity.setAccessCode(accessCode);
    perryTokenEntity.setSsoToken(ssoToken);
    perryTokenEntity.setJsonToken(jsonToken);
    perryTokenEntity.setToken(userToken.getToken());
    perryTokenEntity.setSecurityContext(SerializationUtils.serialize(securityContext));
    perryTokenEntity.setLastUsedDate(perryTokenEntity.getCreatedDate());
    deleteExpiredRecords();
    tokenRepository.save(perryTokenEntity);
    return accessCode;
  }

  private void deleteExpiredRecords() {
    Date validLastUsedDate = DateUtils.addMinutes(new Date(), -properties.getTokenRecordTimeout());
    tokenRepository.deleteByLastUsedDateBeforeOrLastUsedDateIsNull(
        new Timestamp(validLastUsedDate.getTime()));
  }

  public String getPerryTokenByAccessCode(String accessCode) {
    List<PerryTokenEntity> tokens = tokenRepository.findByAccessCode(accessCode);
    if (tokens.isEmpty()) {
      throw new PerryException(ACCESS_CODE + accessCode + " is not found");
    }
    if (tokens.size() > 1) {
      tokenRepository.delete(tokens);
      throw new PerryException(ACCESS_CODE + accessCode + " is not unique");
    }
    PerryTokenEntity perryTokenEntity = tokens.get(0);
    if (new Date().after(DateUtils.addMinutes(perryTokenEntity.getCreatedDate(), properties.getJwt().getTimeout()))) {
      tokenRepository.delete(perryTokenEntity);
      throw new PerryException(ACCESS_CODE + accessCode + " is expired");
    }
    perryTokenEntity.setAccessCode(null);
    return perryTokenEntity.getToken();
  }

  public void update(PerryTokenEntity perryTokenEntity) {
    tokenRepository.save(perryTokenEntity);
  }

  public String deleteToken(String token) {
    Optional<String> accessToken =
        Optional.ofNullable(tokenRepository.findOne(token)).map(PerryTokenEntity::getSsoToken);
    try {
      tokenRepository.delete(token);
    } catch (Exception e) {
      throw new PerryException("token: " + token + " is not present!", e);
    }
    return accessToken.orElseThrow(() -> new PerryException("token entry: '" + token + "' is invalid!"));
  }

  public PerryTokenEntity getPerryToken(String token) {
    PerryTokenEntity perryTokenEntity = tokenRepository.findOne(token);
    if(perryTokenEntity == null) {
      throw new PerryException("token: " + token + " is not found");
    }
    validateToken(perryTokenEntity);
    perryTokenEntity.setLastUsedDate(new Date());
    return perryTokenEntity;
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }

  @Autowired
  public void setTokenRepository(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  private void validateToken(PerryTokenEntity perryTokenEntity) {
    Date expirationDate =
        DateUtils.addMinutes(perryTokenEntity.getLastUsedDate(), properties.getTokenRecordTimeout());
    if (new Date().after(expirationDate)) {
      tokenRepository.delete(perryTokenEntity);
      throw new PerryException("Token " + perryTokenEntity.getToken() +
          " for user:" + perryTokenEntity.getUser() + " is expired");
    }
  }
}
