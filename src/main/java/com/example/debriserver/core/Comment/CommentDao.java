package com.example.debriserver.core.Comment;

import com.example.debriserver.core.Comment.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void getDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostReplyOnPostRes createReplyOnPost(PostReplyOnPostReq postReplyOnPostReq){
        //먼저 유저 인덱스, 내용, 게시글 번호를 저장
        String insertQuery = "INSERT\n" +
                "INTO Comment(userIdx, postIdx, commentContent, authorName)\n" +
                "VALUES (?, ?, ?, ?);";

        //방금 삽입한 인덱스를 가져온다
        String preInsertedIdxQuery= "SELECT MAX(commentIdx) FROM Comment;";

        //그 인덱스를 그룹번호로 클래스 번호로 0을, 순서로 0을 삽입
        String insertPostProcessQuery =
                "UPDATE Comment\n" +
                "SET groupNum = ?, class = ?, commentOrder = ?\n" +
                "WHERE commentIdx = ?;";

        //댓글 정보를 가져온다
        String getInsertedCommentQuery =
                "SELECT commentContent, userIdx, commentIdx, class, groupNum, commentOrder, authorName\n" +
                "FROM Comment\n" +
                "WHERE commentIdx = ?;";

        Object[] insertCommentParameters = new Object[]
                {
                        postReplyOnPostReq.getUserIdx(),
                        postReplyOnPostReq.getPostIdx(),
                        postReplyOnPostReq.getContent(),
                        postReplyOnPostReq.getAuthorName()
                };

        this.jdbcTemplate.update(insertQuery, insertCommentParameters);

        int insertedCommentIdx = this.jdbcTemplate.queryForObject(
                preInsertedIdxQuery,
                int.class);

        Object[] insertPostProcessParameters = new Object[]
                {
                        insertedCommentIdx,
                        0,
                        0,
                        insertedCommentIdx
                };

        this.jdbcTemplate.update(insertPostProcessQuery, insertPostProcessParameters);

        return this.jdbcTemplate.queryForObject
                (
                        getInsertedCommentQuery,
                        (rs, rowNum) ->
                        {
                            PostReplyOnPostRes postReplyOnPostRes = new PostReplyOnPostRes();
                            postReplyOnPostRes.setCommentContent(rs.getString("commentContent"));
                            postReplyOnPostRes.setAuthorIdx(rs.getInt("userIdx"));
                            postReplyOnPostRes.setCommentIdx(rs.getInt("commentIdx"));
                            postReplyOnPostRes.setCommentLevel(rs.getInt("class"));
                            postReplyOnPostRes.setCommentGroup(rs.getInt("groupNum"));
                            postReplyOnPostRes.setCommentOrder(rs.getInt("commentOrder"));
                            postReplyOnPostRes.setAuthorName(rs.getString("authorName"));
                            return postReplyOnPostRes;
                        }
                        , insertedCommentIdx
                );
    }

    public PostReplyOnReplyRes createReplyOnReply(PostReplyOnReplyReq postReplyOnReplyReq){
        PostReplyOnReplyRes postReplyOnReplyRes = new PostReplyOnReplyRes();
        //저장 하려는 대댓글이 첫번쨰일까?
        String countExistReplyOnReplyQuery = "SELECT COUNT(commentIdx) FROM Comment\n" +
                "WHERE groupNum = ? and groupNum != Comment.commentIdx;";
        int countExistPrarameter = postReplyOnReplyReq.getRootCommentIdx();

        int ExistCount = this.jdbcTemplate.queryForObject
                (
                        countExistReplyOnReplyQuery,
                        int.class,
                        countExistPrarameter
                );

        //대댓글 저장
        String storeCommentQuery = "INSERT\n" +
                "INTO Comment(userIdx, postIdx, class,commentOrder, groupNum, commentContent, authorName)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        Object[] storeCommentParameters = new Object[]
                {
                        postReplyOnReplyReq.getUserIdx(),
                        postReplyOnReplyReq.getPostIdx(),
                        1,
                        ExistCount + 1,
                        postReplyOnReplyReq.getRootCommentIdx(),
                        postReplyOnReplyReq.getContent(),
                        postReplyOnReplyReq.getAuthorName()
                };

        this.jdbcTemplate.update(storeCommentQuery, storeCommentParameters);

        String insertedCommentIdx = "SELECT MAX(commentIdx) FROM Comment;";

        int insertedIdx = this.jdbcTemplate.queryForObject(
                insertedCommentIdx,
                int.class
        );

        int[] others;

        String selectOthersQuery = "SELECT groupNum, commentOrder, class\n" +
                "FROM Comment\n" +
                "WHERE commentIdx = ?;";
        others = this.jdbcTemplate.queryForObject
                (
                        selectOthersQuery,
                        (rs, rowNum) -> {
                            int[] temp = new int[3];
                            temp[0] = rs.getInt("groupNum");
                            temp[1] = rs.getInt("commentOrder");
                            temp[2] = rs.getInt("class");
                            return temp;
                        },
                        insertedIdx
                );

        postReplyOnReplyRes.setAuthorIdx(postReplyOnReplyReq.getUserIdx());
        postReplyOnReplyRes.setAuthorName(postReplyOnReplyReq.getAuthorName());
        postReplyOnReplyRes.setCommentContent(postReplyOnReplyReq.getContent());
        postReplyOnReplyRes.setPostIdx(postReplyOnReplyReq.getPostIdx());
        postReplyOnReplyRes.setCommentIdx(insertedIdx);
        postReplyOnReplyRes.setCommentLevel(others[2]);
        postReplyOnReplyRes.setCommentGroup(others[0]);
        postReplyOnReplyRes.setCommentOrder(others[1]);


        return postReplyOnReplyRes;
    }

    public PatchCommentRes deleteComment(int commentIdx){
        PatchCommentRes patchCommentRes;

        String deleteQuery = "UPDATE Comment\n" +
                "SET status = 'DELETE'\n" +
                "WHERE commentIdx = ? and status = 'ACTIVE' or status = 'INACTIVE';";
        String deleteTailQuery = "UPDATE Comment SET status ='DELETE' WHERE groupNum = ?;";
        String getDeletedInfoQuery = "SELECT commentIdx, postIdx, class, commentOrder, groupNum\n" +
                "FROM Comment\n" +
                "WHERE commentIdx = ? and status = 'DELETE';";

        int result = this.jdbcTemplate.update(deleteQuery, commentIdx);
        this.jdbcTemplate.update(deleteTailQuery, commentIdx);

        if(result == 0)
        {
            patchCommentRes = new PatchCommentRes();
            patchCommentRes.setDeleteSuccess(false);

            return patchCommentRes;
        }

        patchCommentRes = this.jdbcTemplate.queryForObject
                (
                        getDeletedInfoQuery,
                        (rs, rowNum) ->
                        {
                            PatchCommentRes patchCommentRes1 =  new PatchCommentRes();
                            patchCommentRes1.setCommentIdx(rs.getInt("commentIdx"));
                            patchCommentRes1.setPostIdx(rs.getInt("postIdx"));
                            patchCommentRes1.setCommentLevel(rs.getInt("class"));
                            patchCommentRes1.setCommentOrder(rs.getInt("commentOrder"));
                            patchCommentRes1.setCommentGroup(rs.getInt("groupNum"));
                            return patchCommentRes1;
                        }, commentIdx);

        patchCommentRes.setDeleteSuccess(true);

        return patchCommentRes;

    }

    public List<GetCommentRes> getComment(int postIdx, int userIdx){
        String getListQuery =
                "SELECT Comment.commentIdx, userIdx, postIdx, authorName, class, commentOrder, groupNum, commentContent\n" +
                        "FROM Comment LEFT JOIN ReportedComment RC on Comment.commentIdx = RC.commentIdx\n" +
                        "WHERE postIdx = ? and Comment.status = 'ACTIVE' and (reportUserIdx != ? or reportUserIdx is null);";

        String getTimeQuery = "SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Comment WHERE commentIdx = ?), CURRENT_TIMESTAMP);";
        String checkLikeStatusQuery = "SELECT exists(SELECT * FROM CommentLike WHERE userIdx = ? and commentIdx = ? and status = 'ACTIVE');";
        String likeNumberCountQuery = "SELECT COUNT(*) FROM CommentLike WHERE commentIdx = ? and status = 'ACTIVE';";

        return this.jdbcTemplate.query
                (
                        getListQuery,
                        (rs, rowNum) -> new GetCommentRes
                                (
                                        rs.getInt("commentIdx"),
                                        rs.getInt("userIdx"),
                                        rs.getInt("postIdx"),
                                        rs.getInt("class"),
                                        rs.getInt("commentOrder"),
                                        rs.getInt("groupNum"),
                                        this.jdbcTemplate.queryForObject(getTimeQuery, int.class, rs.getInt("commentIdx")),
                                        rs.getString("commentContent"),
                                        rs.getString("authorName"),
                                        this.jdbcTemplate.queryForObject(checkLikeStatusQuery, int.class, userIdx, rs.getInt("commentIdx")) == 1,
                                        this.jdbcTemplate.queryForObject(likeNumberCountQuery, int.class, rs.getInt("commentIdx"))
                                ), postIdx, userIdx
                );
    }

    public PatchModRes modifyComment(int commentIdx, PatchModReq patchModReq){
        String modQuery = "UPDATE Comment SET commentContent=? WHERE commentIdx = ?;";

        Object[] modCommentParameter = new Object[] {
                patchModReq.getModContent(),
                commentIdx
        };

        this.jdbcTemplate.update(modQuery, modCommentParameter);

        String getCommentQuery = "SELECT commentIdx, commentContent, class, commentOrder, groupNum\n" +
                "FROM Comment\n" +
                "WHERE commentIdx = ?;";

        PatchModRes patchModRes = this.jdbcTemplate.queryForObject(getCommentQuery,
                (rs, rowNum) -> new PatchModRes
                        (
                                true,
                                rs.getInt("commentIdx"),
                                rs.getString("commentContent"),
                                rs.getInt("class"),
                                rs.getInt("commentOrder"),
                                rs.getInt("groupNum")
                        ), commentIdx);

        return patchModRes;
    }

    public boolean checkCommentExist(int postIdx){
        String checkQuery = "SELECT COUNT(*) FROM Comment WHERE postIdx = ?;";

        int result = this.jdbcTemplate.queryForObject(checkQuery, int.class, postIdx);

        if(result == 0) return false;
        else return true;
    }

    public boolean checkPostDeleted(int postIdx){
        String checkPostDeletedQuery = "SELECT COUNT(*) FROM Post WHERE postIdx = ? and status = 'DELETE';";

        int result = this.jdbcTemplate.queryForObject(checkPostDeletedQuery, int.class, postIdx);

        if(result > 0) return true;
        else return false;
    }

    public boolean checkCommentDeleted(int commentIdx){
        String checkCommentDeletedQuery = "SELECT COUNT(*) FROM Comment WHERE commentIdx = ? and status = 'DELETE';";

        int result = this.jdbcTemplate.queryForObject(checkCommentDeletedQuery, int.class, commentIdx);

        if(result > 0) return true;
        else return false;
    }

    public boolean isAuthor(int userIdx, int commentIdx){
        String query = "SELECT userIdx FROM Comment WHERE commentIdx = ?;";

        int result = this.jdbcTemplate.queryForObject(query, int.class, commentIdx);

        return result != userIdx;
    }

    public boolean isDeleted(int commentIdx){
        String query = "SELECT status FROM Comment WHERE commentIdx = ?;";

        String result = this.jdbcTemplate.queryForObject(query, String.class, commentIdx);

        return result.equalsIgnoreCase("DELETE");
    }

    public PostCommentLikeRes createCommentLike(int userIdx, int commentIdx) {
        String checkRowExistQuery = "SELECT exists(SELECT * FROM CommentLike WHERE  commentIdx = ? and userIdx = ?);";
        String insertLikeQuery = "INSERT INTO CommentLike(userIdx, commentIdx) VALUES (?, ?);";
        String updateLikeQuery = "UPDATE CommentLike SET status = 'ACTIVE' WHERE userIdx = ? and commentIdx = ?;";
        String checkResultQuery = "SELECT exists(SELECT * FROM CommentLike WHERE  commentIdx = ? and userIdx = ? and status = 'ACTIVE');";

        int check = this.jdbcTemplate.queryForObject(checkRowExistQuery, int.class, commentIdx, userIdx);

        if(check == 1) this.jdbcTemplate.update(updateLikeQuery, userIdx, commentIdx);
        else this.jdbcTemplate.update(insertLikeQuery, userIdx, commentIdx);

        check = this.jdbcTemplate.queryForObject(checkResultQuery, int.class, commentIdx, userIdx);

        if(check == 1) return new PostCommentLikeRes(true);
        else return new PostCommentLikeRes(false);
    }

    public PatchCommentLikeRes deleteCommentLike(int userIdx, int commentIdx) {
        String updateQuery = "UPDATE CommentLike SET status = 'INACTIVE' WHERE userIdx = ? and commentIdx = ?;";
        this.jdbcTemplate.update(updateQuery, userIdx, commentIdx);

        return new PatchCommentLikeRes(true);
    }

    public boolean commentExist(int commentIdx, int userIdx) {
        String checkQuery = "SELECT exists(SELECT * FROM Comment WHERE commentIdx = ? and status = 'ACTIVE');";

        return this.jdbcTemplate.queryForObject(checkQuery, int.class, commentIdx) == 0;
    }


}
