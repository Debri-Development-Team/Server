package com.example.debriserver.core.lecture;

import com.example.debriserver.core.lecture.Model.AddLectureReq;
import com.example.debriserver.core.lecture.Model.AddLectureRes;
import com.example.debriserver.core.lecture.Model.SearchLectureReq;
import com.example.debriserver.core.lecture.Model.SearchLectureRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class LectureDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public AddLectureRes addLecture(AddLectureReq addLectureReq){
        AddLectureRes addLectureRes = new AddLectureRes(true);
        String query = "INSERT INTO Lecture\n" +
                "    (lectureName, lectureDescription, language, difficulty,\n" +
                "     lectureKind, pricing, materialUrl, imgUrl)\n" +
                "    VALUE(?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] addLectureParameters = new Object[]
                {
                addLectureReq.getLectureName(),
                addLectureReq.getLectureDescription(),
                addLectureReq.getLanguage(),
                addLectureReq.getDifficulty(),
                addLectureReq.getLectureKind(),
                addLectureReq.getPricing(),
                addLectureReq.getMaterialUrl(),
                addLectureReq.getImgUrl()};

        int result = this.jdbcTemplate.update(query, addLectureParameters);

        if(result > 0) addLectureRes.setAddResult(true);
        else addLectureRes.setAddResult(false);

        return addLectureRes;
    }

    /*public SearchLectureRes searchLecture(SearchLectureReq searchLectureReq){

    }*/
}
