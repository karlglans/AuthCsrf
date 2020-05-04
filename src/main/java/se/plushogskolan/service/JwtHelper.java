package se.plushogskolan.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.plushogskolan.service.exceptions.BadCredentialsException;

import java.util.Date;

public class JwtHelper {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private String issuer;
  static private final int usersExpireTime = 36000000;

  private Algorithm algorithm;
  private JWTVerifier verifier;

  JwtHelper(String secret, String issuer) {
    this.issuer = issuer;
    this.algorithm = Algorithm.HMAC256(secret);
    this.verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build(); //Reusable verifier instance
  }

  private Date makeExpireDate() {
    Date date = new Date();
    date.setTime(date.getTime() + usersExpireTime);
    return date;
  }

  public String makeToken(String username) {
    return JWT.create()
      .withIssuer(issuer)
      .withClaim("exp", makeExpireDate())
      .withSubject(username)
      .sign(algorithm);
  }

  public String getUsernameFromToken(String token) {
    DecodedJWT jwt;
    try {
      jwt = verifier.verify(token);
    } catch (JWTVerificationException exception) {
      logger.info(String.format("Verification failed for token %s", token));
      throw new BadCredentialsException("Missing Authentication Token");
    }
    return jwt.getSubject();
  }
}
