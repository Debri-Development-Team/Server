package com.example.debriserver.core.Lecture;

import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Curri.Model.PostInsertLectureReq;
import com.example.debriserver.core.Lecture.Model.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
    public List<GetLectureListRes> getLectureList(int userIdx) {
        String getQuery = "SELECT lectureIdx, lectureName, chNumber, langTag, pricing, type FROM Lecture WHERE status = 'ACTIVE';";
        String scrapStatusQuery = "SELECT COUNT(status) FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'ACTIVE';";

        return this.jdbcTemplate.query(getQuery, (rs, rowNum) -> new GetLectureListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chNumber"),
                        rs.getString("langTag"),
                        rs.getString("pricing"),
                        rs.getString("type"),
                Objects.requireNonNull(this.jdbcTemplate.queryForObject(scrapStatusQuery, int.class, userIdx, rs.getInt("lectureIdx"))) != 0
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
    public List<GetLectureScrapListRes> getScrapLectureList(int userIdx) {
        String getQuery =
                "SELECT L.lectureIdx, lectureName, chNumber, langTag, pricing, type\n" +
                "FROM Lecture as L LEFT JOIN LectureScrap as LC ON L.lectureIdx = LC.lectureIdx\n" +
                "WHERE L.status = 'ACTIVE' and LC.status = 'ACTIVE' and LC.userIdx = ?;";
        String scrapCountQuery = "SELECT COUNT(*) FROM LectureScrap WHERE lectureIdx = ? and status = 'ACTIVE';";
        String usedCountQuery = "SELECT COUNT(*) FROM Ch_Lecture_Curri WHERE lectureIdx = ?;";
        String likeCountQuery = "SELECT COUNT(*) FROM lectureLike WHERE lectureIdx = ? and status = 'ACTIVE';";
        String checkLikeQuery = "SELECT EXISTS(SELECT * FROM lectureLike WHERE lectureIdx = ? and userIdx = ? and status = 'ACTIVE');";

        return this.jdbcTemplate.query(getQuery,(rs, rowNum)
                -> new GetLectureScrapListRes(
                    rs.getInt("lectureIdx"),
                    rs.getString("lectureName"),
                    rs.getInt("chNumber"),
                    rs.getString("langTag"),
                    rs.getString("pricing"),
                    rs.getString("type"),
                    true,
                    this.jdbcTemplate.queryForObject(scrapCountQuery, int.class, rs.getInt("lectureIdx")),
                    this.jdbcTemplate.queryForObject(usedCountQuery, int.class, rs.getInt("lectureIdx")),
                    this.jdbcTemplate.queryForObject(likeCountQuery, int.class, rs.getInt("lectureIdx")),
                    this.jdbcTemplate.queryForObject(checkLikeQuery, int.class, rs.getInt("lectureIdx"), userIdx) == 1
        ), userIdx);
    }
    /**
     * 강의 상세 내용 조회
     * */
    public GetLectureRes getLecture(int lectureIdx, int userIdx) {
        String getQuery = "SELECT lectureIdx, lectureName, lectureDesc, langTag, pricing, srcLink, type, chNumber, publisher FROM Lecture WHERE lectureIdx = ? and status = 'ACTIVE';";
        String getListQuery = "SELECT chIdx, lectureIdx, chName, chOrder FROM Chapter WHERE lectureIdx = ? and status = 'ACTIVE';";
        String usedCountQuery = "SELECT COUNT(*) FROM Ch_Lecture_Curri WHERE lectureIdx = ?;";
        String likeCountQuery = "SELECT COUNT(*) FROM lectureLike WHERE lectureIdx = ? and status = 'ACTIVE';";
        String checkLikeQuery = "SELECT EXISTS(SELECT * FROM lectureLike WHERE lectureIdx = ? and userIdx = ? and status = 'ACTIVE');";
        String scrapStatusQuery = "SELECT COUNT(status) FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'ACTIVE';";

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
                this.jdbcTemplate.queryForObject(usedCountQuery, int.class, rs.getInt("lectureIdx")),
                this.jdbcTemplate.queryForObject(likeCountQuery, int.class, rs.getInt("lectureIdx")),
                this.jdbcTemplate.queryForObject(checkLikeQuery, int.class, rs.getInt("lectureIdx"), userIdx) == 1,
                Objects.requireNonNull(this.jdbcTemplate.queryForObject(scrapStatusQuery, int.class, userIdx, rs.getInt("lectureIdx"))) != 0,
                rs.getString("publisher"),
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
    public List<GetLectureSearchListRes> searchLecture(String langTag, String typeTag, String pricing, String keyword, int userIdx) {
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

        String scrapStatusQuery = "SELECT COUNT(status) FROM LectureScrap WHERE userIdx = ? and lectureIdx = ? and status = 'ACTIVE';";
        String scrapCountQuery = "SELECT COUNT(*) FROM LectureScrap WHERE lectureIdx = ? and status = 'ACTIVE';";
        String usedCountQuery = "SELECT COUNT(*) FROM Ch_Lecture_Curri WHERE lectureIdx = ?;";
        String likeCountQuery = "SELECT COUNT(*) FROM lectureLike WHERE lectureIdx = ? and status = 'ACTIVE';";
        String checkLikeQuery = "SELECT EXISTS(SELECT * FROM lectureLike WHERE lectureIdx = ? and userIdx = ? and status = 'ACTIVE');";

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
                (rs, rowNum) -> new GetLectureSearchListRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("lectureName"),
                        rs.getInt("chNumber"),
                        rs.getString("langTag"),
                        rs.getString("pricing"),
                        rs.getString("type"),
                        Objects.requireNonNull(this.jdbcTemplate.queryForObject(scrapStatusQuery, int.class, userIdx, rs.getInt("lectureIdx"))) != 0,
                        this.jdbcTemplate.queryForObject(scrapCountQuery, int.class, rs.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(usedCountQuery, int.class, rs.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(likeCountQuery, int.class, rs.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(checkLikeQuery, int.class, rs.getInt("lectureIdx"), userIdx) == 1
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
    
    /**
     * 로드맵 리스트 조회
     * */
    public List<GetRoadmapListRes> getRoadmapList() {
        String getListQuery = "SELECT roadMapIdx,roadMapName, roadMapExp, authorName, requireDay,\n" +
                "       IF(roadCategory = 'Back', 'server', 'front')\n" +
                "FROM Roadmap;";

        return this.jdbcTemplate.query(getListQuery,
                (rs, rowNum) -> new GetRoadmapListRes(
                        rs.getInt("roadMapIdx"),
                        rs.getString("roadMapName"),
                        rs.getString("roadMapExp"),
                        rs.getString("authorName"),
                        rs.getString("IF(roadCategory = 'Back', 'server', 'front')")
                ));
    }

    /**
     * 로드맵 상세 조회
     * */
    public List<GetRoadmapRes> getRoadmapView(String mod, int userIdx) {
        int modNumber;

        if(mod.equalsIgnoreCase("server")) modNumber = 1;
        else modNumber = 2;

        String baseQuery = "SELECT roadMapIdx,roadMapName, roadMapExp, authorName, requireDay FROM Roadmap WHERE roadMapIdx = ?;";
        String childCurriListQuery = "SELECT roadMapIdx, childIdx, childOrder, curriName, lectureIdx, childDesc FROM Roadmap_Child WHERE roadmapIdx = ?;";


        return this.jdbcTemplate.query(baseQuery,
                (rs, rowNum) -> new GetRoadmapRes(
                        rs.getInt("roadMapIdx"),
                        rs.getString("roadMapName"),
                        rs.getString("roadMapExp"),
                        rs.getString("authorName"),
                        rs.getInt("requireDay"),
                        this.jdbcTemplate.query(childCurriListQuery,
                                (rs1, rowNum1) -> new GetRoadmapChildRes(
                                        rs1.getInt("roadMapIdx"),
                                        rs1.getInt("childIdx"),
                                        rs1.getInt("childOrder"),
                                        rs1.getString("childDesc"),
                                        rs1.getString("curriName"),
                                        getRoadmapChildLecture(rs1.getInt("lectureIdx"), rs1.getInt("childIdx"), userIdx)
                                ), rs.getInt("roadMapIdx"))
                ), modNumber);
    }

    public List<GetRoadmapChildLectureList> getRoadmapChildLecture(int lectureIdx, int childIdx, int userIdx){
        String childLectureListQuery = "SELECT lectureIdx, lectureName, chNumber, langTag, pricing, type FROM Lecture WHERE lectureIdx = ?;";
        String countLikeQuery = "SELECT COUNT(*) FROM lectureLike WHERE lectureIdx = ?;";
        String checkUserLikeQuery = "SELECT EXISTS(SELECT * FROM lectureLike WHERE lectureIdx = ? and userIdx = ?);";
        String countScrapQuery = "SELECT COUNT(*) FROM LectureScrap WHERE lectureIdx = ?;";
        String checkScrapQuery = "SELECT EXISTS(SELECT * FROM LectureScrap WHERE lectureIdx = ? and userIdx = ?);";
        String countUsedNumberQuery = "SELECT COUNT(*) FROM Ch_Lecture_Curri WHERE lectureIdx = ?;";

        return this.jdbcTemplate.query(childLectureListQuery,
                (rs2, rowNum2) -> new GetRoadmapChildLectureList(
                        childIdx,
                        rs2.getInt("lectureIdx"),
                        rs2.getString("lectureName"),
                        rs2.getInt("chNumber"),
                        rs2.getString("langTag"),
                        rs2.getString("pricing"),
                        rs2.getString("type"),
                        this.jdbcTemplate.queryForObject(checkScrapQuery, int.class, rs2.getInt("lectureIdx"), userIdx) == 1,
                        this.jdbcTemplate.queryForObject(countScrapQuery, int.class, rs2.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(countUsedNumberQuery, int.class, rs2.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(countLikeQuery, int.class, rs2.getInt("lectureIdx")),
                        this.jdbcTemplate.queryForObject(checkUserLikeQuery, int.class, rs2.getInt("lectureIdx"), userIdx) == 1
                ), lectureIdx);
    }
    /**
     * 존재하면 false 존재 안하면 true
     * */
    public boolean checkRoadmapExist(int roadmapIdx) {
        String checkQuery = "SELECT COUNT(*) FROM Curriculum WHere curriIdx = ?;";

        return 0 == this.jdbcTemplate.queryForObject(checkQuery, int.class, roadmapIdx);
    }


    public LectureReviewRes createLectureReview(int lectureIdx, int authorIdx, String authorName, String content) {
        String insertQuery = "INSERT INTO lectureReview(lectureIdx, authorIdx, authorName, content) VALUES (?,?,?,?);";
        Object[] insertParameters = new Object[]{
                lectureIdx, authorIdx, authorName, content
        };

        this.jdbcTemplate.update(insertQuery, insertParameters);

        return new LectureReviewRes(lectureIdx, authorName, content);
    }

    public List<LectureReviewRes> getLectureReviewList(int lectureIdx) {
        String getQuery = "SELECT lectureIdx, authorName, content FROM lectureReview WHERE lectureidx = ?;";

        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new LectureReviewRes(
                        rs.getInt("lectureIdx"),
                        rs.getString("authorName"),
                        rs.getString("content")
                ), lectureIdx);
    }

    public boolean lectureExist(int lectureIdx) {
        String checkQuery = "SELECT EXISTS(SELECT * FROM Lecture WHERE lectureIdx = ?);";

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, lectureIdx) == 0;
    }

    public LectureLikeRes createLectureLike(int lectureIdx, int userIdx) {
        String insertQuery = "INSERT INTO lectureLike(lectureIdx, userIdx) VALUES (?,?);";
        String updateQuery = "UPDATE lectureLike SET status = 'ACTIVE' WHERE lectureIdx = ? and userIdx = ?;";
        String checkQuery = "SELECT EXISTS((SELECT * FROM lectureLike WHERE lectureIdx = ? and userIdx = ?));";

        boolean result = this.jdbcTemplate.queryForObject(checkQuery, int.class, lectureIdx, userIdx) == 0;

        System.out.println(result);
        if(result) this.jdbcTemplate.update(insertQuery, lectureIdx, userIdx);
        else this.jdbcTemplate.update(updateQuery, lectureIdx, userIdx);

        return new LectureLikeRes(true);
    }

    public LectureLikeRes deleteLectureLike(int lectureIdx, int userIdx) {
        String updateQuery = "UPDATE lectureLike SET status = 'INACTIVE' WHERE lectureIdx = ? and userIdx = ?;";

        this.jdbcTemplate.update(updateQuery, lectureIdx, userIdx);

        return new LectureLikeRes(true);
    }

    public PostRoadmapCopyRes copyRoadmap(PostRoadmapCopyReq postRoadmapCopyReq, int userIdx) throws SQLException {
        // Curri 테이블에 데이터 저장
        String insertQuery = "INSERT\n" +
                "INTO Curriculum(curriName, curriAuthor, visibleStatus, langTag, curriDesc, ownerIdx)\n" +
                "VALUES (?, ?, ?, ?, ?, ?);";

        String getCurriIdxQurey = "SELECT MAX(curriIdx) FROM Curriculum where ownerIdx = ? and (status = 'ACTIVE' OR status = 'INACTIVE');";

        Object[] insertParams = new Object[] {
                postRoadmapCopyReq.getRoadmapName(),
                "Debri Team",
                "INACTIVE",
                postRoadmapCopyReq.getLangTag(),
                postRoadmapCopyReq.getRoadmapExplain(),
                userIdx
        };

        this.jdbcTemplate.update(insertQuery, insertParams);

        int curriIdx = this.jdbcTemplate.queryForObject(getCurriIdxQurey, int.class, userIdx);

        String getLectuerListQuery = "select lectureIdx from Roadmap_Child where roadmapIdx = ?;";

        List<LectuerIndex> list = this.jdbcTemplate.query(getLectuerListQuery,
                (rs, rowNum) -> new LectuerIndex(
                        rs.getInt("lectureIdx")
                ), postRoadmapCopyReq.getRoadmapIdx());

        for(LectuerIndex lecture: list){
            boolean result = insertLecture(curriIdx, userIdx, lecture.getLectuerIdx());
            System.out.println(result);
        }

        return new PostRoadmapCopyRes(curriIdx);
    }

    public boolean insertLecture(int curriIdx, int userIdx, int lectureIdx){
        long retryDate = System.currentTimeMillis();

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
                curriIdx,
                curriIdx,
                lectureIdx
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
                lectureIdx,
                curriIdx,
                curriIdx,
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
                curriIdx,
                userIdx
        };

        String insertDdayQuery = "UPDATE Curriculum SET dDay = ?, dDayAt = ? WHERE curriIdx = ? and ownerIdx = ?;";
        int result = this.jdbcTemplate.update(insertDdayQuery, insertDdayParams);

        return result != 0;
    }
}
