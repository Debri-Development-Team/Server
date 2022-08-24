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

        if(checkUnScrapedCurriExist2(curriIdx,userIdx)==true){
            rescrap(curriIdx,userIdx);

        }else {
            String insertQuery = "INSERT INTO CurriScrap (curriIdx, scrapUserIdx, status) VALUES ((SELECT curriIdx FROM Curriculum WHERE Curriculum.curriIdx=?) , ? , 'ACTIVE')";
            int insertParams = curriIdx;
            int insertParams2 = userIdx;

            this.jdbcTemplate.update(insertQuery, insertParams, insertParams2);
        }
        String getScrapedCurriQuery = "SELECT distinctrow A.* FROM Curriculum as A INNER JOIN CurriScrap as B ON A.curriIdx = B.curriIdx WHERE B.curriIdx= ? and B.status = 'ACTIVE' and A.status = 'ACTIVE' and B.scrapUserIdx = ?";

        return this.jdbcTemplate.queryForObject(getScrapedCurriQuery,
                (rs, rowNum) -> new PostCurriScrapRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getInt("ownerIdx")

                ),curriIdx ,userIdx);

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

        float result = (float) complete / total * 100;

        return  result;
    }

    public PostCurriCreateRes createCurri(PostCurriCreateReq postCurriCreateReq, int userIdx) {

        // Curri 테이블에 데이터 저장
        String insertQuery = "INSERT\n" +
                "INTO Curriculum(curriName, curriAuthor, visibleStatus, langTag, curriDesc, ownerIdx)\n" +
                "VALUES (?, ?, ?, ?, ?, ?);";

        Object[] insertCurriParameters = new Object[]{
                postCurriCreateReq.getCurriName(),
                postCurriCreateReq.getCurriAuthor(),
                postCurriCreateReq.getVisibleStatus(),
                postCurriCreateReq.getLangTag(),
                postCurriCreateReq.getCurriDesc(),
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

        if ( beforeStatus.equals("ACTIVE")) this.jdbcTemplate.update(updateStatusChangedAtQuery, forStatusParams);

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

        String insertLectureRateQuery = "INSERT INTO Lecture_Rate(lectureIdx, userIdx) VALUES (?, ?);";

        Object[] insertLectureRateParams = new Object[]{
                postInsertLectureReq.getLectureIdx(),
                userIdx
        };

        this.jdbcTemplate.update(insertLectureRateQuery, insertLectureRateParams);

        // 현재 해당 커리큘럼의 max progressOrder 및 lectureOrder 가져오기
        String getLastProgressOrder = "SELECT IFNULL(MAX(progressOrder),0)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Curriculum C on C.curriIdx = chlc.curriIdx\n" +
                "WHERE chlc.curriIdx = ? and ownerIdx = ?;";

        String getLastLectureOrder = "SELECT IFNULL(MAX(lectureOrder),0)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN Curriculum C on C.curriIdx = chlc.curriIdx\n" +
                "WHERE chlc.curriIdx = ? and ownerIdx = ?;";

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?, dDayAt = ? WHERE curriIdx = ? and ownerIdx = ?;";

        String getChIdxQurey = "SELECT MIN(ch.chIdx)\n" +
                "FROM Chapter as ch\n" +
                "JOIN Ch_Lecture_Curri as chlc\n" +
                "JOIN Lecture as l\n" +
                "WHERE NOT EXISTS(\n" +
                "    SELECT chlc.chIdx\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    WHERE chlc.curriIdx = ? and chlc.lectureIdx = ? and chlc.chIdx = ch.chIdx\n" +
                "    ) AND ch.lectureIdx = ? AND ch.lectureIdx = l.lectureIdx;\n";

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

//        System.out.println(lastLectureOrder);
//        System.out.println(lastProgressOrder);
//        System.out.println(chNum);

        for (int i = 1; i <= chNum; i++){

            Object[] getChIdxParams = new Object[]{
                    postInsertLectureReq.getCurriIdx(),
                    postInsertLectureReq.getLectureIdx(),
                    postInsertLectureReq.getLectureIdx()
            };

            int chIdx = this.jdbcTemplate.queryForObject(getChIdxQurey, int.class, getChIdxParams);

//            System.out.println(chIdx);

            Object[] insertLectureParams = new Object[] {
                    chIdx,
                    postInsertLectureReq.getLectureIdx(),
                    postInsertLectureReq.getCurriIdx(),
                    lastLectureOrder + 1,
                    lastProgressOrder + i
            };

//            System.out.println(Arrays.toString(insertLectureParams));

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

        System.out.println(Dday);

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

        System.out.println(Arrays.toString(insertDdayParams));

        int result = this.jdbcTemplate.update(insertDdayQuery, insertDdayParams);

        return result != 0;
    }

    public List<GetCurriListRes> getList(int userIdx) {
        String getCurriListQuery = "SELECT distinct c.curriIdx, c.curriName, c.curriAuthor, c.visibleStatus, c.langTag, c.progressRate, c.status, c.curriDesc\n" +
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
                        rs.getString("curriDesc"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        this.jdbcTemplate.queryForObject(getCreatedAtQuery, int.class, rs.getInt("curriIdx"))
                ), userIdx);
    }

    /**
     * 스크랩취소
     * @param scrapIdx
     * @return
     */
    public int scrapCancel(int scrapIdx){
        String deleteUserQuery = "UPDATE CurriScrap SET status='INACTIVE' WHERE scrapIdx = ? and status = 'ACTIVE'";
        int deleteUserParams = scrapIdx;
        return this.jdbcTemplate.update(deleteUserQuery,
                deleteUserParams);
    }

    /**
     * scrapIdx 여부
     * @param scrapIdx
     * @return
     */
    public boolean checkScrapIdxExist(int scrapIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE scrapIdx= ? and status ='ACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, scrapIdx);

        if(result == 0) return false;
        else return true;
    }

    /**
     * 스크랩한 커리큘럼 존재 유무
     * @param userIdx
     * @return
     */
    public boolean checkScrapedCurriExist(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE curriIdx =? and scrapUserIdx =? and status ='ACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx, userIdx);

        if(result == 0) return false;
        else return true;
    }

    public boolean checkUnScrapedCurriExist(int scrapIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE scrapIdx = ? and status = 'INACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, scrapIdx);
        if(result == 0) return false;
        else return true;
    }

    public boolean checkUnScrapedCurriExist2(int curriIdx, int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE curriIdx = ? and scrapUserIdx =? and status = 'INACTIVE'";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, curriIdx, userIdx);
        if(result == 0) return false;
        else return true;
    }

    public void rescrap(int curriIdx ,int userIdx) {
        String updateQuery = "UPDATE CurriScrap SET status = 'ACTIVE' WHERE curriIdx = ? and scrapUserIdx = ? and status = 'INACTIVE' ";

        this.jdbcTemplate.update(updateQuery, curriIdx, userIdx);
    }


    public int deleteCurri(int curriIdx, int userIdx) {
        String deleteCurriQuery = "UPDATE Curriculum SET status = 'DELETE' WHERE curriIdx = ? and ownerIdx = ?;";

        Object[] deleteCurriParams = new Object[]{
                curriIdx,
                userIdx
        };

        int result = this.jdbcTemplate.update(deleteCurriQuery, deleteCurriParams);

        System.out.println(result);

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

    public List<GetScrapListRes> getCurriScrapList(int userIdx) {
        String getQuery =
                "SELECT A.curriIdx, A.curriName, A.curriAuthor, A.langTag, A.progressRate \n" +
                        "FROM Curriculum as A JOIN CurriScrap as B ON A.curriIdx = B.curriIdx\n" +
                        "WHERE B.status = 'ACTIVE' and A.status = 'ACTIVE' and B.scrapUserIdx = ?;";

        return this.jdbcTemplate.query(getQuery,(rs, rowNum)
                -> new GetScrapListRes(
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("curriAuthor"),
                rs.getString("langTag"),
                rs.getFloat("progressRate")
        ), userIdx);
    }

    public boolean checkScrapExist(int userIdx) {
        String checkQuery = "SELECT COUNT(*) FROM CurriScrap WHERE scrapUserIdx = ? and status ='ACTIVE' ";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);

        if(result !=0)return true;
        else return  false;
    }

    public void disconnectAllScrap(int curriIdx) {
        String disconnectAllScrap = "UPDATE CurriScrap\n" +
                "SET status = 'INACTIVE'\n" +
                "WHERE curriIdx = ? and status = 'ACTIVE';";

        this.jdbcTemplate.update(disconnectAllScrap, curriIdx);
    }

    public boolean checkChapterStatus(PatchChapterStatuReq patchChapterStatuReq) {
        String checkChapterStatusQuery = "SELECT chComplete\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE chIdx = ? AND curriIdx = ? AND lectureIdx = ?;";

        Object[] checkChapterStatusParams = new Object[] {
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        String status = this.jdbcTemplate.queryForObject(checkChapterStatusQuery, String.class, checkChapterStatusParams);

        int result;

        if (status.equals("TRUE")){
            result = 1;
        } else {
            result = 0;
        }

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
                patchChapterStatuReq.getLectureIdx(),
                patchChapterStatuReq.getCurriIdx()
        };

        int result = this.jdbcTemplate.queryForObject(checkChapterExistQuery, int.class, checkChapterExistParams);

        System.out.println(result);

        return result > 0;
    }

    public int completeChapter(PatchChapterStatuReq patchChapterStatuReq, int userIdx) {
        String completeChapterQuery = "UPDATE Ch_Lecture_Curri SET chComplete = 'TRUE'\n" +
                "WHERE chIdx = ? and curriIdx = ? and lectureIdx = ?;";

        String completeLectureQuery = "UPDATE Lecture_Rate\n" +
                "SET compeleteStatus = 'TRUE'\n" +
                "WHERE lectureIdx = ? AND userIdx = ?;";

        Object[] completeChapterParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        Object[] completeLectureParams = new Object[]{
                patchChapterStatuReq.getLectureIdx(),
                userIdx
        };

        int result = this.jdbcTemplate.update(completeChapterQuery, completeChapterParams);

        float rate = rate(patchChapterStatuReq.getLectureIdx(), patchChapterStatuReq.getCurriIdx());

        if(rate >= 100) this.jdbcTemplate.update(completeLectureQuery, completeLectureParams);

        return result;
    }

    public int completecancelChapter(PatchChapterStatuReq patchChapterStatuReq) {
        String completeCancelChapterQurey = "UPDATE Ch_Lecture_Curri SET chComplete = 'FALSE'\n" +
                "WHERE chIdx = ? and curriIdx = ? and lectureIdx = ?;";

        Object[] completeCancelChapterParams = new Object[]{
                patchChapterStatuReq.getChIdx(),
                patchChapterStatuReq.getCurriIdx(),
                patchChapterStatuReq.getLectureIdx()
        };

        int result = this.jdbcTemplate.update(completeCancelChapterQurey, completeCancelChapterParams);

        return result;
    }

    public List<LectureListInCurriRes> lectureList(int curriIdx, int userIdx){
        LectureListInCurriRes listInCurriRes = null;

        String getLectureCountQuery = "SELECT COUNT(distinct lectureIdx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        String getLectureIdxQuery = "SELECT distinct lectureIdx\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        String getLectureListQurey = "SELECT distinct l.lectureName, l.langTag, l.chNumber, l.pricing, l.type\n" +
                "FROM Lecture as l\n" +
                "LEFT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx\n" +
                "WHERE chlc.curriIdx = ? and chlc.lectureIdx = ?;";

        String getLectureScrapStatusQuery = "SELECT IFNULL(status, 'FALSE')\n" +
                "FROM LectureScrap\n" +
                "WHERE userIdx = ? and lectureIdx = ?;";

        String getLectureLikeStatusQuery = "SELECT IFNULL(status, 'FALSE')\n" +
                "FROM lectureLike\n" +
                "WHERE userIdx = ? and lectureIdx = ?;";

        String getLectureCurriCountQuery = "SELECT IFNULL(COUNT(distinct lectureIdx, curriIdx),0) as lecturCount\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE lectureIdx = ?;";

        String getLecturLikeCountQuery = "SELECT IFNULL(COUNT(status), 0)\n" +
                "FROM lectureLike\n" +
                "WHERE lectureIdx = ?;";

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

                Object[] getStatusParams = new Object[]{
                        userIdx,
                        lectureIdx
                };

                listInCurriRes = this.jdbcTemplate.queryForObject(getLectureListQurey, ((rs2, rowNum2)
                        -> new LectureListInCurriRes(
                        lectureIdx,

                        rs2.getString("lectureName"),
                        rs2.getString("langTag"),
                        rs2.getInt("chNumber"),
                        rs2.getString("pricing"),
                        rs2.getString("type"),

                        rate(lectureIdx, curriIdx),

                        this.jdbcTemplate.queryForObject(getLectureCurriCountQuery, int.class, lectureIdx),

                        this.jdbcTemplate.queryForObject(getLectureScrapStatusQuery, String.class, getStatusParams),

                        this.jdbcTemplate.queryForObject(getLectureLikeStatusQuery, String.class, getStatusParams),

                        this.jdbcTemplate.queryForObject(getLecturLikeCountQuery, int.class, lectureIdx)

                )), getLectureParams);

                System.out.println(listInCurriRes);

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

        String getChapterListQurey = "SELECT distinct chlc.chIdx, chlc.lectureIdx, c.chName, l.chNumber,l.langTag, chlc.chComplete, chlc.progressOrder, l.lectureIdx\n" +
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

        Object[] getThisCurriParams = new Object[]{
                curriIdx,
                userIdx
        };

        System.out.println(c);

        if (count > 0) {
            int a = 3;
            if (count < 3) a = count;
            else if ((c+2) - count > 0) {
                a = (c+2) - count;
            }
            for (int i = 0; i < a; i++) {
                Object[] getChapterParams = new Object[]{
                        curriIdx,
                        c,
                        userIdx
                };

                chapterListInCurriRes = this.jdbcTemplate.queryForObject(getChapterListQurey, (rs, rowNum)
                        -> new ChapterListInCurriRes
                        (
                                rs.getInt("chIdx"),
                                rs.getInt("lectureIdx"),
                                curriIdx,
                                rs.getString("chName"),
                                rs.getInt("chNumber"),
                                rs.getString("langTag"),
                                rs.getString("chComplete"),
                                rs.getInt("progressOrder"),
                                this.jdbcTemplate.queryForObject(getCompleteChNumQurey, int.class, getThisCurriParams)
                        ), getChapterParams);

                System.out.println(chapterListInCurriRes);

                getChapterListResList.add(i, chapterListInCurriRes);

                c++;
            }
        }

        return getChapterListResList;
    }

    public GetThisCurriRes getThisCurri(int curriIdx) {

        String getCurriOwnerAuthorQuery = "SELECT curriAuthor\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ?;";

        String curriAuthor = this.jdbcTemplate.queryForObject(getCurriOwnerAuthorQuery, String.class, curriIdx);

        String getCurriOwnerIdxQuery = "SELECT ownerIdx\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ?;";

        String getCurriScrapCountQuery = "SELECT IFNULL(COUNT(scrapIdx), 0)\n" +
                "FROM CurriScrap\n" +
                "WHERE curriIdx = ?;";

        String getCurriScrapStatusQuery = "SELECT\n" +
                "    CASE\n" +
                "        WHEN COUNT(status) = 0 THEN 'INACTIVE'\n" +
                "        WHEN status = 'ACTIVE' THEN 'ACTIVE'\n" +
                "        ELSE 'INACTIVE'\n" +
                "    END\n" +
                "FROM CurriScrap\n" +
                "WHERE curriIdx = ? AND scrapUserIdx = ?;";

        int userIdx = this.jdbcTemplate.queryForObject(getCurriOwnerIdxQuery, int.class, curriIdx);

        String getThisCurriQurey = "SELECT distinct curriIdx, curriName, visibleStatus, langTag, progressRate, status, completeAt, curriAuthor, curriDesc\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getCreatedAtQuery = "SELECT distinct c.createdAt\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE c.curriIdx = ? AND c.status != 'DELETE';";

        String getStatusQurey = "SELECT IFNULL(status, '0') AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor = ? and status != 'DELETE';";

        String getDdayNowQurey = "SELECT (TIMESTAMPDIFF(DAY , now(), dDayAt) + 1) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor= ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getChangCreatedQurey = "SELECT IFNULL((TIMESTAMPDIFF(DAY , createdAt, statusChangedAt) + 1), 0) AS RESULT\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        String getDdayAtQuery = "SELECT IF(dDayAt = '0000-00-00 00:00:00', 0, 1)\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor = ?;";

        String updateProgressRateQuery = "UPDATE Curriculum SET progressRate = ? WHERE curriIdx = ? and curriAuthor = ?;";

        String getCompleteQuery = "SELECT COUNT(chlc.chIdx)\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "WHERE chlc.curriIdx = ? and chlc.chComplete = 'TRUE';";

        String getTotalQuery = "SELECT COUNT(chidx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE curriIdx = ?;";

        int complete = this.jdbcTemplate.queryForObject(getCompleteQuery, int.class, curriIdx);
        int total = this.jdbcTemplate.queryForObject(getTotalQuery, int.class, curriIdx);

        if(total != 0) {
            float progressRate = (float) complete / total * 100;

            Object[] updateProgressRatePramas = new Object[]{
                    progressRate,
                    curriIdx,
                    curriAuthor
            };

            this.jdbcTemplate.update(updateProgressRateQuery, updateProgressRatePramas);
        }

        Object[] getThisCurriParams = new Object[]{
                curriIdx,
                curriAuthor
        };

        Object[] getCurriScrapStatusParams = new Object[]{
                curriIdx,
                userIdx
        };

        String Status = this.jdbcTemplate.queryForObject(getStatusQurey, String.class, getThisCurriParams);

        int dDayAt = this.jdbcTemplate.queryForObject(getDdayAtQuery, int.class, getThisCurriParams);

        int dDay;
        if(dDayAt == 1) {
            if (Status.equals("ACTIVE")) {
                dDay = this.jdbcTemplate.queryForObject(getDdayNowQurey, int.class, getThisCurriParams);
            } else {
                dDay = this.jdbcTemplate.queryForObject(getChangCreatedQurey, int.class, getThisCurriParams);
            }
        } else {
            dDay = 0;
        }

        String getTotalDdayQurey = "SELECT dDay\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? and curriAuthor = ?;";

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

        System.out.println(totalDday);
        System.out.println(dDay);
        System.out.println(c);

        String getScrapIdx = "SELECT CASE WHEN COUNT(scrapIdx) = 0 THEN 0 ELSE scrapIdx END FROM CurriScrap WHERE curriIdx = ? AND scrapUserIdx = ?";


        return this.jdbcTemplate.queryForObject(getThisCurriQurey, (rs, rowNum)
                -> new GetThisCurriRes (
                rs.getInt("curriIdx"),
                rs.getString("curriName"),
                rs.getString("visibleStatus"),
                rs.getString("langTag"),
                rs.getFloat("progressRate"),
                rs.getString("status"),
                rs.getInt("completeAt"),
                rs.getString("curriAuthor"),
                rs.getString("curriDesc"),

                dDay,
                this.jdbcTemplate.queryForObject(getCreatedAtQuery, Timestamp.class, curriIdx),

                this.jdbcTemplate.queryForObject(getCurriScrapCountQuery, int.class, curriIdx),
                this.jdbcTemplate.queryForObject(getCurriScrapStatusQuery, String.class, getCurriScrapStatusParams),

                lectureList(curriIdx, userIdx),

                chapterList(curriIdx, c, userIdx),

                this.jdbcTemplate.queryForObject(getScrapIdx, int.class,curriIdx,userIdx)

        ), getThisCurriParams);
    }

    public CurriReviewRes createCurriReview(PostCurriReviewReq postCurriReviewReq, int authorIdx){
        String insertQuery = "INSERT INTO CurriReview(authorIdx, curriIdx, authorName, content) VALUES (?,?,?,?);";

        Object[] insertParams = new Object[]{
                authorIdx,
                postCurriReviewReq.getCurriIdx(),
                postCurriReviewReq.getAuthorName(),
                postCurriReviewReq.getContent()
        };

        this.jdbcTemplate.update(insertQuery, insertParams);

        int currIdx = postCurriReviewReq.getCurriIdx();
        String authorName = postCurriReviewReq.getAuthorName();
        String content = postCurriReviewReq.getContent();

        return new CurriReviewRes(currIdx, authorName, content);
    }

    public List<CurriReviewRes> getCurriReviewList(int curriIdx){
        String getQuery = "SELECT curriIdx, authorName, content\n" +
                "FROM CurriReview\n" +
                "WHERE curriIdx = ?;";

        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new CurriReviewRes(
                        rs.getInt("curriIdx"),
                        rs.getString("authorName"),
                        rs.getString("content")
                ), curriIdx);
    }

    public boolean curriReset(int curriIdx, int userIdx){
        String curriResetQurry = "UPDATE Curriculum\n" +
                "SET progressRate = 0, createdAt = NOW(), statusChangedAt = 0, dDayAt = DATE_ADD(NOW(), INTERVAL dDay DAY )\n" +
                "WHERE curriIdx = ? AND ownerIdx = ?;";

        String chapterResetQurry = "UPDATE Ch_Lecture_Curri\n" +
                "SET chComplete = REPLACE(chComplete, 'TRUE', 'FALSE')\n" +
                "WHERE curriIdx = ?;";

        Object[] curriResetParams = new Object[]{
                curriIdx,
                userIdx
        };

        int result = this.jdbcTemplate.update(curriResetQurry, curriResetParams);
        this.jdbcTemplate.update(chapterResetQurry, curriIdx);

        return result > 0;
    }


    /**
     * 스크랩 Top10 리스트 추출
     * @return
     */
    public List<GetScrapTopListRes> getScrapTopList(){

        String getCreatedAtQuery = "SELECT distinct UNIX_TIMESTAMP(c.createdAt)\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE c.curriIdx = ? AND c.status NOT IN ('DELETE');";


        String get = "SELECT a.curriIdx,COUNT(*) as count, row_number() over (order by Count(*) DESC)ranking, b.curriName, b.curriAuthor ,b.visibleStatus , b.langTag, b.progressRate ,b.status FROM CurriScrap as a LEFT JOIN Curriculum as b on a.curriIdx = b.curriIdx  WHERE a.status ='ACTIVE' group by curriIdx order by COUNT(*) DESC limit 10;";


        return this.jdbcTemplate.query(get,
                (rs, rowNum) -> new GetScrapTopListRes(
                        rs.getInt("curriIdx"),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        this.jdbcTemplate.queryForObject(getCreatedAtQuery, int.class, rs.getInt("curriIdx"))

                ));
    }

    public List<GetLatestListRes> getLatestList(){
        String getLatestListQuery =
                "SELECT curriIdx, curriName, curriAuthor, curriDesc, visibleStatus, langTag, progressRate, status, UNIX_TIMESTAMP(createdAt) \n" +
                "FROM Curriculum WHERE visibleStatus = 'ACTIVE'\n" +
                "ORDER BY createdAt DESC limit 5;";

        return this.jdbcTemplate.query(getLatestListQuery,
                (rs, rowNum) -> new GetLatestListRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("curriAuthor"),
                        rs.getString("curriDesc"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getInt("UNIX_TIMESTAMP(createdAt)")
                ));
    }

    public int curriCopy(PostCurriCopyReq postCurriCopyReq, int userIdx){

        // 커리 복사해오기
        String copyCurriQuery =
                "INSERT INTO Curriculum(curriAuthor, ownerIdx, curriName, visibleStatus, langTag, curriDesc, dDay, dDayAt)\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        String getTargetCurriInfo = "SELECT ? as curriAuthor, ? as ownerIdx, curriName, visibleStatus, langTag, curriDesc, dDay, DATE_ADD(NOW(), INTERVAL dDay DAY) as dDayAt FROM Curriculum WHERE curriIdx = ?;";

        Object[] copyCurriParams = new Object[]{
                postCurriCopyReq.getTargetOwnerNickName(),
                userIdx,
                postCurriCopyReq.getTargetCurriIdx()
        };

        Object[] targetCurriInfo = this.jdbcTemplate.queryForObject(getTargetCurriInfo,
                (rs, rowNum) -> new Object[]{
                        rs.getString("curriAuthor"),
                        rs.getInt("ownerIdx"),
                        rs.getString("curriName"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getString("curriDesc"),
                        rs.getInt("dDay"),
                        rs.getString("dDayAt")
                }, copyCurriParams);

        int result = this.jdbcTemplate.update(copyCurriQuery, targetCurriInfo);

        String getCurriIdxQurey = "SELECT MAX(curriIdx) FROM Curriculum where ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        int curriIdx = this.jdbcTemplate.queryForObject(getCurriIdxQurey, int.class, userIdx);


        // 챕터 복사해오기
        String copyChapterQuery = "INSERT INTO Ch_Lecture_Curri(chIdx, lectureIdx, curriIdx, chComplete, lectureOrder, progressOrder)\n" +
                "SELECT chIdx, lectureIdx, ? as curriIdx , 'FALSE' as chComplete, lectureOrder, progressOrder FROM Ch_Lecture_Curri WHERE curriIdx = ?;";

        Object[] copyChapterParams = new Object[]{
                curriIdx,
                postCurriCopyReq.getTargetCurriIdx()
        };

        int result2 = this.jdbcTemplate.update(copyChapterQuery, copyChapterParams);

        if(result == 0 || result2 == 0) curriIdx = -1;

        return curriIdx;
    }

}
