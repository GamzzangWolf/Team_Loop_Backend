package com.example.loop.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.loop.service.tag.TagService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/api/tag")
public class TagController {
	
	private final TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}
	
	
	
	@PostMapping("/searchTag")
	public ResponseEntity<?> searchTag(@RequestBody Map<String, String> requestBody) {
	    // 서비스 호출하여 검색된 태그 리스트 받기
		String userInput = requestBody.get("userInput");
	    List<Map<String, Object>> searchResults = tagService.searchTagByUserInput(userInput);
	    System.out.println(searchResults);
	    
	    // 결과가 없을 경우
	    if (searchResults.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("검색된 태그가 없습니다.");
	    }
	    
	    // 결과가 있을 경우
	    return ResponseEntity.ok(searchResults);
	}

	
	
	
	
	@PostMapping("/follow")
	public ResponseEntity<?> tagFollow(HttpServletRequest request, @RequestBody Map<String, Object> body) {
	    // 클라이언트로부터 받은 데이터 처리
	    HttpSession session = request.getSession();
	    session.setAttribute("userId", "test");
	    String userId = (String) session.getAttribute("userId");

	    // body에서 tagIds 값을 추출 (List<Integer>)
	    List<Integer> tagIds = (List<Integer>) body.get("tagIds");

	    if (userId == null || userId.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId is required");
	    }

	    // tagIds가 없거나 비어 있으면 처리
	    if (tagIds == null || tagIds.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tagIds are required");
	    }

	    // 여러 태그를 팔로우
	    int result = tagService.tagFollow(userId, tagIds); // tagIds 리스트를 한 번에 전달

	    // 결과 반환
	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.CREATED).body("Follow created successfully");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create follow");
	    }
	}


	
	@DeleteMapping("/unFollow")
	public ResponseEntity<?> tagUnFollow(HttpServletRequest request, @RequestBody Map<String, Object> body) {
	    // 테스트용 코드
	    HttpSession session = request.getSession();
	    session.setAttribute("userId", "test");
	    String userId = (String) session.getAttribute("userId");

	    // body에서 tagIds 값을 추출 (List<Integer>)
	    List<Integer> tagIds = (List<Integer>) body.get("tagIds");

	    if (userId == null || userId.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId is required");
	    }

	    // tagIds가 없거나 비어 있으면 처리
	    if (tagIds == null || tagIds.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tagIds are required");
	    }

	    // 여러 태그를 언팔로우
	    int result = tagService.tagUnFollow(userId, tagIds); // tagIds 리스트를 한 번에 전달

	    // 결과 반환
	    if (result > 0) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to unfollow the tags.");
	    }
	}

	
	
	

}