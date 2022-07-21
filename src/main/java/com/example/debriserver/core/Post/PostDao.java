package com.example.debriserver.core.Post;

import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.core.Post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertPosts(PostPostsReq postPostsReq){
        String insertPostQuery = "INSERT INTO Post(boardIdx, userIdx, postContent, postName) VALUES(?,?,?,?)";
        Object []insertPostParams = new Object[]{
                postPostsReq.getBoardIdx(),
                postPostsReq.getUserIdx(),
                postPostsReq.getPostContent(),
                postPostsReq.getPostName()
        };
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }

    /*public int insertPostsImgs(int postIdx, PostImgUrlReq postImgUrlReq){
        String insertPostImgsQuery = "INSERT INTO PostImage(postIdx, url) VALUES(?,?)";
        Object []insertPostImgsParams = new Object[]{postIdx, postImgUrlReq.getUrl()};
        this.jdbcTemplate.update(insertPostImgsQuery, insertPostImgsParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

    }*/

    public int updatePost(int postIdx, String postContent){
        String updatePostQuery = "UPDATE Post SET postContent=? WHERE postIdx=?";
        Object []updatePostParams = new Object[]{postContent, postIdx};
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }

    /**
     * 있으면 TRUE, 없으면 FALSE
     * */
    public boolean checkBoardExist(int boardIdx){
        String checkBoardQuery = "SELECT exists(SELECT boardIdx  FROM Board WHERE boardIdx = ?);";

        int result = this.jdbcTemplate.queryForObject(checkBoardQuery, int.class, boardIdx);

        return result != 0;
    }

    public int deletePost(int postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETE' WHERE postIdx=?";
        int deletePostParams = postIdx;
        return this.jdbcTemplate.update(deletePostQuery,
                deletePostParams);
    }

    public int insertPostLike(PostPostLikeReq postPostLikeReq) {
        String insertPostLikeQuery = "INSERT INTO PostLike(postIdx, userIdx, likeStatus) VALUES(?,?,?)";
        Object []insertPostLikeParams = new Object[]{
                postPostLikeReq.getPostIdx(),
                postPostLikeReq.getUserIdx(),
                postPostLikeReq.getLikeStatus()
        };
        return this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);
    }

    public int deletePostLike(int postIdx) {
        String insertPostLikeQuery = "UPDATE PostLike SET likeStatus='NULL' WHERE postIdx=?";
        int insertPostLikeParams = postIdx;
        return this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);
    }

    public List<GetPostListRes> getPostList(int boardIdx){
        String getListQuery = "SELECT distinct p.boardIdx, p.postIdx, u.nickname, p.postName\n" +
                "FROM Post as p LEFT JOIN User as u ON p.userIdx = u.userIdx WHERE boardIdx = ?;";

        String getTimeQuery = "SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = ?), CURRENT_TIMESTAMP);";

        String getLikeCountQuery = "SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = ? and likeStatus = 'LIKE';";

        String getCommentNumberQuery = "SELECT COUNT(commentIdx) FROM Comment WHERE postIdx = ?;";

        return this.jdbcTemplate.query(getListQuery,
                (rs, rowNum) -> new GetPostListRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getString("nickName"),
                                rs.getString("postName"),
                                this.jdbcTemplate.queryForObject(getLikeCountQuery, int.class, rs.getInt("postIdx")),
                                this.jdbcTemplate.queryForObject(getTimeQuery, int.class, rs.getInt("postIdx")),
                                this.jdbcTemplate.queryForObject(getCommentNumberQuery, int.class, rs.getInt("postIdx"))
                        ),
                boardIdx);
    }

    public GetPostRes getPost(int postIdx){
        String getPostQuery = "SELECT distinct p.boardIdx, p.postIdx, p.postName, u.nickname, p.postContent\n" +
                "FROM Post as p LEFT JOIN User as u ON p.userIdx = u.userIdx WHERE postIdx = ?;";
        String getLikeQuery = "SELECT COUNT(postIdx) FROM PostLike WHERE postIdx = ? and likeStatus = 'LIKE';";
        String getTimeQuery = "SELECT TIMESTAMPDIFF(minute, (SELECT createdAt FROM Post WHERE postIdx = ?), CURRENT_TIMESTAMP);";

        return this.jdbcTemplate.queryForObject(getPostQuery,
                (rs, rowNum) -> new GetPostRes
                        (
                                rs.getInt("boardIdx"),
                                rs.getInt("postIdx"),
                                rs.getString("postName"),
                                rs.getString("nickName"),
                                rs.getString("postContent"),
                                this.jdbcTemplate.queryForObject(getLikeQuery, int.class, postIdx),
                                this.jdbcTemplate.queryForObject(getTimeQuery, int.class, postIdx)
                        ),
                postIdx);
    }
}
