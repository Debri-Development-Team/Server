package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.Model.GetScrapListRes;
import com.example.debriserver.core.Curri.Model.PostCurriScrapRes;
import com.example.debriserver.core.User.UserProvider;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.debriserver.basicModels.BasicServerStatus.DB_ERROR;

@Service
public class CurriService {
    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final CurriProvider curriProvider;

    @Autowired
    private final CurriDao curriDao;

    @Autowired
    private final UserProvider userProvider;

    public CurriService(jwtUtility jwt, CurriProvider curriProvider, CurriDao curriDao, UserProvider userProvider){
        this.jwt = jwt;
        this.curriProvider = curriProvider;

        this.curriDao = curriDao;
        this.userProvider = userProvider;
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

    public void scrapCancel(int scrapIdx) throws BasicException {

        try{



            curriDao.scrapCancel(scrapIdx);

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

    public boolean checkUnScrapedCurriExist(int scrapIdx) throws BasicException {

        try {

            boolean result = curriDao.checkUnScrapedCurriExist(scrapIdx);

            return result;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    public boolean checkUnScrapedCurriExist2(int curriIdx, int userIdx) throws BasicException {

        try {

            boolean result = curriDao.checkUnScrapedCurriExist2(curriIdx, userIdx);

            return result;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    public List<GetScrapListRes> getCurriScrapList(int userIdx) throws BasicException {

        try {

            List<GetScrapListRes> getCurriScrapListRes = curriDao.getCurriScrapList(userIdx);

            return getCurriScrapListRes;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }

    }

    public boolean checkScrapExist (int userIdx)throws BasicException{
        try{
            boolean result = curriDao.checkScrapExist(userIdx);

            return result;

        }catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }


    }
}
