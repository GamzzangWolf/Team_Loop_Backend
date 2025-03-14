package com.example.loop.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.loop.domain.reply.ReplyDTO;
import com.example.loop.mapper.reply.ReplyMapper;
import com.example.loop.service.reply.ReplyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("api/reply/*")
public class ReplyController {
	
	private final ReplyService replyService;
	private final ReplyMapper replyMapper;
	
	public ReplyController(ReplyMapper repylMapper ,ReplyService replyService) {
		this.replyService = replyService;
		this.replyMapper = repylMapper;
	}


	@GetMapping("/get")
	@ResponseBody
	public List<ReplyDTO> getCommentByBoardNum(@RequestBody Integer boardNum) {	
//		계시글 번호를 가지고 댓글을 가져오기
//		System.out.println("Received boardNum: " + boardNum);  // 로그 추가
		
		List<ReplyDTO> result = replyService.getReplyByBoardNum(boardNum);
		
		return result;
	}
	
	@GetMapping("/boardReplyWithProfile")
	public ResponseEntity<?> getComments(@RequestParam int boardNum, @RequestParam int page, @RequestParam String userId, @RequestParam String likeCheck) {
		 try {
		        // likeCheck가 null일 경우 기본값으로 false 설정
		        boolean isLiked = likeCheck != null && Boolean.parseBoolean(likeCheck);
		        // 첫 번째 댓글 번호 가져오기
		        List<Map<String, Object>> replies = replyService.getReplyByBoardNumWithProfile(boardNum, page, userId, isLiked);
		        // 응답 데이터 준비
		        Map<String, Object> response = new HashMap<>();
		        response.put("replies", replies);
		        return ResponseEntity.ok(response);

		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		    }
	}
	
	
	@PostMapping("/write")
	public ResponseEntity<?> replyWrite(@RequestBody ReplyDTO replyDTO) {
	    try {
	        // 서비스 호출로 댓글 작성 및 작성된 댓글 정보 조회
	        List<Map<String, Object>> savedReply = replyService.replyWrite(replyDTO);

	        if (savedReply != null && !savedReply.isEmpty()) {
	            // 댓글 등록 성공
	            return ResponseEntity.ok(savedReply);
	        } else {
	            // 댓글 등록 실패
	            Map<String, String> errorResponse = new HashMap<>();
	            errorResponse.put("message", "댓글 등록에 실패했습니다.");
	            return ResponseEntity.badRequest().body(errorResponse);
	        }
	    } catch (Exception e) {
	        // 예외 발생 처리
	        e.printStackTrace();
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("message", "댓글 등록 중 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<?> replyDelete(HttpServletRequest request, @RequestBody Map<String, Object> requestBody){
		//테스트용 코드
		
		String userId = (String) requestBody.get("userId");
		
		 // requestBody에서 replyNum을 꺼내고, Integer로 변환
	    Integer replyNum = (Integer) requestBody.get("replyNum");
	    
		
		if (userId == null || userId.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId is required");
	    }
		if(replyNum == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("replyNum is required");
		}
		
//		System.out.println("Delete Query: userId = " + userId + ", replyNum = " + replyNum);

		int result = replyService.replyDelete(userId, replyNum);
		
		// 결과 반환
	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete Reply");
	    }
	}
	
	 // 댓글 업데이트
    @PatchMapping("/update")
    public ResponseEntity<?> updateReplyContents(@RequestBody ReplyDTO replyDTO) {
        // 댓글 수정 서비스 호출
    	List<Map<String, Object>> updatedReply = replyService.replyUpdate(replyDTO);
    	System.out.println(updatedReply);

        if (updatedReply != null) {
            // 수정된 댓글 객체를 반환
            return ResponseEntity.ok(updatedReply); // 200 OK와 수정된 댓글 반환
        } else {
            // 실패 시 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update the reply.");
        }
    }
    
    @PostMapping("/likeToggle")
	public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, String> likeRequest) {
	    String userId = likeRequest.get("userId");
	    int replyNum = Integer.parseInt(likeRequest.get("replyNum"));
	    // likeCheck가 "true"일 때만 true로 변환
	    String likeCheckValue = likeRequest.get("likeCheck");
	    boolean likeCheck = "1".equals(likeCheckValue);
	    // 응답에 담을 데이터를 저장할 맵 생성
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // 비즈니스 로직 처리: likeCheck를 기반으로 상태를 업데이트
	        boolean updatedLikeState = replyService.toggleLikeState(replyNum, userId, likeCheck);

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
	
	
	
}