package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.GetLectureListRes;
import com.example.debriserver.core.Lecture.Model.GetLectureRes;
import com.example.debriserver.core.Lecture.Model.GetRoadmapListRes;
import com.example.debriserver.core.Lecture.Model.GetRoadmapRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<GetLectureListRes> getLectureList() throws BasicException{
        try{
            return lectureDao.getLectureList();
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetLectureListRes> getScrapLectureList(int userIdx) throws BasicException{
        try{
            return lectureDao.getScrapLectureList(userIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public GetLectureRes getLecture(int lectureIdx) throws BasicException{
        try{
            return lectureDao.getLecture(lectureIdx);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public List<GetLectureListRes> searchLecture(String langTag, String typeTag, String pricing, String keyword) throws BasicException {
        try {
            if (!lectureProvider.checkSearchRowExist(langTag, typeTag, pricing, keyword)) throw new BasicException(BasicServerStatus.SEARCH_TARGET_NOT_EXIST);

                return lectureDao.searchLecture(langTag, typeTag, pricing, keyword);
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

    public List<GetRoadmapRes> getRoadmapView(int roadmapIdx) throws BasicException{
        try{
            return lectureDao.getRoadmapView(roadmapIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
}
