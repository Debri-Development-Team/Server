package com.example.debriserver.core.lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.lecture.Model.AddLectureReq;
import com.example.debriserver.core.lecture.Model.AddLectureRes;
import com.example.debriserver.core.lecture.Model.SearchLectureReq;
import com.example.debriserver.core.lecture.Model.SearchLectureRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureService {
    private final LectureDao lectureDao;

    @Autowired
    public LectureService(LectureDao lectureDao){
        this.lectureDao = lectureDao;
    }

    public AddLectureRes addLecture(AddLectureReq addLectureReq) throws BasicException {
        try{
            return lectureDao.addLecture(addLectureReq);
        }catch(Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }

   /* public SearchLectureRes searchLecture(SearchLectureReq searchLectureReq) throws BasicException {

        try{
            return lectureDao.searchLecture(searchLectureReq);
        }catch (Exception exception){
            throw new BasicException(BasicServerStatus.DB_ERROR);
        }
    }*/
}
