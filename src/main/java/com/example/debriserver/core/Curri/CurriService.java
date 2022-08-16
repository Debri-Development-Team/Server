package com.example.debriserver.core.Curri;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.core.User.UserDao;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Curri.Model.*;
import com.example.debriserver.core.User.UserProvider;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.debriserver.basicModels.BasicServerStatus.*;

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


    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq, int userIdx) throws BasicException{
        try{
            PostCurriCreateRes postCurriCreateRes = curriDao.createCurri(postCurriCreateReq, userIdx);

            return postCurriCreateRes;
        } catch(Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean curriModify(PostCurriModifyReq postCurriModifyReq, int userIdx) throws BasicException{
        try{
            return curriDao.curriModify(postCurriModifyReq, userIdx);
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean curriNameModify(PacthCurriNameModifyReq pacthCurriNameModifyReq, int userIdx) throws BasicException{
        try{
            int curriIdx = pacthCurriNameModifyReq.getCurriIdx();

            if(!curriProvider.checkCurriExist(curriIdx, userIdx)) throw new BasicException(CURRI_EMPTY_ID);

            return curriDao.curriNameModify(pacthCurriNameModifyReq, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean curriVisibleStatusModify(PacthCurriVisibleStatusModifyReq pacthCurriVisibleStatusModifyReq, int userIdx) throws BasicException{
        try{
            int curriIdx = pacthCurriVisibleStatusModifyReq.getCurriIdx();

            if(!curriProvider.checkCurriExist(curriIdx, userIdx)) throw new BasicException(CURRI_EMPTY_ID);

            return curriDao.curriVisibleStatusModify(pacthCurriVisibleStatusModifyReq, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public boolean curriStatusModify(PacthCurriStatusModifyReq pacthCurriStatusModifyReq, int userIdx) throws BasicException{
        try{
            int curriIdx = pacthCurriStatusModifyReq.getCurriIdx();

            if(!curriProvider.checkCurriExist(curriIdx, userIdx)) throw new BasicException(CURRI_EMPTY_ID);

            return curriDao.curriStatusModify(pacthCurriStatusModifyReq, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
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

    public void completeChapter(PatchChapterStatuReq patchChapterCompleteReq) throws BasicException {
        try {

            int result = curriDao.completeChapter(patchChapterCompleteReq);
            if (result == 0){
                throw new BasicException(DB_ERROR);
            }
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public void cancelCompleteChapter(PatchChapterStatuReq patchChapterStatuReq) throws BasicException{
        try {
            int result = curriDao.completecancelChapter(patchChapterStatuReq);
            if (result == 0){
                throw new BasicException(DB_ERROR);
            }
        } catch (Exception exception){
            throw new BasicException(DB_ERROR);
        }
    }

    public GetThisCurriRes getThisCurri(int curriIdx, int userIdx) throws BasicException{
        try{
            return curriDao.getThisCurri(curriIdx, userIdx);
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

    public boolean curriReset(int curriIdx, int userIdx)throws BasicException{
        try{
            return curriDao.curriReset(curriIdx, userIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public CurriReviewRes createCurriReview(PostCurriReviewReq postCurriReviewReq, int authorIdx) throws BasicException{
        try{
            return curriDao.createCurriReview(postCurriReviewReq, authorIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
        }
    }

    public List<CurriReviewRes> getCurriReviewList(int curriIdx) throws BasicException{
        try{
            return curriDao.getCurriReviewList(curriIdx);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(DB_ERROR);
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
