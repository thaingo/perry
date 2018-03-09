package gov.ca.cwds.service;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.TokenRepository;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by TPT2 on 10/27/2017.
 */
@Service
@Transactional(value = "tokenTransactionManager", noRollbackFor = PerryException.class)
public class TokenService {
  private PerryProperties properties;
  private TokenRepository tokenRepository;
  private RandomValueStringGenerator generator = new RandomValueStringGenerator();

  public String issueAccessCode(UniversalUserToken userToken, OAuth2AccessToken accessToken) {
    String accessCode = generator.generate();
    PerryTokenEntity perryTokenEntity = new PerryTokenEntity();
    perryTokenEntity.setCreatedDate(LocalDateTime.now());
    perryTokenEntity.setUser(userToken.getUserId());
    perryTokenEntity.setAccessCode(accessCode);
    perryTokenEntity.writeAccessToken(accessToken);
    perryTokenEntity.setToken(userToken.getToken());
    deleteExpiredRecords();
    tokenRepository.save(perryTokenEntity);
    return accessCode;
  }

  private void deleteExpiredRecords() {
    LocalDateTime createdDateTime = LocalDateTime.now().minusDays(properties.getTokenRecordTimeout());
    tokenRepository.deleteByCreatedDateBefore(createdDateTime);
  }

  public String getPerryTokenByAccessCode(String accessCode) {
    List<PerryTokenEntity> tokens = tokenRepository.findByAccessCode(accessCode);
    if (tokens.isEmpty()) {
      throw new PerryException("Access Code: " + accessCode + " is not found");
    }
    if (tokens.size() > 1) {
      tokenRepository.delete(tokens);
      throw new PerryException("Access Code: " + accessCode + " is not unique");
    }
    PerryTokenEntity perryTokenEntity = tokens.get(0);
    if (LocalDateTime.now().isAfter(perryTokenEntity.getCreatedDate().plusMinutes(properties.getJwt().getTimeout()))) {
      tokenRepository.delete(perryTokenEntity);
      throw new PerryException("Access Code: " + accessCode + " is expired");
    }
    perryTokenEntity.setAccessCode(null);
    return perryTokenEntity.getToken();
  }

  public void updateAccessToken(String token, OAuth2AccessToken accessToken) {
    tokenRepository.updateAccessToken(token, (Serializable) accessToken);
  }

  public OAuth2AccessToken deleteToken(String token) {
    Optional<OAuth2AccessToken> accessToken =
        Optional.ofNullable(tokenRepository.findOne(token)).map(PerryTokenEntity::readAccessToken);
    try {
      tokenRepository.delete(token);
    } catch (Exception e) {
      throw new PerryException("token: " + token + " is not present!", e);
    }
    return accessToken.orElseThrow(() -> new PerryException("token entry: '" + token + "' is invalid!"));
  }

  public OAuth2AccessToken getAccessTokenByPerryToken(String token) {
    return Optional.ofNullable(tokenRepository.findOne(token))
        .map(PerryTokenEntity::readAccessToken)
        .orElse(null);
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }

  @Autowired
  public void setTokenRepository(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }
}
