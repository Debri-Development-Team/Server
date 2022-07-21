package com.example.debriserver.core.Jwt;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Jwt.Model.PatchRefreshRes;
import com.example.debriserver.utility.Model.RefreshJwtRes;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtDao jwtDao;
    private final jwtUtility jwtUtility;

    public JwtService(JwtDao jwtDao, jwtUtility jwtUtility)
    {
        this.jwtDao = jwtDao;
        this.jwtUtility = jwtUtility;
    }

    public PatchRefreshRes getRefresh(RefreshJwtRes refreshJwtRes) throws BasicException {

        try{
            return jwtDao.getRefresh(refreshJwtRes);

        }catch(BasicException exception){
            throw exception;
        }
        catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
}
