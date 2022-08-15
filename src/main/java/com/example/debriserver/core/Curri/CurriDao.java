package com.example.debriserver.core.Curri;

import com.example.debriserver.core.Curri.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class CurriDao {
    JdbcTemplate jdbcTemplate;

    long retryDate = System.currentTimeMillis();

    @Autowired
    public void getDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    /**
     * 커리큘럼 스크랩
     * @param curriIdx
     * @param userIdx
     * @return
     */
    public PostCurriScrapRes scrapCurri(int curriIdx, int userIdx) {

        String insertQuery = "INSERT INTO Curriculum (curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx) " +
                "SELECT curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx FROM Curriculum where curriIdx = ?";
        int insertParams = curriIdx;

        this.jdbcTemplate.update(insertQuery,insertParams);

        String updateQuery = "UPDATE Curriculum SET progressRate = 0, scraped = 'TRUE', ownerIdx = ? WHERE curriIdx = (SELECT LAST_INSERT_ID()) ";
        int updateParams = userIdx;

        this.jdbcTemplate.update(updateQuery,updateParams);

        String getScrapedCurriQuery = "SELECT curriIdx,curriName,curriAuthor,visibleStatus,langTag,progressRate,status,scraped,ownerIdx " +
                "FROM Curriculum WHERE curriIdx = (SELECT LAST_INSERT_ID()) ";


        return this.jdbcTemplate.queryForObject(getScrapedCurriQuery,
                (rs, rowNum) -> new PostCurriScrapRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getString("scraped"),
                        rs.getInt("ownerIdx")
                ));
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

        String getCurriIdxQurey = "SELECT MAX(curriIdx) FROM Curriculum where ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        int curriIdx = this.jdbcTemplate.queryForObject(getCurriIdxQurey, int.class, userIdx);

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

        String updateStatusChangedAtQuery = "UPDATE Curriculum SET statusChangedAt = NOW() WHERE curriIdx = ? and ownerIdx = ?;";

        if ( beforeStatus == "ACTIVE") this.jdbcTemplate.update(updateStatusChangedAtQuery, forStatusParams);

        return result != 0;
    }

    public boolean curriNameModify(PacthCurriNameModifyReq pacthCurriNameModifyReq, int userIdx) {

        String curriNameModifyQuery = "UPDATE Curriculum SET curriName = ? WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] curriNameModifyParams = new Object[]{
                pacthCurriNameModifyReq.getCurriName(),
                pacthCurriNameModifyReq.getCurriIdx(),
                userIdx
        };

        int result = this.jdbcTemplate.update(curriNameModifyQuery, curriNameModifyParams);

//        System.out.println("nameModify : " + result);

        return result != 0;
    }

    public boolean curriVisibleStatusModify(PacthCurriVisibleStatusModifyReq pacthCurriVisibleStatusModifyReq, int userIdx) {

        String curriVisibleStatusModifyQuery = "UPDATE Curriculum SET visibleStatus = ? WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] curriVisibleStatusModifyParams = new Object[]{
                pacthCurriVisibleStatusModifyReq.getVisibleStatus(),
                pacthCurriVisibleStatusModifyReq.getCurriIdx(),
                userIdx
        };

        int result = this.jdbcTemplate.update(curriVisibleStatusModifyQuery, curriVisibleStatusModifyParams);

