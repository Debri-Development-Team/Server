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
    public void getDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq, int userIdx) {

        // Curri 테이블에 데이터 저장
        String insertQuery = "INSERT\n" +
                "INTO Curriculum(curriName, curriAuthor, visibleStatus, langTag, ownerIdx)\n" +
                "VALUES (?, ?, ?, ?, ?);";

        Object[] insertCurriParameters = new Object[]{
                postCurriCreateReq.getCurriName(),
                postCurriCreateReq.getCurriAuthor(),
                postCurriCreateReq.getVisibleStatus(),
                postCurriCreateReq.getLangTag(),
                userIdx
        };

        this.jdbcTemplate.update(insertQuery, insertCurriParameters);

        // Ch_Lecture_Curri 테이블에 데이터 업뎃
            // 해당 lecture의 chapter list 추출
        String getChapterListQuery = "SELECT distinct ch.chIdx, ch.lectureIdx, ch.chOrder\n" +
                "FROM Chapter as ch\n" +
                "LEFT JOIN Lecture L on ch.lectureIdx = L.lectureIdx\n" +
                "WHERE ch.lectureIdx = ? and L.status = 'ACTIVE'";

        String insertLectureListQuery = "INSERT\n" +
                "INTO Ch_Lecture_Curri(chIdx, lectureIdx, curriIdx, lectureOrder, progressOrder)\n" +
                "VALUES (?, ?, ?, ?, ?);";

    }

    public List<GetCurriListRes> getList(int userIdx) {
        String getCurriListQuery = "SELECT distinct c.curriIdx, c.curriName, c.curriAuthor, c.visibleStatus, c.langTag, c.progressRate, c.status\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE u.userIdx = ? AND u.userIdx = c.ownerIdx AND c.status NOT IN ('DELETE');";

        String getCreatedAtQuery = "SELECT distinct UNIX_TIMESTAMP(c.createdAt)\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE c.curriIdx = ? AND c.status NOT IN ('DELETE');";

        return this.jdbcTemplate.query(getCurriListQuery,
                (rs, rowNum) -> new GetCurriListRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        this.jdbcTemplate.queryForObject(getCreatedAtQuery, int.class, rs.getInt("curriIdx"))
                ), userIdx);
    }

    public int deleteCurri(int curriIdx, int userIdx) {
        String deleteCurriQuery = "UPDATE Curriculum SET status = 'DELETE' WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] deleteCurriParams = new Object[]{
                curriIdx,
                userIdx
        };

        int result = this.jdbcTemplate.update(deleteCurriQuery, deleteCurriParams);

        return result;
    }

    public int checkCurriExist(int curriIdx) {
        String checkCurriQuery = "SELECT exists(SELECT curriIdx FROM Curriculum WHERE curriIdx = ?;";

        return this.jdbcTemplate.queryForObject(checkCurriQuery, int.class, curriIdx);
    }

    public int checkCurriScrap(int curriIdx) {
        String countCurriScrapQuery = "SELECT count(cs.scrapIdx)\n" +
                "FROM CurriScrap as cs\n" +
                "JOIN Curriculum as c\n" +
                "WHERE c.curriIdx = ? and c.curriIdx = cs.curriIdx and cs.status = 'ACTIVE';";

        int result = this.jdbcTemplate.queryForObject(countCurriScrapQuery, int.class, curriIdx);

        return result;
    }

    public void disconnectAllScrap(int curriIdx) {
        String disconnectAllScrap = "UPDATE CurriScrap\n" +
                "SET status = 'INACTIVE'\n" +
                "WHERE curriIdx = ? and status = 'ACTIVE';";

        this.jdbcTemplate.update(disconnectAllScrap, curriIdx);
    }

    public boolean checkChapterStatus(PatchChapterStatuReq patchChapterStatuReq) {
        String checkChapterStatusQuery = "SELECT exists(\n" +
                "    SELECT chIdx\n" +
                "    FROM Ch_Lecture_Curri\n" +
                "    WHERE chIdx = ? AND chComplete = 'FALSE'\n" +
                "    );";
        int checkChapterStatusParams = patchChapterStatuReq.getChIdx();
        int result = this.jdbcTemplate.queryForObject(checkChapterStatusQuery, int.class, checkChapterStatusParams);
        return result != 0;
    }


    public boolean checkChapterExist(PatchChapterStatuReq patchChapterStatuReq) {
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

        return result == 0;
    }

    public int completeChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx) {
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

        Object[] updateCurriRateParams = new Object[]{
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
        int completeInLeture = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInLectureQurey, int.class, getInLectureParams);
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

    public int completecancelChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx) {
        String completeCancelChapterQurey = "UPDATE Ch_Lecture_Curri SET chComplete = 'FALSE'\n" +
                "WHERE chIdx = ? and curriIdx = ? and lectureIdx = ?;";

        Object[] completeCancelChapterParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int result = this.jdbcTemplate.update(completeCancelChapterQurey, completeCancelChapterParams);

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

        Object[] updateCurriRateParams = new Object[]{
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
        int completeInLeture = this.jdbcTemplate.queryForObject(getCompleteChapterNumberInLectureQurey, int.class, getInLectureParams);
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

    public GetThisCurriRes getThisCurri(GetThisCurriReq getThisCurriReq, int userIdx) {

        int curriIdx = getThisCurriReq.getCurriIdx();

        String forDdayQurey = "SELECT distinct l.chNumber\n" +
                "FROM Lecture as l\n" +
                "LEFT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx\n" +
                "LEFT JOIN Lecture_Rate as lr on l.lectureIdx = l.lectureIdx\n" +
                "WHERE chlc.curriIdx = ?;";

        int chNum = this.jdbcTemplate.queryForObject(forDdayQurey, int.class, curriIdx);

        /*
        * 1. dDay 칼럼에 데이터 저장 -> 이건 생성으로 옮기기
        * 2. 시간이 지남에 따라 dDay 숫자도 업데이트하는 쿼리로 바꾸기
        * */

        float a = chNum / 3;
        int b = chNum / 3;
        int Dday;
        if(b < a ){
            Dday = (b + 1) * 7;
        } else {
            Dday = b * 7;
        }

        String getThisCurriQurey = "SELECT distinct curriIdx, curriName, visibleStatus, langTag, progressRate, status, completeAt\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and status = 'ACTIVE';";

        String getLectureListQurey = "SELECT distinct l.lectureIdx, l.lectureName, l.langTag, l.chNumber, lr.progressRate\n" +
                "FROM Lecture as l\n" +
                "LEFT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx\n" +
                "LEFT JOIN Lecture_Rate as lr on l.lectureIdx = lr.lectureIdx\n" +
                "WHERE chlc.curriIdx = ?;";

        String getChapterListQurey = "SELECT distinct chlc.chIdx, c.chName, l.chNumber,l.langTag, chlc.chComplete, chlc.progressOrder, l.lectureIdx\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Chapter as c on chlc.chIdx = c.chIdx\n" +
                "JOIN (SELECT langTag, chNumber, l.lectureIdx\n" +
                "      FROM Lecture as l\n" +
                "      JOIN Ch_Lecture_Curri as chlc\n" +
                "      WHERE chlc.lectureIdx = l.lectureIdx) as l\n" +
                "WHERE chlc.curriIdx = ? and l.lectureIdx = chlc.lectureIdx;";

        String getCompleteNumQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE chlc.curriIdx = ? and chlc.lectureIdx = ? and chlc.chComplete = 'TRUE'";

        String getCreatedAtQuery = "SELECT distinct UNIX_TIMESTAMP(c.createdAt)\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE c.curriIdx = ? AND c.status NOT IN ('DELETE');";

        Object[] getThisCurriParams = new Object[]{
                getThisCurriReq.getCurriIdx(),
                userIdx
        };

        return this.jdbcTemplate.queryForObject(getThisCurriQurey, (rs, rowNum)
                -> new GetThisCurriRes(
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("visibleStatus"),
                rs.getString("langTag"),
                rs.getFloat("progressRate"),
                rs.getString("status"),
                rs.getInt("completeAt"),
                rs.getInt(Dday),
                this.jdbcTemplate.queryForObject(getCreatedAtQuery, int.class, rs.getInt("curriIdx")),

                this.jdbcTemplate.query(getLectureListQurey, ((rs2, rowNum2)
                        -> new LectureListInCurriRes(
                        rs2.getInt("lectureIdx"),
                        rs2.getString("lectureName"),
                        rs2.getString("langTag"),
                        rs2.getInt("chNumber"),
                        rs2.getFloat("progressRate")
                )), curriIdx),

                this.jdbcTemplate.query(getChapterListQurey, ((rs1, rsNum1)
                        -> new ChapterListInCurriRes(
                        rs1.getInt("chIdx"),
                        rs1.getString("chName"),
                        rs1.getInt("chNumber"),
                        rs1.getString("langTag"),
                        rs1.getString("chComplete"),
                        rs1.getInt("progressOrder"),
                        rs1.getInt("lectureIdx"),
                        this.jdbcTemplate.queryForObject(getCompleteNumQurey, int.class,
                                new Object[]{
                                        curriIdx, rs1.getInt("lectureIdx")
                                })
                )), curriIdx)
        ), getThisCurriParams);
    }
}