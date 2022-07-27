package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.model.PostCurriScrapRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;
import static com.example.debriserver.basicModels.BasicServerStatus.USERS_EMPTY_USER_ID;

@Service
public class CurriService {
    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final CurriProvider curriProvider;

    @Autowired
    private final CurriDao curriDao;

    public CurriService(jwtUtility jwt, CurriProvider curriProvider, CurriDao curriDao){
        this.jwt = jwt;
        this.curriProvider = curriProvider;
        this.curriDao = curriDao;
    }

    /**
     * 커리큘럼 스크랩
     * @param curriIdx
     * @param userIdx
     * @return
     * @throws BasicException
     */
    public PostCurriScrapRes scrapCurri(int curriIdx, int userIdx) throws BasicException {

        try {

            PostCurriScrapRes postCurriScrapRes = curriDao.scrapCurri(curriIdx,userIdx);

            return postCurriScrapRes;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    public void scrapCancel(String userId) throws BasicException {

        try{

            if (userProvider.checkUserExist(userId) == false) {
                throw new BasicException(USERS_EMPTY_USER_ID);
            }

            int result = userDao.deleteUser(userId);
            if (result == 0) {
                throw new BasicException(DB_ERROR);
            }
        }
        catch (Exception exception) {
            throw new BasicException(DB_ERROR);
        }
    }
    public boolean checkScrapedCurriExist(int curriIdx ,int userIdx) throws BasicException {

        try {

            boolean result = curriDao.checkScrapedCurriExist(curriIdx,userIdx);

            return result;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }
}
