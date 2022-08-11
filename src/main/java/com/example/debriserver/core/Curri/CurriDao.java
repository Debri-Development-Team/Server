package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class CurriDao {
    JdbcTemplate jdbcTemplate;

    long retryDate = System.currentTimeMillis();

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

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?, dDayAt = ? WHERE curriIdx = ? and ownerIdx = ?;";

        this.jdbcTemplate.update(insertQuery, insertCurriParameters);

        // lecture 정보 insert
        String insertLectureQuery = "INSERT\n" +
                "INTO Ch_Lecture_Curri(chIdx, lectureIdx, curriIdx, lectureOrder, progressOrder)\n" +
                "VALUES (?, ?, ?, ?, ?);";

        String getSumChNumQurey = "SELECT SUM(distinct l.chNumber)\n" +
                "FROM Lecture as l\n" +
                "LEFT JOIN Ch_Lecture_Curri as chlc on chlc.lectureIdx = l.lectureIdx\n" +
                "WHERE chlc.curriIdx = ?;";

        String getCurriIdxQurey = "SELECT MAX(curriIdx) FROM Curriculum where ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        int curriIdx = this.jdbcTemplate.queryForObject(getCurriIdxQurey, int.class, userIdx);

        int index = 0;

        LectureForCurriCreateReq lecture = postCurriCreateReq.getLectureList().get(index);

        String getChNumInLectureQurey = "SELECT chNumber FROM Lecture WHERE lectureIdx = ?;";

        String getChIdxQurey = "SELECT MIN(chIdx)\n" +
                "FROM Ch_Lecture as chl\n" +
                "WHERE NOT EXISTS(\n" +
                "    SELECT chIdx\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    WHERE curriIdx = ? and chlc.lectureIdx = ? and chl.chIdx = chlc.chIdx\n" +
                "    );";

        int lectureIdx = lecture.getLectureIdx();
        int chNumInLecture = this.jdbcTemplate.queryForObject(getChNumInLectureQurey, int.class, lectureIdx);

        for (int i = 1; i <= chNumInLecture; i++){

            if (i > chNumInLecture){
                index += 1;
                lecture = postCurriCreateReq.getLectureList().get(index);
                lectureIdx = lecture.getLectureIdx();
                chNumInLecture += this.jdbcTemplate.queryForObject(getChNumInLectureQurey, int.class, lectureIdx);
            }

            Object[] getChIdxParams = new Object[]{
                    curriIdx,
                    lectureIdx
            };

            int chIdx = this.jdbcTemplate.queryForObject(getChIdxQurey, int.class, getChIdxParams);

            Object[] insertLectureParams = new Object[] {
                    chIdx,
                    lectureIdx,
                    curriIdx,
                    lecture.getLectureOrder(),
                    i
            };

            this.jdbcTemplate.update(insertLectureQuery, insertLectureParams);
        }

        int chNum = this.jdbcTemplate.queryForObject(getSumChNumQurey, int.class, curriIdx);

        float a = (float) chNum / 3;
        int b = chNum / 3;
        int Dday;
        if(b < a ){
            Dday = (b + 1) * 7;
        } else {
            Dday = b * 7;
        }

        Timestamp origianl = new Timestamp(retryDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(origianl.getTime());
        cal.add(Calendar.DAY_OF_MONTH, Dday);
        Timestamp later = new Timestamp(cal.getTime().getTime());

        Object[] insertDdayParams = new Object[]{
                Dday,
                later.toString(),
                curriIdx,
                userIdx
        };

        this.jdbcTemplate.update(insertDdayQuery, insertDdayParams);

        PostCurriCreateRes postCurriCreateRes = new PostCurriCreateRes();

        postCurriCreateRes.setCurriIdx(curriIdx);

        return postCurriCreateRes;
    }

    public boolean curriModify(PostCurriModifyReq postCurriModifyReq, int userIdx) {
        //curri 이름, 활성*비활성, 공유*비공유
        String curriModifyQuery = "UPDATE Curriculum SET curriName = ?, visibleStatus = ?, status = ? WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] curriModifyParams = new Object[] {
                postCurriModifyReq.getCurriName(),
                postCurriModifyReq.getVisibleStatus(),
                postCurriModifyReq.getStatus(),
                postCurriModifyReq.getCurriIdx(),
                userIdx
        };

        String checkStatusQuery = "SELECT status\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] forStatusParams = new Object[] {
                postCurriModifyReq.getCurriIdx(),
                userIdx
        };

        String beforeStatus = this.jdbcTemplate.queryForObject(checkStatusQuery, String.class, forStatusParams);

        int result = this.jdbcTemplate.update(curriModifyQuery, curriModifyParams);

        String afterStatus = this.jdbcTemplate.queryForObject(checkStatusQuery, String.class, forStatusParams);

        String updateStatusChangedAtQuery = "UPDATE Curriculum SET statusChangedAt = NOW() WHERE curriIdx = ? and ownerIdx = ?;";

        if ( beforeStatus != afterStatus ) this.jdbcTemplate.update(updateStatusChangedAtQuery, forStatusParams);

        return result != 0;
    }

    public boolean insertLecture(PostInsertLectureReq postInsertLectureReq, int userIdx){
        // ch-l-c에 강의 연결
        String insertLectureQuery = "INSERT\n" +
                "INTO Ch_Lecture_Curri(chIdx, lectureIdx, curriIdx, lectureOrder, progressOrder)\n" +
                "VALUES (?, ?, ?, ?, ?);";

        // 현재 해당 커리큘럼의 max progressOrder 및 lectureOrder 가져오기
        String getLastProgressOrder = "SELECT MAX(progressOrder)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Curriculum C on C.curriIdx = chlc.curriIdx\n" +
                "WHERE chlc.curriIdx = ? and ownerIdx = ?";

        String getLastLectureOrder = "SELECT MAX(lectureOrder)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Curriculum C on C.curriIdx = chlc.curriIdx\n" +
                "WHERE chlc.curriIdx = ? and ownerIdx = ?";

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?\n" +
                "WHERE curriIdx = ?;";

        String getChIdxQurey = "SELECT MIN(chIdx)\n" +
                "FROM Ch_Lecture as chl\n" +
                "WHERE NOT EXISTS(\n" +
                "    SELECT chIdx\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    WHERE curriIdx = ? and chlc.lectureIdx = ? and chl.chIdx = chlc.chIdx\n" +
                "    );";

        String getChNumQurey = "SELECT l.chNumber\n" +
                "FROM Lecture as l\n" +
                "WHERE l.lectureIdx = ?;";

        Object[] getLastOrderParams = new Object[]{
                postInsertLectureReq.getCurriIdx(),
                userIdx
        };

        int lastLectureOrder = this.jdbcTemplate.queryForObject(getLastLectureOrder, int.class, getLastOrderParams);
        int lastProgressOrder = this.jdbcTemplate.queryForObject(getLastProgressOrder, int.class, getLastOrderParams);

        int chNum = this.jdbcTemplate.queryForObject(getChNumQurey, int.class, postInsertLectureReq.getLectureIdx());

        for (int i = 1; i <= chNum; i++){

            Object[] getChIdxParams = new Object[]{
                    postInsertLectureReq.getCurriIdx(),
                    postInsertLectureReq.getLectureIdx()
            };

            int chIdx = this.jdbcTemplate.queryForObject(getChIdxQurey, int.class, getChIdxParams);

            Object[] insertLectureParams = new Object[] {
                    chIdx,
                    postInsertLectureReq.getLectureIdx(),
                    postInsertLectureReq.getCurriIdx(),
                    lastLectureOrder + 1,
                    lastProgressOrder + 1
            };

            this.jdbcTemplate.update(insertLectureQuery, insertLectureParams);
        }

        int f = lastProgressOrder % 3;

        int Dday;

        if (f == 0){
            float a = (float) chNum / 3;
            int b = chNum / 3;
            if(b < a ){
                Dday = (b + 1) * 7;
            } else {
                Dday = b * 7;
            }
        } else {
            int F = chNum - (3 - f);
            float a = (float) F / 3;
            int b = F / 3;
            if(b < a ){
                Dday = (b + 1) * 7;
            } else {
                Dday = b * 7;
            }
        }

        int curriIdx = postInsertLectureReq.getCurriIdx();

        String getDdayQurey = "SELECT dDay\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] getDdayParams = new Object[]{
                postInsertLectureReq.getCurriIdx(),
                userIdx
        };

        int beforeDday = this.jdbcTemplate.queryForObject(getDdayQurey, int.class, getDdayParams);
        int afterDday = beforeDday + Dday;

        Object[] insertDdayParams = new Object[]{
                afterDday,
                curriIdx
        };

        int result = this.jdbcTemplate.update(insertDdayQuery, insertDdayParams);

        return result != 0;
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

        String getThisCurriQurey = "SELECT distinct curriIdx, curriName, visibleStatus, langTag, progressRate, status, completeAt\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

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

        String getStatusQurey = "SELECT status\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getDdayNowQurey = "SELECT (TIMESTAMPDIFF(DAY , now(), dDayAt) + 1) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getChangCreatedQurey = "SELECT (TIMESTAMPDIFF(DAY , statusChangedAt, createdAt) + 1) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        Object[] getThisCurriParams = new Object[]{
                getThisCurriReq.getCurriIdx(),
                userIdx
        };

        Object[] Status = new Object[]{
                this.jdbcTemplate.queryForObject(getStatusQurey, String.class, getThisCurriParams)
        };

        int dDay;

        if (Status[0] == "ACTIVE"){
            dDay = this.jdbcTemplate.queryForObject(getDdayNowQurey, int.class, getThisCurriParams);
        } else {
            dDay = this.jdbcTemplate.queryForObject(getChangCreatedQurey, int.class, getThisCurriParams);
        }

        return this.jdbcTemplate.queryForObject(getThisCurriQurey, (rs, rowNum)
                -> new GetThisCurriRes(
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("visibleStatus"),
                rs.getString("langTag"),
                rs.getFloat("progressRate"),
                rs.getString("status"),
                rs.getInt("completeAt"),
                dDay,
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