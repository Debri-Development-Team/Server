package com.example.debriserver.core.Board;

import com.example.debriserver.basicModels.BasicException;
import com.example.debriserver.basicModels.BasicResponse;
import com.example.debriserver.basicModels.BasicServerStatus;
import com.example.debriserver.core.Board.model.GetUnscrapBoardListRes;
import com.example.debriserver.core.Board.model.GetScrapBoardListRes;
import com.example.debriserver.utility.jwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.debriserver.basicModels.BasicServerStatus.USERS_EMPTY_USER_ID;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    final jwtUtility jwt = new jwtUtility();

    @Autowired
    private final BoardProvider boardProvider;

    @Autowired
    private final BoardService boardService;

    public BoardController(BoardProvider boardProvider, BoardService boardService) {
        this.boardProvider = boardProvider;
        this.boardService = boardService;
    }

    /**
     * 게시판 스크랩
     */
    @ResponseBody
    @PostMapping("/scrap/{boardIdx}")
    public BasicResponse<String> scrapBoard(@PathVariable("boardIdx") int boardIdx) {
        try {
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);
            boardService.scrapBoard(boardIdx, userIdx);

            String result = "게시판이 스크랩 되었습니다.";

            return new BasicResponse<>(result);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시판 스크랩 취소
     */
    @ResponseBody
    @PatchMapping("/scrap/cancel/{boardIdx}")
    public BasicResponse<String> cancelScrapBoard(@PathVariable("boardIdx") int boardIdx) {
        try {
            String jwtToken = jwt.getJwt();

            if(jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);
            boardService.cancelScrapBoard(boardIdx, userIdx);

            String result = "게시판 스크랩이 취소 되었습니다.";

            return new BasicResponse<>(result);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     * 2.3 유저의 즐겨찾기 게시판 조회
     */
    @GetMapping("/scrap/getList")
    public BasicResponse<List<GetScrapBoardListRes>> getBoardList() {

        try {
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);
            List<GetScrapBoardListRes> getScrapBoardListRes = boardService.getScrapBoardList(userIdx);

            return new BasicResponse<>(getScrapBoardListRes);
        } catch (BasicException exception) {
            return new BasicResponse<>((exception.getStatus()));
        }
    }

    /**
     *
     * 2.4 유저가 구독하지 않은 게시판 리스트 조회 api
     * */
    @GetMapping("/unscrap/getList")
    public BasicResponse<List<GetUnscrapBoardListRes>> getList(){

        try{
            String jwtToken = jwt.getJwt();

            if (jwt.isJwtExpired(jwtToken)) throw new BasicException(BasicServerStatus.EXPIRED_TOKEN);

            int userIdx = jwt.getUserIdx(jwtToken);

            if(boardProvider.checkUserExist(userIdx) == 0)
            {
                throw new BasicException(USERS_EMPTY_USER_ID);
            }

            List<GetUnscrapBoardListRes> getUnscrapBoardListResList = boardProvider.getList(userIdx);

            return new BasicResponse<>(getUnscrapBoardListResList);

        }catch (BasicException exception){
            return new BasicResponse<>((exception.getStatus()));
        }
    }
}
