package com.example.debriserver.core.lecture;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.lecture.Model.AddLectureReq;
import com.example.debriserver.core.lecture.Model.AddLectureRes;
import com.example.debriserver.core.lecture.Model.SearchLectureReq;
import com.example.debriserver.core.lecture.Model.SearchLectureRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Basic;

@RestController
@RequestMapping("/lecture")
public class LectureController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final LectureService lectureService;
    @Autowired
    private final LectureDao lectureDao;

    public LectureController(LectureService lectureService, LectureDao lectureDao) {
        this.lectureService = lectureService;
        this.lectureDao = lectureDao;
    }

    @PostMapping("/add")
    public BasicResponse<AddLectureRes> addLecture(@RequestBody AddLectureReq addLectureReq){

        try{
            AddLectureRes addLectureRes = lectureService.addLecture(addLectureReq);

            return new BasicResponse<>(addLectureRes);
        }catch(BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /*@PostMapping("/search")
    public BasicResponse<SearchLectureRes> searchLecture(@RequestBody SearchLectureReq searchLectureReq) {

        try{
            SearchLectureRes searchLectureRes = lectureService.searchLecture(searchLectureReq);

            return new BasicResponse<>(searchLectureRes);
        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }*/
}