//        System.out.println(result);

        return result != 0;
    }

    public boolean curriStatusModify(PacthCurriStatusModifyReq pacthCurriStatusModifyReq, int userIdx) {
        String curriStatusModifyQuery = "UPDATE Curriculum SET status = ? WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] curriStatusModifyParams = new Object[]{
                pacthCurriStatusModifyReq.getStatus(),
                pacthCurriStatusModifyReq.getCurriIdx(),
                userIdx
        };

        String checkStatusQuery = "SELECT status\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and status != 'DELETE';";

        Object[] forStatusParams = new Object[] {
                pacthCurriStatusModifyReq.getCurriIdx(),
                userIdx
        };

        String beforeStatus = this.jdbcTemplate.queryForObject(checkStatusQuery, String.class, forStatusParams);

        int result = this.jdbcTemplate.update(curriStatusModifyQuery, curriStatusModifyParams);

        String updateStatusChangedAtQuery = "UPDATE Curriculum SET statusChangedAt = NOW() WHERE curriIdx = ? and ownerIdx = ?;";

        if (Objects.equals(beforeStatus, "ACTIVE")) this.jdbcTemplate.update(updateStatusChangedAtQuery, forStatusParams);

        return result != 0;
    }

    public boolean checkCurriExist(int curriIdx, int userIdx) {

        String checkCurriExistQuery = "SELECT COUNT(curriIdx)\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and status != 'DELETE';";

        Object[] checkCurriExistParams = new Object[] {
                curriIdx,
                userIdx
        };

        int result = this.jdbcTemplate.queryForObject(checkCurriExistQuery, int.class, checkCurriExistParams);

        return result > 0;
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

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?, dDayAt = ? WHERE curriIdx = ? and ownerIdx = ?;";

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

        Timestamp origianl = new Timestamp(retryDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(origianl.getTime());
        cal.add(Calendar.DAY_OF_MONTH, afterDday);
        Timestamp later = new Timestamp(cal.getTime().getTime());

        Object[] insertDdayParams = new Object[]{
                afterDday,
                later,
                curriIdx,
                userIdx
        };

        String insertLectureRateQurey = "insert Lecture_Rate SET lectureIdx = ?, userIdx = ?;";

        String checkLectureRateQurey = "SELECT exists(\n" +
                "    SELECT lectureIdx\n" +
                "    FROM Lecture_Rate\n" +
                "    WHERE lectureIdx = ? and userIdx = ?\n" +
                "           );";

        Object[] insertLectureRateParams = new Object[] {
                postInsertLectureReq.getLectureIdx(),
                userIdx
        };

        int a = this.jdbcTemplate.queryForObject(checkLectureRateQurey, int.class, insertLectureRateParams);

        if (a == 0 ) {
            this.jdbcTemplate.update(insertLectureRateQurey, insertLectureRateParams);
        }
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

    public int scrapCancel(String userId){
        String deleteUserQuery = "UPDATE Curriculum SET status='DELETE' WHERE userId = ? and status = 'ACTIVE'";
        String deleteUserParams = userId;
        return this.jdbcTemplate.update(deleteUserQuery,
                deleteUserParams);
    }

    /**
     * 스크랩한 커리큘럼 존재 유무
     * @param userIdx
     * @return
     */
    public boolean checkScrapedCurriExist(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM Curriculum WHERE curriName = (SELECT curriName FROM Curriculum WHERE curriIdx =?) and curriAuthor = (SELECT curriAuthor FROM Curriculum WHERE curriIdx =?)and scraped = 'TRUE' and ownerIdx =? ";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx,curriIdx,userIdx);

        if(result == 0) return false;
        else return true;
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

    public float rate(int lectureIdx, int curriIdx){
        String getCompleteQuery = "SELECT COUNT(chlc.chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE chlc.lectureIdx = ? and chlc.curriIdx = ? and chlc.chComplete = 'TRUE';";

        String getTotalQuery = "SELECT COUNT(chidx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE lectureIdx = ? and curriIdx = ?;";

        Object[] getParams = new Object[]{
                lectureIdx,
                curriIdx
        };

        int complete = this.jdbcTemplate.queryForObject(getCompleteQuery, int.class, getParams);
        int total = this.jdbcTemplate.queryForObject(getTotalQuery, int.class, getParams);

        float result = complete / total * 100;

        return  result;
    }

    public List<LectureListInCurriRes> lectureList(int curriIdx){
        LectureListInCurriRes listInCurriRes = null;

        String getLectureCountQuery = "SELECT COUNT(distinct lectureIdx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        String getLectureIdxQuery = "SELECT distinct lectureIdx\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        String getLectureListQurey = "SELECT distinct l.lectureName, l.langTag, l.chNumber\n" +
                "FROM Lecture as l\n" +
                "LEFT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx\n" +
                "WHERE chlc.curriIdx = ? and chlc.lectureIdx = ?;";

        int count = this.jdbcTemplate.queryForObject(getLectureCountQuery, int.class, curriIdx);

        List<LectureIdxList> lectureIdxLists = this.jdbcTemplate.query(getLectureIdxQuery,
                (rs, rowNum) -> new LectureIdxList(
                        rs.getInt("lectureIdx")
                )
                ,curriIdx);

        List<LectureListInCurriRes> getLectureListResList = new ArrayList<>();

        if (count > 0){
            for (int i = 0; i < count; i++) {
                int lectureIdx = lectureIdxLists.get(i).getLectureIdx();

                Object[] getLectureParams = new Object[]{
                        curriIdx, lectureIdx
                };

                listInCurriRes = this.jdbcTemplate.queryForObject(getLectureListQurey, ((rs2, rowNum2)
                        -> new LectureListInCurriRes(
                        lectureIdx,
                        rs2.getString("lectureName"),
                        rs2.getString("langTag"),
                        rs2.getInt("chNumber"),
                        rate(lectureIdx, curriIdx)
                )), getLectureParams);

                System.out.println(lectureIdx + " : " + listInCurriRes);

                getLectureListResList.add(i, listInCurriRes);
            }
        }

        return getLectureListResList;
    }

    public List<ChapterListInCurriRes> chapterList(int curriIdx, int c, int userIdx){
        List<ChapterListInCurriRes> getChapterListResList = new ArrayList<>();
        ChapterListInCurriRes chapterListInCurriRes;

        String getCompleteChNumQurey = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE chlc.curriIdx = ? and chlc.lectureIdx = ? and chlc.chComplete = 'TRUE';";

        String getChapterListQurey = "SELECT distinct chlc.chIdx, c.chName, l.chNumber,l.langTag, chlc.chComplete, chlc.progressOrder, l.lectureIdx\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Chapter as c on chlc.chIdx = c.chIdx\n" +
                "JOIN (SELECT langTag, chNumber, l.lectureIdx\n" +
                "      FROM Lecture as l\n" +
                "      JOIN Ch_Lecture_Curri as chlc\n" +
                "      WHERE chlc.lectureIdx = l.lectureIdx) as l\n" +
                "LEFT JOIN Curriculum as C2 on chlc.curriIdx = C2.curriIdx\n"+
                "WHERE chlc.curriIdx = ? and l.lectureIdx = chlc.lectureIdx and chlc.progressOrder = ? and C2.ownerIdx = ?;";

        String chCount = "SELECT COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        int count = this.jdbcTemplate.queryForObject(chCount, int.class, curriIdx);

        System.out.println(count);

        Object[] getThisCurriParams = new Object[]{
                curriIdx,
                userIdx
        };

        if (count > 0) {
            int a = 3;
            if ((c+2) - count > 0) a = (c+2) - count;
            for (int i = 0; i < a; i++) {
                Object[] getChapterParams = new Object[]{
                        curriIdx,
                        c,
                        userIdx
                };

                System.out.println(c);

                chapterListInCurriRes = this.jdbcTemplate.queryForObject(getChapterListQurey, (rs, rowNum)
                        -> new ChapterListInCurriRes
                        (
                                rs.getInt("chIdx"),
                                rs.getString("chName"),
                                rs.getInt("chNumber"),
                                rs.getString("langTag"),
                                rs.getString("chComplete"),
                                rs.getInt("progressOrder"),
                                this.jdbcTemplate.queryForObject(getCompleteChNumQurey, int.class, getThisCurriParams)
                        ), getChapterParams);
                getChapterListResList.add(i, chapterListInCurriRes);

                Object[] chapter = new Object[]{
                        chapterListInCurriRes.getChIdx(),
                        chapterListInCurriRes.getChName(),
                        chapterListInCurriRes.getChName(),
                        chapterListInCurriRes.getLangTag(),
                        chapterListInCurriRes.getChComplete(),
                        chapterListInCurriRes.getProgressOrder()
                };

                System.out.println("chapter : " + Arrays.toString(chapter));

                c++;
            }
        }

        return getChapterListResList;
    }

    public GetThisCurriRes getThisCurri(int curriIdx, int userIdx) {

        String getThisCurriQurey = "SELECT distinct curriIdx, curriName, visibleStatus, langTag, progressRate, status, completeAt\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getCreatedAtQuery = "SELECT distinct c.createdAt\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE c.curriIdx = ? AND c.status != 'DELETE';";

        String getStatusQurey = "SELECT IFNULL(status, '0') AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and status != 'DELETE';";

        String getDdayNowQurey = "SELECT (TIMESTAMPDIFF(DAY , now(), dDayAt) + 1) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getChangCreatedQurey = "SELECT IFNULL((TIMESTAMPDIFF(DAY , createdAt, statusChangedAt) + 1), 0) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getDdayAtQuery = "SELECT IF(dDayAt = '0000-00-00 00:00:00', 0, 1)\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] getThisCurriParams = new Object[]{
                curriIdx,
                userIdx
        };

        String Status = this.jdbcTemplate.queryForObject(getStatusQurey, String.class, getThisCurriParams);

        System.out.println(Status);

        int dDayAt = this.jdbcTemplate.queryForObject(getDdayAtQuery, int.class, getThisCurriParams);

        System.out.println(dDayAt);

        int dDay;
        if(dDayAt == 1) {
            if (Status == "ACTIVE") {
                dDay = this.jdbcTemplate.queryForObject(getDdayNowQurey, int.class, getThisCurriParams);
            } else {
                dDay = this.jdbcTemplate.queryForObject(getChangCreatedQurey, int.class, getThisCurriParams);
            }
        } else {
            dDay = 0;
        }

        System.out.println(dDay);

        String getTotalDdayQurey = "SELECT dDay\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and ownerIdx = ?;";

        int totalDday = this.jdbcTemplate.queryForObject(getTotalDdayQurey, int.class,getThisCurriParams);
        int a = totalDday / 7;
        int b = dDay / 7;
        int c;

        if (a != 0) {
            if (a == b) {
                c = 1;
            } else {
                c = (a - (a - b)) * 3 + 1;
            }
        } else {
            c = 0;
        }

        return this.jdbcTemplate.queryForObject(getThisCurriQurey, (rs, rowNum)
                -> new GetThisCurriRes (
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("visibleStatus"),
                rs.getString("langTag"),
                rs.getFloat("progressRate"),
                rs.getString("status"),
                rs.getInt("completeAt"),
                dDay,
                this.jdbcTemplate.queryForObject(getCreatedAtQuery, Timestamp.class, rs.getInt("curriIdx")),

                lectureList(curriIdx),

                chapterList(curriIdx, c, userIdx)
        ), getThisCurriParams);
    }
}