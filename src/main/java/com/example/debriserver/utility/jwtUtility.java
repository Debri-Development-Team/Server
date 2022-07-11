package com.example.debriserver.utility;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.secretResource.SecretResource;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class jwtUtility {
    private static final int JWT_EXPIRE_TIME = 604800000;

    public static String createToken(int userIdx) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + JWT_EXPIRE_TIME);

        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("userIdx", userIdx)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SecretResource.JWT_KEY)
                .compact();

    }

    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
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
