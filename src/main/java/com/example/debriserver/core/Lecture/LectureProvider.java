package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.utility.jwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final jwtUtility jwt;

    @Autowired
    private final LectureDao lectureDao;

    public LectureProvider(jwtUtility jwt, LectureDao lectureDao){
        this.jwt = jwt;
        this.lectureDao = lectureDao;
    }


    public boolean checkLectureExist(int lectureIdx) throws BasicException{
        try{
            return lectureDao.checkLectureExist(lectureIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkLectureScrapExist(int userIdx, int lectureIdx) throws BasicException{
        try{
            return lectureDao.checkLectureScrapExist(userIdx, lectureIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkLectureUnscrapExist(int userIdx, int lectureIdx) throws BasicException{
        try{
            return lectureDao.checkLectureUnscrapExist(userIdx, lectureIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkLectureScrapDataExist(int userIdx, int lectureIdx) throws BasicException{
        try{
            return lectureDao.checkLectureScrapDataExist(userIdx, lectureIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

    public boolean checkScrapActiveLectureExist(int userIdx) throws  BasicException{
        try{
            return lectureDao.checkScrapActiveLectureExist(userIdx);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }
}
