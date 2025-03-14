package com.example.loop.controller;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.loop.domain.board.BoardDTO;
import com.example.loop.domain.tag.TagDTO;
import com.example.loop.domain.user.UserDTO;
import com.example.loop.service.board.BoardService;
import com.example.loop.service.reply.ReplyService;
import com.example.loop.service.tag.TagService;
import com.example.loop.service.user.BlockService;
import com.example.loop.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/api/board")
public class BoardController {

	private final BoardService boardService;
	private final TagService tagService;
	private final UserService userService;
	private final ReplyService replyService;
	private final BlockService blockService;
	
	public BoardController(BoardService boardService, TagService tagService, UserService userService, ReplyService replyService, BlockService blockService) {
		this.boardService = boardService;
		this.tagService = tagService;
		this.userService = userService;
		this.replyService = replyService;
		this.blockService = blockService;
	}
	
	@PostMapping("/main")
	public ResponseEntity<Map<String, Object>> showBoardMain(HttpServletRequest request, @RequestBody Map<String, Object> input) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	    	// 조건에 맞게 정렬처리한 값들을 묶어서 반환
	    	List<Map<String, Object>> responseList = boardService.returnBoardMain(input);

	        // 성공 응답 작성
	        response.put("status", "success");
	        response.put("data", responseList);
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 발생 시 에러 메시지 작성
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@GetMapping("/postDetail/{boardNum}")
	public ResponseEntity<Map<String, Object>> getPostDetail(@PathVariable int boardNum) {
		try {
			 Map<String, Object> boardDetails = boardService.getBoardDetailsByBoardNum(boardNum);
			 return ResponseEntity.ok(boardDetails);
		 } catch (IllegalArgumentException e) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(Collections.emptyMap()); // 빈 Map 반환
		    }
	}
	
	
	@PostMapping("/write")
	public ResponseEntity<Long> write(BoardDTO board, MultipartFile[] files, String[] tags, HttpServletRequest req) throws Exception {
	    // 로그인된 사용자 ID를 가져옴
	    String userId = (String) req.getSession().getAttribute("loginUser");
	    board.setUserId(userId);
	    // 게시글 등록 서비스 호출 (tags는 현재 사용하지 않음)
	    
//	    System.out.println(board);
//	    System.out.println("tag"+tags);
	    if(tags == null) {
	    	Map<String, List<String>> resultTag = new HashMap<>();
	    	long boardNum = boardService.regist(board, files, resultTag);
		    if (boardNum != -1) {	    	
		        return new ResponseEntity<>(boardNum, HttpStatus.OK);
		    } else {
		        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	    }
	    
	    else if(tags.length>0) {
	    	// Flask URL 정의
	        String flaskUrl = "http://localhost:5000/categorize";  // Flask 서버의 URL

	        // 요청 헤더 설정
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        // 요청 바디에 tags 배열 추가
	        Map<String, Object> request = new HashMap<>();
	        request.put("tags", tags);  // tags 배열을 요청에 포함
	        Map<String, List<String>> resultTag = new HashMap<>();

	        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

	        // RestTemplate을 사용하여 Flask 서버로 POST 요청 전송
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, entity, String.class);

	     // Flask 서버로부터 응답을 받은 후 처리
	        if (response.getStatusCode() == HttpStatus.OK) {
	            String result = response.getBody();
	            // JSON 파싱을 위해 ObjectMapper 사용
	            ObjectMapper objectMapper = new ObjectMapper();
	            try {
	                	resultTag = objectMapper.readValue(result, Map.class);
	                // 반환된 결과 확인
	                System.out.println("Flask Response: " + resultTag);
	            } catch (IOException e) {
	                // JSON 파싱 오류 처리
	                System.out.println("JSON 파싱 오류: " + e.getMessage());
	            }
	        } else {
	            // 오류 처리
	            System.out.println("Flask 서버에서 오류 발생");
	        }
	        
	        long boardNum = boardService.regist(board, files, resultTag);
		    if (boardNum != -1) {	    	
		        return new ResponseEntity<>(boardNum, HttpStatus.OK);
		    } else {
		        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		    }

	    }else {
	    	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    
	    
	}
	
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> search(HttpServletRequest request,  @RequestBody Map<String, String> query){
		Map<String, Object> response = new HashMap<>();		
		System.out.println("여기 들어옴");
	    
	    try {
	        // 검색어를 사용하여 게시글 목록 반환
	    	List<Map<String, Object>> boardnumSearch = boardService.returnSearchQuery(query);

	        // 성공 응답
	        response.put("status", "success");
	        response.put("data", boardnumSearch);
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 응답
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	
	@GetMapping("postStatus/{boardnum}")
	public ResponseEntity<HashMap<String, Object>> postStatus(@PathVariable int boardnum){
		return new ResponseEntity<HashMap<String, Object>>(boardService.postComponent(boardnum),HttpStatus.OK);
	}
  
  
	@PostMapping("/ranking")
	public ResponseEntity<Map<String, Object>> showRanking(@RequestBody Map<String, String> request) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	    	// 조건에 맞게 정렬처리한 값들을 묶어서 반환
	    	List<Map<String, Object>> responseList = boardService.returnRanking();
//	    	System.out.println(responseList);
	        // 성공 응답 작성
	        response.put("status", "success");
	        response.put("data", responseList);
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 발생 시 에러 메시지 작성
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(HttpServletRequest request,@RequestBody Map<String, String>postRequest) {
		Map<String, Object> response = new HashMap<>();
		int page = Integer.parseInt(postRequest.get("page"));
		String userId = postRequest.get("userId");

		// 유효성 검사
	    if (page <= 0 || userId == null || userId.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "Invalid input parameters.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

		try {
	        // 페이지네이션 처리 로직
	        List<Map<String, Object>> posts = boardService.getPosts(page, 6, userId);

	        // 성공 응답 작성
	        response.put("status", "success");
	        response.put("data", posts);

	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 발생 시 에러 메시지 작성
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
    }
	
	@DeleteMapping("/posts/{userId}/{boardNum}")
	public ResponseEntity<String> deletePost(@PathVariable int boardNum, @PathVariable String userId) {
	    boolean isDeleted = boardService.deletePost(boardNum, userId);

	    if (isDeleted) {
	        return ResponseEntity.ok("게시글이 삭제되었습니다.");
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 실패");
	    }
	}
	
	@PostMapping("/bookmarks")
    public ResponseEntity<Map<String, Object>> getBookmarks(HttpServletRequest request,@RequestBody Map<String, String>postRequest) {
		Map<String, Object> response = new HashMap<>();
		int page = Integer.parseInt(postRequest.get("page"));
		String userId = postRequest.get("userId");

		// 유효성 검사
	    if (page <= 0 || userId == null || userId.trim().isEmpty()) {
	        response.put("status", "error");
	        response.put("message", "Invalid input parameters.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

		try {
	        // 페이지네이션 처리 로직
	        List<Map<String, Object>> bookmarks = boardService.getBookmarks(page, 6, userId);

	        // 성공 응답 작성
	        response.put("status", "success");
	        response.put("data", bookmarks);

	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 발생 시 에러 메시지 작성
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
    }
	
	@DeleteMapping("/bookmarks/{userId}/{boardNum}")
	public ResponseEntity<String> deleteBookmarks(@PathVariable int boardNum, @PathVariable String userId) {
	    boolean isDeleted = boardService.deleteBookmarks(boardNum, userId);

	    if (isDeleted) {
	        return ResponseEntity.ok("게시글이 삭제되었습니다.");
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 실패");
	    }
	}
	
	@PostMapping("/buttonCheck")
	public ResponseEntity<Map<String, Object>> buttonCheck(@RequestBody Map<String, String> dataToSend){
	    String userId = dataToSend.get("userId");
	    int boardNum = Integer.parseInt(dataToSend.get("boardNum"));
	    String likeCheck = dataToSend.get("likeCheck");
	    String saveCheck = dataToSend.get("saveCheck");
	    String likeNumStr = dataToSend.get("likeNum");

	    int likeNum;
	    if (likeNumStr == null || likeNumStr.isEmpty()) {
	        // likeNum이 null이거나 비어있으면 기본값 0을 설정
	        likeNum = 0;
	    } else {
	        // likeNum이 null이 아니면 파싱
	        likeNum = Integer.parseInt(likeNumStr);
	    }

		Map<String, Object> response = new HashMap<>();
	    
	    try {
	    	List<Map<String, Object>> buttonState = boardService.returnButtonState(userId, boardNum);

	        // 성공 응답
	        response.put("status", "success");
	        response.put("data", buttonState);
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 에러 응답
	        response.put("status", "error");
	        response.put("message", "Failed to retrieve board information.");
	        response.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PostMapping("/likeToggle")
	public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, String> likeRequest) {
	    String userId = likeRequest.get("userId");
	    int boardNum = Integer.parseInt(likeRequest.get("boardNum"));
	    // likeCheck가 "true"일 때만 true로 변환
	    boolean likeCheck = "true".equals(likeRequest.get("likeCheck"));

	    // 응답에 담을 데이터를 저장할 맵 생성
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // 비즈니스 로직 처리: likeCheck를 기반으로 상태를 업데이트
	        boolean updatedLikeState = boardService.toggleLikeState(boardNum, userId, likeCheck);

	        // 업데이트된 좋아요 상태를 응답에 담음
	        response.put("status", "success");
	        response.put("updatedLikeState", updatedLikeState);

	        // 성공 응답 반환
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 예외가 발생한 경우 에러 응답 구성
	        response.put("status", "error");
	        response.put("message", "Failed to toggle like state.");
	        response.put("error", e.getMessage());

	        // 에러 응답 반환
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PostMapping("/saveToggle")
	public ResponseEntity<Map<String, Object>> toggleSave(@RequestBody Map<String, String> saveRequest) {
	    String userId = saveRequest.get("userId");
	    int boardNum = Integer.parseInt(saveRequest.get("boardNum"));
	    // saveCheck가 "true"일 때만 true로 변환
	    boolean saveCheck = "true".equals(saveRequest.get("saveCheck"));

	    // 응답에 담을 데이터를 저장할 맵 생성
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // 비즈니스 로직 처리: saveCheck를 기반으로 상태를 업데이트
	        boolean updatedSaveState = boardService.toggleSaveState(boardNum, userId, saveCheck);

	        // 업데이트된 저장 상태를 응답에 담음
	        response.put("status", "success");
	        response.put("updatedSaveState", updatedSaveState);

	        // 성공 응답 반환
	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception e) {
	        // 예외가 발생한 경우 에러 응답 구성
	        response.put("status", "error");
	        response.put("message", "Failed to toggle like state.");
	        response.put("error", e.getMessage());

	        // 에러 응답 반환
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	

	
}