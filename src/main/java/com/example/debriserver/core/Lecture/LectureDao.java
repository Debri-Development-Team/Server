package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Lecture.Model.GetLectureListRes;
import com.example.debriserver.core.Lecture.Model.GetLectureRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LectureDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 전체 강의 리스트 조회
     */
    public List<GetLectureListRes> getLectureList() {
        String getQuery =
                "SELECT L.lectureIdx, lectureName, chapterNumber, L.langTag, pricing, SUB.matreialType\n" +
                        "FROM\n" +
                        "(Lecture as L LEFT JOIN\n" +
                        "(SELECT M.chapterNumber, MAT.lectureIdx, M.matreialType FROM Material as M LEFT JOIN Material_Lecture as MAT ON M.materialIdx = MAT.materialIdx) as SUB\n" +
                        "ON L.lectureIdx = SUB.lectureIdx);";

        return this.jdbcTemplate.query(getQuery, (rs, rowNum) -> new GetLectureListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chapterNumber"),
                        rs.getString("langTag"),
                        rs.getString("pricing"),
                        rs.getString("materialType")
                )
        );
    }

    /**
     * 강의 스크랩 생성
     */
    public boolean createLectureScrap(int userIdx, int lectureIdx, boolean checkParameter) {
        String insertQuery = "INSERT INTO LectureScrap(userIdx, lectureIdx) VALUES(?, ?);";
        String updateQuery = "UPDATE LectureScrap SET status = 'ACTIVE' WHERE userIdx = ? and lectureIdx = ?;";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        if (checkParameter) {
            //update
            return 0 < this.jdbcTemplate.update(updateQuery, parameters);
        } else {
            //insert
            this.jdbcTemplate.update(insertQuery, parameters);
            return true;
        }

    }

    /**
     * 강의 스크랩 삭제
     */
    public boolean deleteLectureScrap(int userIdx, int lectureIdx) {
        String updateQuery = "UPDATE LectureScrap SET status = 'INACTIVE' WHERE userIdx = ? and lectureIdx = ?;";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 0 < this.jdbcTemplate.update(updateQuery, parameters);
    }
    /**
     * 스크랩한 강의 리스트 조회
     * */
    public List<GetLectureListRes> getScrapLectureList(int userIdx) {
        String getQuery =
                "SELECT SUB2.lectureIdx, lectureName, chapterNumber, SUB2.langTag, pricing, SUB2.materialType\n" +
                "                FROM (SELECT L.*, SUB.chapterNumber, SUB.materialType FROM (Lecture as L LEFT JOIN\n" +
                "                (SELECT M.chapterNumber, MAT.lectureIdx, M.materialType FROM Material as M LEFT JOIN Material_Lecture as MAT ON M.materialIdx = MAT.materialIdx) as SUB\n" +
                "                ON L.lectureIdx = SUB.lectureIdx)) as SUB2 LEFT JOIN\n" +
                "                LectureScrap as LS ON SUB2.lectureIdx = LS.lectureIdx\n" +
                "                WHERE LS.status = 'ACTIVE' and LS.userIdx = ?;";

        return this.jdbcTemplate.query(getQuery,(rs, rowNum)
                -> new GetLectureListRes(
                    rs.getInt("lectureIdx"),
                    rs.getString("lectureName"),
                    rs.getInt("chapterNumber"),
                    rs.getString("langTag"),
                    rs.getString("pricing"),
                    rs.getString("materialType")
        ), userIdx);
    }
    /**
     * 강의 상세 내용 조회
     * */
    public GetLectureRes getLecture(int lectureIdx) {
        String getQuery =
                "SELECT L.*, SUB.materialIdx, SUB.materialName,SUB.materialAuthor, SUB.publisher, SUB.publishDate, SUB.materialLink, SUB.chapterNumber FROM\n" +
                "Lecture as L LEFT JOIN\n" +
                "(SELECT M.*, ML.lectureIdx FROM Material as M LEFT JOIN Material_Lecture as ML ON M.materialIdx = ML.materialIdx) as SUB\n" +
                "ON L.lectureIdx = SUB.lectureIdx WHERE L.lectureIdx = ?;";

        return this.jdbcTemplate.queryForObject(getQuery, (rs, rowNum)
                -> new GetLectureRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getString("lectureDesc"),
                        rs.getString("langTag"),
                        rs.getInt("matNumber"),
                        rs.getString("pricing"),
                        rs.getString("createdAt"),
                        rs.getString("updatedAt"),
                        rs.getString("complete"),
                        rs.getInt("materialIdx"),
                        rs.getString("materialName"),
                        rs.getString("materialAuthor"),
                        rs.getString("publisher"),
                        rs.getString("publishDate"),
                        rs.getString("materialLink"),
                        rs.getInt("chapterNumber")
        ), lectureIdx);
    }

    /**
     * 강의 검색
     * */
    public List<GetLectureListRes> searchLecture(String langTag, String typeTag, String pricing, String keyword) {
        String getQuery ="";

        Object[] parameters = new Object[]{
                pricing,
                pricing,
                langTag,
                langTag,
                typeTag,
                typeTag,
        };

        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new GetLectureListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chapterNumber"),
                        rs.getString(langTag),
                        rs.getString("pricing"),
                        rs.getString("materialType")
                ), parameters);
    }

    /**
     * 강의가 존재 하는지 체크하는 메서드
     *
     * @return true: 존재, false: 없음
     */
    public boolean checkLectureExist(int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT lectureIdx FROM Lecture WHERE lectureIdx = ?);";

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, lectureIdx);
    }

    /**
     * 이미 스크랩 한 강의인지 체크하는 메서드
     *
     * @return true: 이미 했음, false: 안했음
     */
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
     *
     * @return true: 이미 삭제 했음, false: 삭제 안했음
     */
    public boolean checkLectureUnscrapExist(int userIdx, int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'INACTIVE');";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }

    /**
     * 기존 스크랩 데이터가 존재하는지 체크하는 메서드
     *
     * @return true: 있음, false: 없음
     */
    public boolean checkLectureScrapDataExist(int userIdx, int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and lectureIdx = ?);";

        Object[] parameters = new Object[]{
                userIdx,
                lectureIdx
        };

        return 1 == this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }

    /**
     * 유효한 스크랩 데이터가 존재하는지 체크하는 메서드
     *
     * @return true: 있음 false 없음
     */
    public boolean checkScrapActiveLectureExist(int userIdx) {
        String checkQuery = "SELECT EXISTS(SELECT userIdx, lectureIdx FROM LectureScrap WHERE userIdx = ? and status = 'ACTIVE');";

        return 0 < this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public int checkSearchRowExist(String langTag, String typeTag, String pricing, String keyword) {
        String checkQuery = "";

        Object[] parameters = new Object[]{
                pricing,
                pricing,
                langTag,
                langTag,
                typeTag,
                typeTag,
        };
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }
}
