package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.Curri.model.*;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.debriserver.basicModels.BasicServerStatus.*;

@Service
public class CurriProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final CurriDao curriDao;

    public CurriProvider(jwtUtility jwt, CurriDao curriDao){
        this.jwt = jwt;
        this.curriDao = curriDao;
    }

    public List<GetCurriListRes> getList(int userIdx) throws BasicException{
        try{
            List<GetCurriListRes> getCurriListRes = curriDao.getList(userIdx);
            return getCurriListRes;
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public int checkCurriScrap(int curriIdx) throws BasicException{
        try{
            return curriDao.checkCurriScrap(curriIdx);
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean checkCurriExist(int curriIdx, int userIdx) throws BasicException{
        try{
            return curriDao.checkCurriExist(curriIdx, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }


    public boolean checkChapterStatus(PatchChapterStatuReq patchChapterStatuReq) throws BasicException{
        try {
            return curriDao.checkChapterStatus(patchChapterStatuReq);
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean checkChapterExist(PatchChapterStatuReq patchChapterStatuReq) throws BasicException{
        try{
            return curriDao.checkChapterExist(patchChapterStatuReq);
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }
}