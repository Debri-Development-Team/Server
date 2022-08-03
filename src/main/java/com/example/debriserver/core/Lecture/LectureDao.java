package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Lecture.Model.ChListRes;
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
        String getQuery = "SELECT lectureIdx, lectureName, chNumber, langTag, pricing, type FROM Lecture WHERE status = 'ACTIVE';";

        return this.jdbcTemplate.query(getQuery, (rs, rowNum) -> new GetLectureListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chNumber"),
                        rs.getString("langTag"),
                        rs.getString("pricing"),
                        rs.getString("type")
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
                "SELECT L.lectureIdx, lectureName, chNumber, langTag, pricing, type\n" +
                "FROM Lecture as L LEFT JOIN LectureScrap as LC ON L.lectureIdx = LC.lectureIdx\n" +
                "WHERE L.status = 'ACTIVE' and LC.status = 'ACTIVE' and LC.userIdx = ?;";


        return this.jdbcTemplate.query(getQuery,(rs, rowNum)
                -> new GetLectureListRes(
                    rs.getInt("lectureIdx"),
                    rs.getString("lectureName"),
                    rs.getInt("chNumber"),
                    rs.getString("langTag"),
                    rs.getString("pricing"),
                    rs.getString("type")
        ), userIdx);
    }
    /**
     * 강의 상세 내용 조회
     * */
    public GetLectureRes getLecture(int lectureIdx) {
        String updateQuery = "UPDATE Lecture SET chNumber = (SELECT COUNT(*) FROM (SELECT chIdx FROM Ch_Lecture WHERE lectureIdx = ?) as sub) WHERE lectureIdx = ?;";
        String getQuery = "SELECT lectureIdx, lectureName, lectureDesc, langTag, pricing, srcLink, type, chNumber FROM Lecture WHERE lectureIdx = ? and status = 'ACTIVE';";
        String getListQuery = "SELECT chIdx, lectureIdx, chName, chOrder FROM Chapter WHERE lectureIdx = ? and status = 'ACTIVE';";

        Object[] updateParameter = new Object[]{
                lectureIdx,
                lectureIdx
        };

        this.jdbcTemplate.update(updateQuery, updateParameter);

        return this.jdbcTemplate.queryForObject(getQuery, (rs, rowNum)
                -> new GetLectureRes(
                rs.getInt("lectureIdx"),
                rs.getString("lectureName"),
                rs.getString("lectureDesc"),
                rs.getString("langTag"),
                rs.getString("pricing"),
                rs.getString("srcLink"),
                rs.getString("type"),
                rs.getInt("chNumber"),
                this.jdbcTemplate.query(getListQuery,((rs1, rowNum1)
                        -> new ChListRes(
                                rs1.getInt("chIdx"),
                                rs1.getInt("lectureIdx"),
                                rs1.getString("chName"),
                                rs1.getInt("chOrder")
                )), lectureIdx)


        ), lectureIdx);
    }

    /**
     * 강의 검색
     * */
    public List<GetLectureListRes> searchLecture(String langTag, String typeTag, String pricing, String keyword) {
        String getQuery =
                "SELECT lectureIdx, lectureName, chNumber, langTag, pricing, type FROM Lecture\n" +
                "WHERE\n" +
                "(CASE WHEN STRCMP('Front', ?) = 0 THEN langTag = 'Front' WHEN STRCMP('Back', ?) = 0 THEN langTag = 'Back'\n" +
                "WHEN STRCMP('Python', ?) = 0 THEN langTag = 'Python' WHEN STRCMP('C 언어', ?) = 0 THEN langTag = 'C 언어'\n" +
                "ELSE (langTag = 'Front' or langTag = 'Back' or langTag = 'Python' or langTag = 'C 언어') END)\n" +
                "and\n" +
                "(CASE WHEN STRCMP('서적', ?) = 0 THEN type = '서적' WHEN STRCMP('영상', ?) = 0 THEN type = '영상' ELSE (type = '서적' or type = '영상') END)\n" +
                "and\n" +
                "(CASE WHEN STRCMP('무료', ?) = 0 THEN pricing = '무료' WHEN STRCMP('유료', ?) = 0 THEN pricing = '유료' ELSE (pricing = '무료' or pricing = '유료') END)\n" +
                "and\n" +
                "lectureName LIKE" + "'%" + keyword + "%'\n" +
                "and status = 'ACTIVE';";

        Object[] parameters = new Object[]{
                langTag,
                langTag,
                langTag,
                langTag,
                typeTag,
                typeTag,
                pricing,
                pricing
        };

        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new GetLectureListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chNumber"),
                        rs.getString("langTag"),
                        rs.getString("pricing"),
                        rs.getString("type")
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
        String checkQuery =
                "SELECT COUNT(*)\n" +
                "FROM(\n" +
                "SELECT lectureIdx, lectureName, chNumber, langTag, pricing, type FROM Lecture\n" +
                "WHERE\n" +
                "(CASE WHEN STRCMP('Front', ?) = 0 THEN langTag = 'Front' WHEN STRCMP('Back', ?) = 0 THEN langTag = 'Back'\n" +
                "WHEN STRCMP('Python', ?) = 0 THEN langTag = 'Python' WHEN STRCMP('C 언어', ?) = 0 THEN langTag = 'C 언어'\n" +
                "ELSE (langTag = 'Front' or langTag = 'Back' or langTag = 'Python' or langTag = 'C 언어') END)\n" +
                "and\n" +
                "(CASE WHEN STRCMP('서적', ?) = 0 THEN type = '서적' WHEN STRCMP('영상', ?) = 0 THEN type = '영상' ELSE (type = '서적' or type = '영상') END)\n" +
                "and\n" +
                "(CASE WHEN STRCMP('무료', ?) = 0 THEN pricing = '무료' WHEN STRCMP('유료', ?) = 0 THEN pricing = '유료' ELSE (pricing = '무료' or pricing = '유료') END)\n" +
                "and\n" +
                "lectureName LIKE" + "'%" + keyword + "%'\n" +
                "and status = 'ACTIVE') as R;";

        Object[] parameters = new Object[]{
                langTag,
                langTag,
                langTag,
                langTag,
                typeTag,
                typeTag,
                pricing,
                pricing
        };

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, parameters);
    }
}
