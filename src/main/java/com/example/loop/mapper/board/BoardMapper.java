package com.example.loop.mapper.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.loop.domain.board.BoardDTO;

@Mapper
public interface BoardMapper {
	//보드 전체 데이터 가져오기
	List<BoardDTO> getAllBoardContents();
	
	// 현재 세션에 로그인된 유저의 계시물을 제외하고 값들을 가져옴
//	List<BoardDTO> getBoardExcepUid(Map<String, Object> input);
	
	// 현재 세션에 로그인된 유저의 계시물과 차단된 유저의 계시물을 제외하고 값들을 가져옴
	List<BoardDTO> getBoardExcepUidAndWithBlockedUser(Map<String, Object> input);
	
	// 보드 작성
	int writeBoard(BoardDTO boardDTO);
	
	// 보드 삭제
	int deleteBoard(String userId, int boardNum);
	
	
	//타임 체크 구현부
	
	// 읽은 보드를 체크
	int inputCheckTime(String userId, int boardNum);
	
	// 보드 업데이트 체크
	int updateCheckTime();
	
	
	//좋아요 관련 구현부
	
	//계시글별 좋아요 수를 가져오기
	int boardLikeCount(int typeIndex);

	//유저의 게시글 수 가져오기
	Integer getBoardCntByUserId(String userId);
	
	int getLastNum(String userId);
	//검색어가 해당하는 모든 boardnum 리스트로 가져오기
	List<Integer> getBoardNumsBySearchTerm(String searchTerm, int limit ,int offset);

	//해당 boardnum으로 board내용 가져오기
	BoardDTO getBoardByBoardNum(Integer boardNum);

	List<Integer> getBoardNumsByTime(String formatedNow);

	List<Integer> getBoardNumsByuserId(int offset, int size, String userId);

	List<Integer> getBoardNumsBybookmarks(int offset, int size, String userId);
}