package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CurriDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq){

        // Curri 테이블에 데이터 저장
        String insertQuery = "";
        Object[] insertCurriParameters = new Object[]{
                postCurriCreateReq.getCurriName(),
                postCurriCreateReq.getCurriAuthor(),
                postCurriCreateReq.getVisibleStatus(),
                postCurriCreateReq.getLangTag(),
                postCurriCreateReq.getOwnerIdx()
        };
        this.jdbcTemplate.update(insertQuery,insertCurriParameters);

        // 선택한 Lecture의 정보를 Material 테이블과 연결
        String getLetureQuery = "";

    }

    public List<GetCurriListRes> getList(int userIdx) {
        String getCurriListQuery ="SELECT distinct c.curriIdx, c.curriName, c.curriAuthor, c.visibleStatus, c.langTag, c.progressRate, c.createdAt, c.status\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE u.userIdx = ? AND u.userIdx = c.ownerIdx AND c.status NOT IN ('DELETE');";

        return this.jdbcTemplate.query(getCurriListQuery,
                (rs, rowNum) -> new GetCurriListRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("c.curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getString("progressRate"),
                        rs.getInt("createdAt"),
                        rs.getString("status")
                ), userIdx);
    }

    public int deleteCurri(int curriIdx){
        String deleteCurriQuery = "UPDATE Curriculum SET status = 'DELETE' WHERE curriIdx = ?;";
        return this.jdbcTemplate.update(deleteCurriQuery, curriIdx);
    }

    public int checkCurriExist(int curriIdx){
        String checkCurriQuery = "SELECT exists(SELECT curriIdx FROM Curriculum WHERE curriIdx = ?;";

        return this.jdbcTemplate.queryForObject(checkCurriQuery,
                int.class,
                curriIdx);

    }

    public boolean checkChapterStatus(PatchChapterStatuReq patchChapterStatuReq){
        String checkChapterStatusQuery = "SELECT exists(\n" +
                "    SELECT chIdx\n" +
                "    FROM Ch_Lecture_Curri\n" +
                "    WHERE chIdx = ? AND chComplete = 'FALSE'\n" +
                "    );";
        int checkChapterStatusParams = patchChapterStatuReq.getChIdx();
        int result = this.jdbcTemplate.queryForObject(checkChapterStatusQuery, int.class, checkChapterStatusParams);
        return result != 0;
    }


    public boolean checkChapterExist(PatchChapterStatuReq patchChapterStatuReq){
        String checkChapterExistQuery = "SELECT exists(\n" +
                "    SELECT chlc.chIdx, chlc.lectureIdx, chlc.curriIdx\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    JOIN (SELECT c.chIdx\n" +
                "          FROM Chapter as c\n" +
                "          RIGHT JOIN Ch_Lecture as chl on chl.chIdx = c.chIdx\n" +
                "          LEFT JOIN Lecture as l on l.lectureIdx = chl.lectureIdx) as ch\n" +
                "    JOIN (SELECT l.lectureIdx\n" +
                "          FROM Lecture as l\n" +
                "          RIGHT JOIN Ch_Lecture as chl on l.lectureIdx = chl.lectureIdx\n" +
                "          RIGHT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx) as l\n" +
                "    JOIN Curriculum as c\n" +
                "    WHERE chlc.chIdx = ? and chlc.lectureIdx = ? and chlc.curriIdx = ? and ch.chIdx = chlc.chIdx and l.lectureIdx = chlc.lectureIdx and c.curriIdx = chlc.curriIdx);";

        Object[] checkChapterExistParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int result = this.jdbcTemplate.queryForObject(checkChapterExistQuery, int.class, checkChapterExistParams);

        return result != 0;
    }

    public int completeChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx){
        String completeChapterQuery = "UPDATE Ch_Lecture_Curri SET chComplete = 'TRUE'\n" +
                "WHERE chIdx = ? and curriIdx = ? and lectureIdx = ?;";

        Object[] completeChapterParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int result = this.jdbcTemplate.update(completeChapterQuery, completeChapterParams);

        String getAllChapterNumberInCurriQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ?;";

        String getCompleteChapterNumberInCurriQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and chComplete = 'TRUE';";

        int getInCurriParams = patchChapterStatuReq.getCurriIdx();

        int chapterInCurri = this.jdbcTemplate.queryForObject(getAllChapterNumberInCurriQurey, int.class, getInCurriParams);
        int completeInCurri = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInCurriQurey, int.class, getInCurriParams);
        float curriRate = completeInCurri / chapterInCurri * 100;

        String updateCurriRateQurey = "UPDATE Curriculum SET progressRate = ? WHERE curriIdx = ?;";

        Object[] updateCurriRateParams = new Object[] {
                curriRate,
                patchChapterStatuReq.getCurriIdx()
        };

        this.jdbcTemplate.update(updateCurriRateQurey, updateCurriRateParams);

        String getAllChapterNumberInLectureQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and lectureIdx = ?;\n";

        String getCompleteChapterNumberInLectureQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and lectureIdx = ? and chComplete = 'TRUE';";

        Object[] getInLectureParams = new Object[]{
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int chapterInLecture = this.jdbcTemplate.queryForObject(getAllChapterNumberInLectureQurey, int.class, getInLectureParams);
        int completeInLeture = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInLectureQurey, int.class, getInLectureParams)
        float lectureRate = completeInLeture / chapterInLecture * 100;

        String updataLectureRateQurey = "UPDATE Lecture_Rate SET progressRate = ? WHERE lectureIdx = ? and userIdx = ?;";

        Object[] updateLectureRateParams = new Object[]{
                lectureRate,
                patchChapterStatuReq.getLectureIdx(),
                userIdx
        };

        this.jdbcTemplate.update(updataLectureRateQurey, updateLectureRateParams);

        return result;
    }

    public int completecancelChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx){
        String completeCancelChapterQurey = "UPDATE Ch_Lecture_Curri SET chComplete = 'FALSE'\n" +
                "WHERE chIdx = ? and curriIdx = ? and lectureIdx = ?;";

        Object[] completeCancelChapterParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int result = this.jdbcTemplate.queryForObject(completeCancelChapterQurey, int.class, completeCancelChapterParams);

        String getAllChapterNumberInCurriQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ?;";

        String getCompleteChapterNumberInCurriQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and chComplete = 'TRUE';";

        int getInCurriParams = patchChapterStatuReq.getCurriIdx();

        int chapterInCurri = this.jdbcTemplate.queryForObject(getAllChapterNumberInCurriQurey, int.class, getInCurriParams);
        int completeInCurri = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInCurriQurey, int.class, getInCurriParams);
        float curriRate = completeInCurri / chapterInCurri * 100;

        String updateCurriRateQurey = "UPDATE Curriculum SET progressRate = ? WHERE curriIdx = ?;";

        Object[] updateCurriRateParams = new Object[] {
                curriRate,
                patchChapterStatuReq.getCurriIdx()
        };

        this.jdbcTemplate.update(updateCurriRateQurey, updateCurriRateParams);

        String getAllChapterNumberInLectureQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and lectureIdx = ?;\n";

        String getCompleteChapterNumberInLectureQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE curriIdx = ? and lectureIdx = ? and chComplete = 'TRUE';";

        Object[] getInLectureParams = new Object[]{
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int chapterInLecture = this.jdbcTemplate.queryForObject(getAllChapterNumberInLectureQurey, int.class, getInLectureParams);
        int completeInLeture = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInLectureQurey, int.class, getInLectureParams)
        float lectureRate = completeInLeture / chapterInLecture * 100;

        String updataLectureRateQurey = "UPDATE Lecture_Rate SET progressRate = ? WHERE lectureIdx = ? and userIdx = ?;";

        Object[] updateLectureRateParams = new Object[]{
                lectureRate,
                patchChapterStatuReq.getLectureIdx(),
                userIdx
        };

        this.jdbcTemplate.update(updataLectureRateQurey, updateLectureRateParams);

        return result;
    }

    public GetThisCurriRes getThisCurri(GetThisCurriReq getThisCurriReq){
        String getLectureInCurriQurey = "";

        int getLectureInCurriParams = getThisCurriReq.getCurriIdx();

        String getThisCurriQurey = "";
    }
}