package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.*;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.RxJava3CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureService {
    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final LectureProvider lectureProvider;

    @Autowired
    private final LectureDao lectureDao;

    public LectureService(jwtUtility jwt, LectureProvider lectureProvider, LectureDao lectureDao){
        this.jwt = jwt;
        this.lectureProvider = lectureProvider;
        this.lectureDao = lectureDao;
    }

    public boolean createLectureScrap(int userIdx, int lectureIdx, boolean checkParameter) throws BasicException{
        try{
            return lectureDao.createLectureScrap(userIdx, lectureIdx, checkParameter);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean deleteLectureScrap(int userIdx, int lectureIdx) throws BasicException{
        try{
            return lectureDao.deleteLectureScrap(userIdx, lectureIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public GetLectureListPageRes getLectureList(int userIdx, int pageNum) throws BasicException{
        try{
            return lectureDao.getLectureList(userIdx, pageNum);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetLectureScrapListRes> getScrapLectureList(int userIdx) throws BasicException{
        try{
            return lectureDao.getScrapLectureList(userIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public GetLectureRes getLecture(int lectureIdx, int userIdx) throws BasicException{
        try{
            return lectureDao.getLecture(lectureIdx, userIdx);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetLectureSearchListRes> searchLecture(String langTag, String typeTag, String pricing, String keyword, int userIdx) throws BasicException {
        try {
            if (!lectureProvider.checkSearchRowExist(langTag, typeTag, pricing, keyword)) throw new BasicException(BasicServerStatus.SEARCH_TARGET_NOT_EXIST);

                return lectureDao.searchLecture(langTag, typeTag, pricing, keyword, userIdx);
        } catch (BasicException exception) {
            throw new BasicException(exception.getStatus());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetRoadmapListRes> getRoadmapList() throws BasicException{
        try{
            return lectureDao.getRoadmapList();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetRoadmapRes> getRoadmapView(String mod, int userIdx) throws BasicException{
        try{
            return lectureDao.getRoadmapView(mod, userIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public LectureReviewRes createLectureReview(int lectureIdx, int authorIdx, String authorName, String content) throws BasicException{
        try{
            return lectureDao.createLectureReview(lectureIdx, authorIdx, authorName, content);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public GetLectureReviewPageRes getLectureReviewList(int lectureIdx, int pageNum) throws BasicException{

        try{
            return lectureDao.getLectureReviewList(lectureIdx, pageNum);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public LectureLikeRes createLectureLike(int lectureIdx, int userIdx) throws BasicException{
        try{
            return lectureDao.createLectureLike(lectureIdx, userIdx);

        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean lectureExist(int lectureIdx) throws BasicException{
        try{
            return lectureDao.lectureExist(lectureIdx);

        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public LectureLikeRes deleteLectureLike(int lectureIdx, int userIdx) throws BasicException{
        try{
            return lectureDao.deleteLectureLike(lectureIdx, userIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public PostRoadmapCopyRes copyRoadmap(PostRoadmapCopyReq postRoadmapCopyReq, int userIdx) throws BasicException{

        try{
            return lectureDao.copyRoadmap(postRoadmapCopyReq, userIdx);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
}
