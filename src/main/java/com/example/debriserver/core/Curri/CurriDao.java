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
        // 해당 강의 자료의 정보를 찾아 커리큘럼에 추가하는 쿼리
        String insertLectureQuery = "INSERT INTO Ch_Lecture_Curri(chIdx, lectureIdx, curriIdx, lectureOrder, progressOrder)\n" +
                "SELECT\n" +
                "    chIdx, lectureIdx, c.curriIdx,\n" +
                "    mo.ml + 1 as ml,\n" +
                "    ROW_NUMBER() over(order by chIdx) + mo.mp as num\n" +
                "FROM Chapter as chl\n" +
                "JOIN (\n" +
                "    SELECT IFNULL(MAX(progressOrder), 0) as mp,\n" +
                "           IFNULL(MAX(lectureOrder),0) as ml\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    WHERE chlc.curriIdx = ?\n" +
                ") mo\n" +
                "JOIN (\n" +
                "    SELECT curriIdx\n" +
                "    FROM Curriculum\n" +
                "    WHERE curriIdx = ?\n" +
                ") c\n" +
                "WHERE chl.lectureIdx = ?;\n";

        Object[] insertLectureParameters = new Object[] {
                postInsertLectureReq.getCurriIdx(),
                postInsertLectureReq.getCurriIdx(),
                postInsertLectureReq.getLectureIdx()
        };

        this.jdbcTemplate.update(insertLectureQuery, insertLectureParameters);

        String getDdayQurey = "SELECT\n" +
                "    IF(TRUNCATE(chNumber % 3, 0) = 0, TRUNCATE(chNumber / 3, 0) * 7,\n" +
                "       (TRUNCATE(chNumber / 3, 0) + 1) * 7) as afDay\n" +
                "FROM Curriculum as c\n" +
                "LEFT JOIN(\n" +
                "    SELECT distinct chNumber, l.lectureIdx, chlc.curriIdx\n" +
                "    FROM Lecture as l\n" +
                "    LEFT JOIN Ch_Lecture_Curri as chlc on l.lectureIdx = chlc.lectureIdx\n" +
                "    WHERE l.lectureIdx = ? AND chlc.curriIdx = ?\n" +
                ") l on l.curriIdx = c.curriIdx\n" +
                "WHERE c.curriIdx = ? AND ownerIdx = ?;";

        Object[] getDdayParams = new Object[]{
                postInsertLectureReq.getLectureIdx(),
                postInsertLectureReq.getCurriIdx(),
                postInsertLectureReq.getCurriIdx(),
                userIdx
        };

        int afterDday = this.jdbcTemplate.queryForObject(getDdayQurey, int.class, getDdayParams);

        Timestamp origianl = new Timestamp(retryDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(origianl.getTime());
        cal.add(Calendar.DAY_OF_MONTH, afterDday);
        Timestamp later = new Timestamp(cal.getTime().getTime());

        Object[] insertDdayParams = new Object[]{
                afterDday,
                later,
                postInsertLectureReq.getCurriIdx(),
                userIdx
        };

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?, dDayAt = ? WHERE curriIdx = ? and ownerIdx = ?;";
        int result = this.jdbcTemplate.update(insertDdayQuery, insertDdayParams);

        return result != 0;
    }

    public List<GetCurriListRes> getList(int userIdx) {
        String getCurriListQuery = "SELECT distinct c.curriIdx, c.curriName, c.curriAuthor, c.visibleStatus, c.langTag, c.progressRate, c.status, c.curriDesc, UNIX_TIMESTAMP(c.createdAt) as createAt\n" +
                "FROM Curriculum as c\n" +
                "JOIN User as u\n" +
                "WHERE u.userIdx = ? AND u.userIdx = c.ownerIdx AND c.status NOT IN ('DELETE');";

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
                        rs.getInt("createAt")
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
        String checkChapterExistQuery = "SELECT \n" +
                "    COUNT(chIdx)\n" +
                "FROM Ch_Lecture_Curri\n" +
                "WHERE chIdx = ? AND lectureIdx = ? AND curriIdx = ?";

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

    public GetThisCurriRes getThisCurri(int curriIdx, int userIdx) {
        // check status
        String checkStatusQuery = "SELECT COUNT(curriIdx)\n" +
                "FROM Curriculum\n" +
                "WHERE curriIdx = ? AND status = 'ACTIVE';";

        int check = this.jdbcTemplate.queryForObject(checkStatusQuery, int.class, curriIdx);

        // D - day update
        if (check > 0) {
            String upDateDayQuery = "UPDATE Curriculum as C, \n" +
                    "    ( SELECT DATEDIFF(dDayAt, NOW()) as newDay\n" +
                    "    FROM Curriculum\n" +
                    "    WHERE curriIdx = " + curriIdx + " ) as C2\n" +
                    "SET C.dDay = C2.newDay\n" +
                    "WHERE curriIdx = ?;";

            this.jdbcTemplate.update(upDateDayQuery, curriIdx);
        }

        String getCurriQuery = "SELECT\n" +
                "    c.curriIdx, curriName, visibleStatus, langTag,\n" +
                "    progressRate, c.status, UNIX_TIMESTAMP(completeAt) as completeAt, curriAuthor,\n" +
                "    dDay, createdAt, curriDesc, cs.cntScrap, cs.scrapIdx,\n" +
                "    cs.scrapStatus, chlc.cntCh,\n" +
                "    IF((cntCh - dDay) < 7, 0,\n" +
                "        IF(dDay < 1, (TRUNCATE(cntCh / 7, 0) - 1) * 3,\n" +
                "        (TRUNCATE((cntCh - dDay) / 7, 0) - 1) * 3)) as cusor,\n" +
                "    IFNULL(cs2.cntSc, 0) as cntSc\n" +
                "FROM Curriculum as c\n" +
                "JOIN (\n" +
                "    SELECT\n" +
                "        COUNT(cs.curriIdx) as cntScrap,\n" +
                "        cs2.scrapIdx,\n" +
                "        cs2.scrapStatus\n" +
                "    FROM CurriScrap as cs\n" +
                "JOIN (\n" +
                "        SELECT\n" +
                "            IFNULL(scrapIdx, -1) as scrapIdx,\n" +
                "            IFNULL(status, 'INACTIVE') as scrapStatus\n" +
                "        FROM CurriScrap as cs2\n" +
                "        WHERE curriIdx = " + curriIdx + " AND scrapUserIdx = " + userIdx + "\n" +
                "    ) cs2\n" +
                "    WHERE cs.status = 'ACTIVE' AND cs.curriIdx = " + curriIdx + "\n" +
                ") cs\n" +
                "JOIN (\n" +
                "    SELECT IF(TRUNCATE(COUNT(chIdx) % 3, 0) = 0, TRUNCATE(COUNT(chIdx) / 3, 0) * 7,\n" +
                "              (TRUNCATE(COUNT(chIdx) / 3, 0) + 1) * 7) as cntCh\n" +
                "    FROM Ch_Lecture_Curri\n" +
                "    WHERE curriIdx = " + curriIdx + "\n" +
                ") chlc\n" +
                "LEFT JOIN (\n" +
                "    SELECT curriIdx, COUNT(scrapIdx) as cntSc\n" +
                "    FROM CurriScrap\n" +
                "    WHERE status = 'ACTIVE'\n" +
                "    GROUP BY curriIdx\n" +
                ") cs2 on c.curriIdx = cs2.curriIdx\n" +
                "WHERE c.curriIdx = ? AND c.status != 'DELETE';";

        String getLectureQuery = "SELECT\n" +
                "    l.lectureIdx, l.lectureName, l.langTag, l.chNumber, l.pricing,\n" +
                "    l.type, COUNT(chlc2.curriIdx) as usedCnt, chlc.cpCnt,\n" +
                "    (cpCnt / l.chNumber) * 100 as progressRate,\n" +
                "    chlc.scrapStatus, chlc.likeStatus,\n" +
                "    IFNULL(llc, 0) as llc\n" +
                "FROM Lecture as l\n" +
                "JOIN (\n" +
                "    SELECT chlc.lectureIdx, COUNT(chI.chIdx) as cpCnt,\n" +
                "           IFNULL(lScrap.status, 'INACTIVE') as scrapStatus,\n" +
                "           IFNULL(lLike.status, 'INACTIVE') as likeStatus,\n" +
                "           llc\n" +
                "    FROM Ch_Lecture_Curri as chlc\n" +
                "    LEFT JOIN (\n" +
                "        SELECT chI.lectureIdx, chI.chIdx\n" +
                "        FROM Ch_Lecture_Curri as chI\n" +
                "        WHERE chI.curriIdx = " + curriIdx + " AND chComplete = 'TRUE'\n" +
                "    ) chI on chI.lectureIdx = chlc.lectureIdx\n" +
                "    LEFT JOIN (\n" +
                "        SELECT status, lectureIdx\n" +
                "        FROM LectureScrap\n" +
                "        WHERE userIdx = " + userIdx + "\n" +
                "    ) lScrap on lScrap.lectureIdx = chlc.lectureIdx\n" +
                "    LEFT JOIN (\n" +
                "        SELECT status, lectureIdx\n" +
                "        FROM lectureLike\n" +
                "        WHERE userIdx = " + userIdx + "\n" +
                "    ) lLike on lLike.lectureIdx = chlc.lectureIdx\n" +
                "    LEFT JOIN (\n" +
                "        SELECT COUNT(status) as llc, lectureIdx\n" +
                "        FROM lectureLike\n" +
                "        WHERE status = 'ACTIVE'\n" +
                "        GROUP BY lectureIdx\n" +
                "    ) lcLike on lcLike.lectureIdx = chlc.lectureIdx\n" +
                "    WHERE chlc.curriIdx = " + curriIdx + "\n" +
                "    GROUP BY lectureIdx\n" +
                ") chlc\n" +
                "LEFT JOIN (\n" +
                "    SELECT curriIdx, lectureIdx\n" +
                "    FROM Ch_Lecture_Curri\n" +
                "    GROUP BY curriIdx\n" +
                ") chlc2 on chlc2.lectureIdx = chlc.lectureIdx\n" +
                "WHERE status = 'ACTIVE' AND l.lectureIdx = chlc.lectureIdx\n" +
                "GROUP BY l.lectureIdx;";

        String getChapterQuery = "SELECT\n" +
                "    chlc.chIdx, chlc.lectureIdx, chlc.curriIdx, chlc.progressOrder, chlc.chComplete,\n" +
                "    c.chName, l.chNumber, IFNULL(chlc2.chCnt, 0) as chCnt, l.langTag\n" +
                "FROM Ch_Lecture_Curri as chlc\n" +
                "LEFT JOIN (\n" +
                "    SELECT chNumber, lectureIdx, langTag\n" +
                "    FROM Lecture\n" +
                ") l on chlc.lectureIdx = l.lectureIdx\n" +
                "LEFT JOIN (\n" +
                "    SELECT chIdx, chName\n" +
                "    FROM Chapter\n" +
                "    GROUP BY chIdx\n" +
                ") c on chlc.chIdx = c.chIdx\n" +
                "LEFT JOIN (\n" +
                "    SELECT curriIdx, lectureIdx, COUNT(chComplete) as chCnt\n" +
                "    FROM Ch_Lecture_Curri as  chlc2\n" +
                "    WHERE chComplete = 'TRUE'\n" +
                "    GROUP BY lectureIdx\n" +
                ") chlc2 on chlc.lectureIdx = chlc2.lectureIdx AND chlc.lectureIdx = chlc2.lectureIdx\n" +
                "WHERE chlc.curriIdx = " + curriIdx + "\n" +
                "LIMIT ?, 3;";

        return this.jdbcTemplate.queryForObject(getCurriQuery,
                (rs, rowNum) -> new GetThisCurriRes(
                        rs.getInt("curriIdx"),
                        rs.getString("curriName"),
                        rs.getString("visibleStatus"),
                        rs.getString("langTag"),
                        rs.getFloat("progressRate"),
                        rs.getString("status"),
                        rs.getInt("completeAt"),
                        rs.getString("curriAuthor"),
                        rs.getString("curriDesc"),
                        rs.getInt("dDay"),
                        rs.getInt("cntCh"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("cntScrap"),
                        rs.getString("scrapStatus"),
                        rs.getInt("scrapIdx"),
                        rs.getInt("cntSc"),

                        this.jdbcTemplate.query(getLectureQuery,
                                (rs2, rowNum2) -> new LectureListInCurriRes(
                                        rs2.getInt("lectureIdx"),
                                        rs2.getString("lectureName"),
                                        rs2.getString("langTag"),
                                        rs2.getInt("chNumber"),
                                        rs2.getString("pricing"),
                                        rs2.getString("type"),
                                        rs2.getFloat("progressRate"),
                                        rs2.getInt("usedCnt"),
                                        rs2.getString("scrapStatus"),
                                        rs2.getString("likeStatus"),
                                        rs2.getInt("llc")
                                )),

                        this.jdbcTemplate.query(getChapterQuery,
                                (rs3, rowNum3) -> new ChapterListInCurriRes(
                                        rs3.getInt("chIdx"),
                                        rs3.getInt("lectureIdx"),
                                        rs3.getInt("curriIdx"),
                                        rs3.getString("chName"),
                                        rs3.getInt("chNumber"),
                                        rs3.getString("langTag"),
                                        rs3.getString("chComplete"),
                                        rs3.getInt("progressOrder"),
                                        rs3.getInt("chCnt")
                                ), rs.getInt("cusor"))
                ), curriIdx);
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

    public GetCurriReviewPageRes getCurriReviewList(int curriIdx, int pageNum){
        String getQuery = "SELECT curriReviewIdx, curriIdx, authorName, content FROM CurriReview WHERE curriIdx = ? order by curriReviewIdx LIMIT ?, 12;";
        String countQuery = "SELECT COUNT(curriReviewIdx) FROM CurriReview WHERE curriIdx = ?;";

        List<CurriReviewRes> reviewList = this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new CurriReviewRes(
                        rs.getInt("curriIdx"),
                        rs.getString("authorName"),
                        rs.getString("content")
                ), curriIdx, pageNum - 1);

        int count = this.jdbcTemplate.queryForObject(countQuery, int.class, curriIdx);

        return new GetCurriReviewPageRes(reviewList, count);
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
