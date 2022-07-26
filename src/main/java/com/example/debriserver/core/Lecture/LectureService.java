package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Lecture.Model.GetLectureListRes;
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
}
