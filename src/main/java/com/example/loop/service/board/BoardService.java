package com.example.loop.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.loop.domain.board.BoardDTO;

public interface BoardService {
	//보드 전체 데이터 가져오기
	List<BoardDTO> getAllBoardContents();
	
	// 조건에 따라 리턴시키는 값들을 정상화 하여 반환
	 List<Map<String, Object>> returnBoardMain(Map<String, Object> input);
	 
	 public Map<String, Object> getBoardDetailsByBoardNum(int boardNum);
	
	
//	long regist(BoardDTO board, MultipartFile[] files, String[] tags) throws Exception;
	
	 long regist(BoardDTO board, MultipartFile[] files, Map<String, List<String>> resultTag) throws Exception;
	
	// 보드 삭제
	int deleteBoard(String userId, int boardNum);
	
	
	//타임 체크 구현부
	
	// 읽은 보드를 체크
	int inputCheckTime(String userId, int boardNum);
	
	// 보드 업데이트 체크
	int updateCheckTime();

	HashMap<String, Object> postComponent(int boardnum);
	//검색어에 따라 데이터 가져오기
	List<Map<String, Object>> returnSearchQuery(Map<String, String> query);

	//시간에 따라 데이터 가져오기
	List<Map<String, Object>> returnRanking();

	List<Map<String, Object>> getPosts(int page, int size, String userId);

	boolean deletePost(int boardNum, String userId);

	List<Map<String, Object>> returnButtonState(String userId, int boardNum);

	boolean toggleLikeState(int boardNum, String userId, boolean likeCheck);

	boolean toggleSaveState(int boardNum, String userId, boolean saveCheck);

	boolean deleteBookmarks(int boardNum, String userId);

	List<Map<String, Object>> getBookmarks(int page, int size, String userId);

}