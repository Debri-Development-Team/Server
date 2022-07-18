package com.example.debriserver.utility;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.secretResource.SecretResource;
import com.example.debriserver.utility.Model.RefreshJwtRes;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class jwtUtility {
    private static final int JWT_EXPIRE_TIME = 604800000;

    /**
     * Jwt Token 생성 메서드
     * */
    public static String createToken(int userIdx) {

        Date now = new Date();
        Date expireDate = new Date(System.currentTimeMillis()*600000*6);


        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userIdx", userIdx)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SecretResource.JWT_KEY)
                .compact();

    }
    /**
     * Refresh Token 생성 매서드
     * */
    public static String createRefreshToken() {
        Date now = new Date();
        Date refreshExpireDate = new Date(System.currentTimeMillis()*600000*6*24);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshExpireDate)
                .signWith(SignatureAlgorithm.HS256, SecretResource.JWT_KEY)
                .compact();
    }

    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("ACCESS-TOKEN");
    }

    /**
     * AccessToken 재발급 메서드
     * */
    public RefreshJwtRes refreshToken(String accessToken, String refreshToken) throws BasicException{

        int userIdx = parseJwt(accessToken);

        return new RefreshJwtRes(userIdx, createToken(userIdx), createRefreshToken());
    }

    /**
     * AccessToken 만료 확인 메서드
     * @return 만료: true 아직 유효: false
     * */
    public boolean isJwtExpired(String accessToken){
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SecretResource.JWT_KEY).parseClaimsJws(accessToken);
            return claims.getBody().getExpiration().before(new Date());
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * userIdx 추출
     * @return int
     * @throws BasicException
     * */
    public int getUserIdx() throws BasicException{
        // JWT 가져오기
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BasicException(BasicServerStatus.JWT_NOT_EXIST);
        }

        //JWT 파싱
        return parseJwt(accessToken);
    }

    /**
     * Jwt 파싱해서 userIdx를 리턴하는 메서드
     * */
    public int parseJwt(String accessToken) throws BasicException{
        Jws<Claims> claimsJws;
        try{
            claimsJws = Jwts.parser()
                    .setSigningKey(SecretResource.JWT_KEY)
                    .parseClaimsJws(accessToken);
        } catch (SignatureException exception){
            throw new BasicException(BasicServerStatus.INVALID_SIGNATURE);
        } catch (MalformedJwtException exception){
            throw new BasicException(BasicServerStatus.INVALID_JWT);
        } catch (ExpiredJwtException exception){
            throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException exception){
            throw new BasicException(BasicServerStatus.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException exception){
            throw new BasicException(BasicServerStatus.EMPTY_JWT_CLAIMS_STRING);
        }

        //Return userIdx
        return claimsJws.getBody().get("userIdx", Integer.class);
    }
}
