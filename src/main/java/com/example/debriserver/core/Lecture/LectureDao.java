package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Lecture.Model.GetLectureListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LectureDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 전체 강의 리스트 조회
     * */
    public List<BasicResponse<GetLectureListRes>> getLectureList() {
        String getQuery = "";
    }

    /**
     * 강의 스크랩 생성
     * */
    public boolean createLectureScrap(int userIdx, int lectureIdx, boolean checkParameter) {
        String insertQuery = "INSERT INTO LectureScrap(userIdx, lectureIdx) VALUES(?, ?);";
        String updateQuery = "UPDATE LectureScrap SET status = 'ACTIVE' WHERE userIdx = ? and lectureIdx = ?;";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        if(checkParameter){
            //update
            return 0 < this.jdbcTemplate.update(updateQuery, parameters);
        }
        else {
            //insert
            this.jdbcTemplate.update(insertQuery, parameters);
            return true;
        }

    }

    /**
     * 강의 스크랩 삭제
     * */
    public boolean deleteLectureScrap(int userIdx, int lectureIdx){
        String updateQuery = "UPDATE LectureScrap SET status = 'INACTIVE' WHERE userIdx = ? and lectureIdx = ?;";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 0 < this.jdbcTemplate.update(updateQuery, parameters);
    }
    /**
     * 강의가 존재 하는지 체크하는 메서드
     * @return true: 존재, false: 없음
     * */
    public boolean checkLectureExist(int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT lectureIdx FROM Lecture WHERE lectureIdx = ?);";

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, lectureIdx);
    }

    /**
     * 이미 스크랩 한 강의인지 체크하는 메서드
     * @return true: 이미 했음, false: 안했음
     * */
    public boolean checkLectureScrapExist(int userIdx, int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'ACTIVE');";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }

    /**
     * 이미 스크랩 삭제한 강의인지 체크하는 메서드
     * @return true: 이미 삭제 했음, false: 삭제 안했음
     * */
    public boolean checkLectureUnscrapExist(int userIdx, int lectureIdx){
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'INACTIVE');";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }

    /**
     * 기존 스크랩 데이터가 존재하는지 체크하는 메서드
     * @return true: 있음, false: 없음
     * */
    public boolean checkLectureScrapDataExist(int userIdx, int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and lectureIdx = ?);";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }


}
