package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.Model.*;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.debriserver.basicModels.BasicServerStatus.*;

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

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq, int userIdx) throws BasicException{
        try{
            PostCurriCreateRes postCurriCreateRes = curriDao.createCurri(postCurriCreateReq, userIdx);

            return postCurriCreateRes;
        } catch(Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean curriModify(PostCurriModifyReq postCurriModifyReq, int userIdx) throws BasicException{
        try{
            return curriDao.curriModify(postCurriModifyReq, userIdx);
        } catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public void deleteCurri(int curriIdx, int userIdx) throws BasicException {
        int result;

        try{
            if (curriProvider.checkCurriScrap(curriIdx) > 0 ){
                curriDao.disconnectAllScrap(curriIdx);
            }

            result = curriDao.deleteCurri(curriIdx, userIdx);

            if (result == 0){
                throw new BasicException(DB_ERROR);
            }

        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public void completeChapter(PatchChapterStatuReq patchChapterCompleteReq, int userIdx) throws BasicException {
        try {

            int result = curriDao.completeChapter(patchChapterCompleteReq, userIdx);
            if (result == 0){
                throw new BasicException(DB_ERROR);
            }
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public void cancelCompleteChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx) throws BasicException{
        try {
            int result = curriDao.completecancelChapter(patchChapterStatuReq, userIdx);
            if (result == 0){
                throw new BasicException(DB_ERROR);
            }
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public GetThisCurriRes getThisCurri(GetThisCurriReq getThisCurriReq, int userIdx) throws BasicException{
        try{
            return curriDao.getThisCurri(getThisCurriReq, userIdx);

        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean insertLecture(PostInsertLectureReq postInsertLectureReq, int userIdx) throws BasicException{
        try{
            return curriDao.insertLecture(postInsertLectureReq, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }
}
