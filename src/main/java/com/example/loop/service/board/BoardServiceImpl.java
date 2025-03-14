package com.example.loop.service.board;
package com.example.loop.service.board;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.loop.domain.file.FileDTO;
import com.example.loop.domain.reply.ReplyDTO;
import com.example.loop.domain.board.BoardDTO;
import com.example.loop.domain.board.TagRelationDTO;
import com.example.loop.domain.file.FileDTO;
import com.example.loop.domain.tag.TagDTO;
import com.example.loop.domain.user.NotificationDTO;
import com.example.loop.domain.user.UserDTO;
import com.example.loop.mapper.board.BoardMapper;
import com.example.loop.mapper.board.BookmarkMapper;
import com.example.loop.mapper.file.FileMapper;
import com.example.loop.mapper.reply.ReplyMapper;
import com.example.loop.mapper.tag.TagMapper;
import com.example.loop.service.reply.ReplyService;
import com.example.loop.service.tag.TagService;
import com.example.loop.service.user.BlockService;
import com.example.loop.service.user.NotificationService;
import com.example.loop.service.user.UserService;
import com.example.loop.mapper.board.LikeMapper;

@Service
public class BoardServiceImpl implements BoardService {

	@Value("${file.dir}")
	private String saveFolder;

//	@Autowired
//	private BoardMapper boardMapper;

	private final BoardMapper boardMapper;
	private final TagMapper tagMapper;
	private final UserMapper userMapper;
	private final ReplyMapper replyMapper;
	private final BlockMapper blockMapper;
	private final FileMapper fileMapper;
	private final NotificationMapper notificationMapper;
	private final CheckMapper checkMapper;
	private final BookmarkMapper bookmarkMapper;
	private final LikeMapper likeMapper;
	private final FollowMapper followMapper;
	private final TagService tagService;
	private final UserService userService;
	private final NotificationMapper notifyMapper;
	private final NotificationService notifyService;
	private final SearchLogMapper searchLogMapper;
	
	public BoardServiceImpl(BoardMapper boardMapper, TagMapper tagMapper, UserMapper userMapper,
			ReplyMapper replyMapper, BlockMapper blockMapper, NotificationMapper notificationMapper,
			CheckMapper checkMapper, BookmarkMapper bookmarkMapper, LikeMapper likeMapper,
			UserService userService, TagService tagService, FileMapper fileMapper, NotificationMapper notifyMapper,
			NotificationService notifyService,FollowMapper followMapper, SearchLogMapper searchLogMapper) {
		this.boardMapper = boardMapper;
		this.tagMapper = tagMapper;
		this.userMapper = userMapper;
		this.replyMapper = replyMapper;
		this.blockMapper = blockMapper;
		this.fileMapper = fileMapper;
		this.notificationMapper = notificationMapper;
		this.checkMapper = checkMapper;
		this.bookmarkMapper = bookmarkMapper;
		this.likeMapper = likeMapper;
		this.userService = userService;
		this.tagService = tagService;
		this.notifyMapper = notifyMapper;
		this.notifyService = notifyService;
		this.followMapper = followMapper;
		this.searchLogMapper = searchLogMapper;
	}

	@Override
	public List<BoardDTO> getAllBoardContents() {
		// 계시글 전체를 가져오는 비즈니스 로직
		List<BoardDTO> result = boardMapper.getAllBoardContents();
//		System.out.println("전체 보드 컨텐츠를 가져오는지 테스트 : "+result);
		return result;
	}

	@Override
	public List<Map<String, Object>> returnBoardMain(Map<String, Object> input) {
		// TODO Auto-generated method stub
		// BoardController에서 비즈니스 로직을 처리하고 있는 문제가 있어 서비스에서 비즈니스 로직을 처리하게 수정

		// 테스트용 코드
		String userId = (String) input.get("userId");
		int page = (int) input.get("page");

		int pageSize = 1;
		int offset = (page-1)*pageSize;
		
//		System.out.println("Limit : "+ pageSize+ " Offset : "+ offset);
		input.put("limit", pageSize);
		input.put("offset", offset);

		// 여러 값들을 묶어서 보내주기 위한 List 형식의 HashMap
		List<Map<String, Object>> responseList = new ArrayList<>();

//		유저 정보를 아이디를 기준으로 조회해서 DTO에 담아서 반환
// 		if (page == 1) {
// 			UserDTO userData = userMapper.getUserByUserid(userId);
// 			Map<String, Object> userProfile = userService.getUserIdAndProfileById(userId);

// 			// MAP 형식으로 반환하기 위한 해시 맵 생성
// 			Map<String, Object> userInfo = new HashMap<>();
// 			// "User" 이름으로 유저 프로필 데이터를 삽입(지금 로그인한 유저의 프로필)
// 			userInfo.put("UserLogedIn", userProfile);
// 			// 묶어서 값들을 한번에 보내기 위해 List 형식의 HashMap 에 데이터를 추가
// 			responseList.add(userInfo);
// 		}
// 		// 자신과 차단 유저의 계시물을 제외하고 읽은 계시물을 후순위 정렬하여 데이터를 가져옴
 		List<BoardDTO> boardList = boardMapper.getBoardExcepUidAndWithBlockedUser(input);

//        	System.out.println(boardList);

		// 반복문을 돌면서 각각 계시물의 태그와 댓글 수를 가져옴
		for (BoardDTO board : boardList) {
			Map<String, Object> boardPack = getBoardDetails(board);
			// 각 Map들을 리스트에 추가
			responseList.add(boardPack); // 각 게시물과 태그 데이터 추가
		}

		return responseList;

	}

	@Override
	public Map<String, Object> getBoardDetailsByBoardNum(int boardNum) {
		// TODO Auto-generated method stub
		BoardDTO board = boardMapper.getBoardByBoardNum(boardNum);
		if (board == null) {
			throw new IllegalArgumentException("Invalid boardNum: " + boardNum);
		}

		// 기존 메서드를 호출
		return getBoardDetails(board);
	}

	@Override
	   public long regist(BoardDTO board, MultipartFile[] files, Map<String, List<String>> resultTag) throws Exception {
	      // 게시글 저장
	      if (boardMapper.writeBoard(board) != 1) {
	         return -1;
	      }

	      // 저장된 게시글 번호 가져오기
	      int boardNum = boardMapper.getLastNum(board.getUserId());
//	       System.out.println("boardNum: " + boardNum);

	      if (resultTag != null && !resultTag.isEmpty()) {
	    	  for (Map.Entry<String, List<String>> entry : resultTag.entrySet()) {
	              String category = entry.getKey(); // 카테고리 이름 (기타, 여행 등)
	              List<String> tags = entry.getValue(); // 해당 카테고리에 포함된 태그 리스트
	           
	           // 카테고리의 태그 처리
	              for (String tagName : tags) {
	                  // 태그를 정규화하여 검색
	                  TagDTO tag = tagMapper.getTagByName(tagName.toLowerCase().trim()); // 이름을 소문자로 정규화

	                  int tagId;
	                  if (tag == null) {
	                      // 태그가 없으면 새로 생성
	                      TagDTO tagDTO = new TagDTO();
	                      tagDTO.setTagName(tagName);
	                      tagDTO.setNormalizedTag(category); // 정규화된 태그 저장
	                      tagDTO.setSimilarityId(Math.random()); // 랜덤 similarityId 생성
	                      tagMapper.tagInput(tagDTO); // 새 태그 입력
	                      
	                      
//	                      System.out.println("저장된 테그 이름과 카테고리");
//	                      System.out.println("Categoty : "+ category);
//	                      System.out.println("tagName : "+ tagName);

	                      // 방금 삽입한 태그의 ID 가져오기
	                      tagId = tagMapper.getLastTagId();
	                  } else {
	                      // 기존 태그가 있다면 ID 사용
	                      tagId = tag.getTagId();
	                  }

	                  // 태그-게시글 관계 저장
	                  TagRelationDTO tagRelation = new TagRelationDTO();
	                  tagRelation.setBoardNum(boardNum);
	                  tagRelation.setTagId(tagId);
	                  tagMapper.insertTagRelation(tagRelation);
	              }
	    	  }
	      }
	      else {
	    	  System.out.println("콘솔찍기");
	      }

	      // 파일 처리
	      if (files != null && files.length > 0) {
//	           System.out.println("파일 개수 : " + files.length);

	         String savePath = saveFolder + files;
	         File directory = new File(saveFolder);
	         if (!directory.exists()) {
	            directory.mkdirs(); // 경로가 없으면 생성
	         }

	         for (MultipartFile file : files) {
	            String orgname = file.getOriginalFilename();
	            int lastIdx = orgname.lastIndexOf(".");
	            String ext = orgname.substring(lastIdx);

	            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	            String systemname = time + UUID.randomUUID().toString() + ext;

	            // 파일 저장
	            String fullPath = saveFolder + systemname;
	            file.transferTo(new File(fullPath));

	            // FileDTO 생성 및 저장
	            FileDTO fdto = new FileDTO();
	            fdto.setOrgName(orgname);
	            fdto.setSystemName(systemname);
	            fdto.setBoardNum(boardNum); // 게시글 번호 설정
	            fileMapper.insertFile(fdto); // 데이터베이스에 삽입
	         }
	      }
	      NotificationDTO notify = new NotificationDTO();
	      notify.setNotificationType(5);
	      notify.setTypeIndex(boardNum);
	      notify.setNotifier(board.getUserId());
	      List<String> users = followMapper.getAllFollowerListByUserId(board.getUserId());
	      if(users.size()>0) {
	    	  for(String user : users) {
	    		  notify.setRecipient(user);
	    		  notifyMapper.insertNotification(notify);
	    		  NotificationDTO newnotify = notifyMapper.getNotification(board.getUserId());
	    		  notifyService.sendNotificationToUser(user, newnotify);
	    	  }
	      }
	      return boardNum;
	   }
	
	@Override
	public int deleteBoard(String userId, int boardNum) {
		// TODO Auto-generated method stub
		// 계시글을 삭제하는 로직
		int result = 0;
		if (userId != null && !userId.isEmpty()) {
			result = boardMapper.deleteBoard(userId, boardNum);
		} else if (userId == null || userId.isEmpty()) {
			System.out.println("계시글 삭제 아이디값 오류");
		} else {
			System.out.println("계시글 삭제 UnExpected Error");
		}
		return result;
	}

	@Override
	public int inputCheckTime(String userId, int boardNum) {
		// TODO Auto-generated method stub
		// 계시글 전체를 가져오는 비즈니스 로직
		int result = boardMapper.inputCheckTime(userId, boardNum);
//				System.out.println("전체 보드 컨텐츠를 가져오는지 테스트 : "+result);
		return result;
	}

	@Override
	public int updateCheckTime() {
		// TODO Auto-generated method stub
		// 게시글
		return 0;
	}

	@Override
	public List<Map<String, Object>> returnSearchQuery(Map<String, String> query) {
	    // 검색어가 해당하는 모든 boardnum 가져오기
//		System.out.println(query);
		String userId = query.get("userId");
		String searchTerm = query.get("query"); // 클라이언트에서 보낸 검색어 추출
		int page = Integer.parseInt(query.get("page"));
		int limit = 2;
		int offset = (page-1)*limit;
		
//		System.out.println("userId : "+ userId+ " searchTerm : " + searchTerm);
//		System.out.println("limit : " + limit + " offset : "+ offset);
		
	    List<Integer> boardNums = boardMapper.getBoardNumsBySearchTerm(searchTerm, limit , offset);
	   	    
	    
	    //System.out.println("검색된 boardNums: " + boardNums); // 검색된 boardNum 리스트 출력

		List<Map<String, Object>> boardnumSearch = new ArrayList<>();

		for (Integer boardNum : boardNums) {
			// boardNum 기반으로 데이터 가져오기
			Map<String, Object> boardData = new HashMap<>();
			BoardDTO board = boardMapper.getBoardByBoardNum(boardNum);
			// System.out.println("현재 처리 중인 board: " + board); // 각 게시글 데이터 확인

			boardData.put("board", board);

			// 작성자 정보 가져오기
			Map<String, Object> writerProfile = userService.getUserIdAndProfileById(board.getUserId());
			// System.out.println("작성자 정보: " + writerProfile); // 작성자 정보 출력

			boardData.put("writer", writerProfile);

			// 태그 정보 가져오기
			List<String> tags = tagService.getTagNamesByBoardNum(boardNum);
//	        System.out.println("태그 목록: " + tags); // 태그 리스트 출력

			boardData.put("tags", tags);

			// 댓글 수 가져오기
			int replyCount = replyMapper.getReplyCountByBoardNum(boardNum);
			// System.out.println("댓글 수: " + replyCount); // 댓글 수 출력

			boardData.put("replyCount", replyCount);

			// 주워진 계시물의 아이디 값을 기준으로 계시글의 좋아요 수를 카운트
			int likeCount = boardMapper.boardLikeCount(boardNum);

			// 좋아요 숫자를 추가
			boardData.put("likeCount", likeCount);

			List<String> boardImg = fileMapper.getBoardFiles(boardNum);
			boardData.put("boardImg", boardImg);

			// 데이터를 리스트에 추가
			boardnumSearch.add(boardData);

		}
		searchLogMapper.insertSearchLog(searchTerm, userId);
//	    System.out.println("최종 결과: " + boardnumSearch); // 최종 리스트 확인
		return boardnumSearch;
	}

	@Override
	public HashMap<String, Object> postComponent(int boardnum) {
		// TODO Auto-generated method stub
		return null;
	}

	// Duration을 기반으로 시간을 "몇분 전", "몇시간 전", "몇일 전", "몇주 전" 형태로 반환하는 메서드
	private String formatDuration(Duration duration) {
		if (duration.toDays() > 0) {
			// 일수 차이 계산
			long days = duration.toDays();
			if (days >= 7) {
				long weeks = days / 7;
				return weeks + "주 전";
			} else {
				return days + "일 전";
			}
		} else if (duration.toHours() > 0) {
			// 시간 차이 계산
			long hours = duration.toHours();
			return hours + "시간 전";
		} else if (duration.toMinutes() > 0) {
			// 분 차이 계산
			long minutes = duration.toMinutes();
			return minutes + "분 전";
		} else {
			// 초 차이 계산
			long seconds = duration.getSeconds();
			return seconds + "초 전";
		}
	}

	public List<Map<String, Object>> returnRanking() {
		String formatedNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		// 1시간 이내 해당하는 모든 boardnum 가져오기
		List<Integer> boardTimes = boardMapper.getBoardNumsByTime(formatedNow);
//	    System.out.println("검색된 boardTimes: " + boardTimes); 

		List<Map<String, Object>> boardnumTime = new ArrayList<>();

		for (Integer boardNum : boardTimes) {
			// boardNum 기반으로 데이터 가져오기
			Map<String, Object> boardData = new HashMap<>();
			BoardDTO board = boardMapper.getBoardByBoardNum(boardNum);

			LocalDateTime boardTime = board.getBoardTime();
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

			// 현재 시간 구하기
			LocalDateTime now = LocalDateTime.now();

			// boardTime과 현재 시간의 차이 계산
			Duration duration = Duration.between(boardTime, now);

			// 시간 차이를 표현할 문자열 변수
			String timeDifference = formatDuration(duration);

			// 시간 차이 추가
			boardData.put("timeDifference", timeDifference);

			boardData.put("board", board);

			// 작성자 정보 가져오기
			Map<String, Object> writerProfile = userService.getUserIdAndProfileById(board.getUserId());
			// System.out.println("작성자 정보: " + writerProfile); // 작성자 정보 출력

			boardData.put("writer", writerProfile);

			// 태그 정보 가져오기
			List<String> tags = tagService.getTagNamesByBoardNum(boardNum);
			// System.out.println("태그 목록: " + tags); // 태그 리스트 출력

			boardData.put("tags", tags);

			// 댓글 수 가져오기
			int replyCount = replyMapper.getReplyCountByBoardNum(boardNum);
			// System.out.println("댓글 수: " + replyCount); // 댓글 수 출력

			boardData.put("replyCount", replyCount);

			// 주워진 계시물의 아이디 값을 기준으로 계시글의 좋아요 수를 카운트
			int likeCount = boardMapper.boardLikeCount(boardNum);
			// 좋아요 숫자를 추가
			boardData.put("likeCount", likeCount);

			// 데이터를 리스트에 추가
			boardnumTime.add(boardData);

		}
//	    System.out.println("최종 결과: " + boardnumTime); // 최종 리스트 확인
		return boardnumTime;
	}

	// 공통 내부 메서드
	private Map<String, Object> getBoardDetails(BoardDTO board) {
		// 게시물에 대한 정보를 조회
		// 주워진 계시물의 아이디 값을 기준으로 태그를 조회
		List<String> tags = tagService.getTagNamesByBoardNum(board.getBoardNum());

		// 계시글을 작성한 유저의 아이디를 기준으로 작성자의 아이디와 프로필을 가져오기
		Map<String, Object> writerProfile = userService.getUserIdAndProfileById(board.getUserId());

//	    List<ReplyDTO> getReplyByBoardNum = replyMapper.getReplyByBoardNum(board.getBoardNum());

		// 주워진 계시물의 아이디 값을 기준으로 계시글의 댓글 수를 조회
		int replyCount = replyMapper.getReplyCountByBoardNum(board.getBoardNum());

		// 주워진 계시물의 아이디 값을 기준으로 계시글의 좋아요 수를 카운트
		// int likeCount = boardMapper.boardLikeCount(board.getBoardNum());

		// 주워진 계시물의 아이디 값을 기준으로 계시글의 파일들을 가져옴
		List<String> boardImg = fileMapper.getBoardFiles(board.getBoardNum());

		// 계시글에 대한 정보를 통합하여 반환하기 위한 Map 선언
		Map<String, Object> boardPack = new HashMap<>();
		// 보드 라는 이름으로 계시물의 값을 DTO로 묶어서 반환
//	        System.out.println(board);
		boardPack.put("board", board);
		// 태그들이라는 이름으로 계시물의 태그들을 DTO로 묶어서 반환
		boardPack.put("tags", tags);

		// writer 이라는 이름으로 계시글의 작성자를 Map으로 묶어서 반환
		boardPack.put("writer", writerProfile);

		// 댓글 숫자를 replyCount 라는 이름으로 반환
		boardPack.put("replyCount", replyCount);

		// 좋아요 숫자를 추가
		// boardPack.put("likeCount", likeCount);

		// 파일의 시스템 이름을 추가
		boardPack.put("boardImg", boardImg);

//	        boardPack.put("getReplyByBoardNum", getReplyByBoardNum);

		return boardPack;
	}

	@Override
	public List<Map<String, Object>> getPosts(int page, int size, String userId) {
		// 유저가 작성한 모든 boardnum 가져오기
		int offset = (page - 1) * size;

		List<Integer> boardNums = boardMapper.getBoardNumsByuserId(offset, size, userId);

		List<Map<String, Object>> posts = new ArrayList<>();

		for (Integer boardNum : boardNums) {
			// boardNum 기반으로 데이터 가져오기
			Map<String, Object> boardData = new HashMap<>();

			// boardNum 넘겨주기
			boardData.put("boardNum", boardNum);

			// 댓글 수 가져오기
			int replyCount = replyMapper.getReplyCountByBoardNum(boardNum);
			boardData.put("replyCount", replyCount);

			// 주워진 계시물의 아이디 값을 기준으로 계시글의 좋아요 수를 카운트
			int likeCount = boardMapper.boardLikeCount(boardNum);
			boardData.put("likeCount", likeCount);

			// 이미지 URL 리스트 가져오기
			List<String> boardImg = fileMapper.getBoardFiles(boardNum);
			// 첫 번째 이미지 URL만 추출 (비어있지 않으면)
			String firstImage = (boardImg != null && !boardImg.isEmpty()) ? boardImg.get(0) : null;
			boardData.put("imageUrl", firstImage); // 첫 번째 이미지 URL만 저장

			// 데이터를 리스트에 추가
			posts.add(boardData);
		}

//	    System.out.println("최종 결과: " + boardnumSearch); // 최종 리스트 확인
		return posts;
	}

	@Override
	public boolean deletePost(int boardNum, String userId) {
		List<Integer> notificaions = notificationMapper.checkByboardNum(boardNum);
		if (!notificaions.isEmpty()) {
			for (Integer notificaion : notificaions) {
				notificationMapper.deleteBynotificaionNum(notificaion);
			}
		}
		if (fileMapper.checkByboardNum(boardNum) > 0) {
			fileMapper.deleteFilesByBoardnum(boardNum);
		}
		if (checkMapper.checkByboardNum(boardNum) > 0) {
			checkMapper.deleteFilesByBoardnum(boardNum);
		}
		if (bookmarkMapper.checkByboardNum(boardNum) > 0) {
			bookmarkMapper.deleteFilesByBoardnum(boardNum);
		}

		// 태그 처리
		List<TagDTO> tags = tagService.getTagIdsByBoardNum(boardNum);
		System.out.println(tags);
		if (!tags.isEmpty()) { // 태그가 있는 경우에만 처리
			for (TagDTO tag : tags) {
				int tagId = tag.getTagId();

				// 태그가 다른 게시글에 연결되어 있는지 확인
				int boardCount = tagMapper.countBoardsByTagId(tagId);

				if (boardCount == 1) { // 게시글이 1개인 경우
					// 태그 팔로우 제거
					tagMapper.deleteTagFollowByTagId(tagId);

					// 태그 삭제
					tagMapper.deleteTagByTagId(tagId);
				}
			}

			// 태그 관계에서 boardNum이 없는 태그 관계 제거
			tagMapper.deleteNullTagRelations();
		}

		List<Integer> replies = replyMapper.checkByboardNum(boardNum);
		System.out.println(replies);
		if (replies.size()>0) {
			for (Integer reply : replies) {
//				if (likeMapper.checkByreplyNum(reply)) {
				likeMapper.deleteByreplyNum(reply);
//				}
				replyMapper.deleteByreplyNum(reply);
			}
		}

		List<Integer> likes = likeMapper.checkByboardNum(boardNum);
		if (!likes.isEmpty()) {
			for (Integer like : likes) {
				likeMapper.deleteBylikeNum(like);
			}
		}
		boardMapper.deleteBoard(userId, boardNum);

		return true;
	}

	@Override
	public List<Map<String, Object>> returnButtonState(String userId, int boardNum) {
		List<Map<String, Object>> buttonsState = new ArrayList<>();

		// 좋아요 상태 가져오기
		boolean likeState = likeMapper.getStateByboardNum(userId, boardNum);
		// 저장 상태 가져오기
		boolean bookmarkState = bookmarkMapper.getStateByboardNum(userId, boardNum);
		// 좋아요 개수 가져오기
		int likeNum = likeMapper.getlikeNumByboardNum(userId, boardNum);

		// 상태를 담을 Map 생성
		Map<String, Object> stateMap = new HashMap<>();
		stateMap.put("boardNum", boardNum); // boardNum 추가
		stateMap.put("userId", userId); // userId 추가
		stateMap.put("likeState", likeState);
		stateMap.put("bookmarkState", bookmarkState);
		stateMap.put("likeNum", likeNum);

//	    System.out.println("stateMap"+stateMap);
//	    buttonsState.add(boardNum)
		// Map을 List에 추가
		buttonsState.add(stateMap);
//	    System.out.println("buttonsState"+buttonsState);
		return buttonsState;
	}

	@Override
	public boolean toggleLikeState(int boardNum, String userId, boolean likeCheck) {
		if (likeCheck == false) {
			likeMapper.addlikeNum(boardNum, userId);
		}
		else if(likeCheck==true) {
 			int likeNum = likeMapper.getlikeNum(boardNum,userId);
			likeMapper.deletelikeNum(boardNum, userId);
			notifyMapper.deleteNotify(1,likeNum,userId);
		}
		return true;
	}

	@Override
	public boolean toggleSaveState(int boardNum, String userId, boolean saveCheck) {
		if (saveCheck == false) {
			bookmarkMapper.addbookmark(boardNum, userId);
		} else if (saveCheck == true) {
			bookmarkMapper.deletebookmark(boardNum, userId);
		}
		return true;
	}

	@Override
	public boolean deleteBookmarks(int boardNum, String userId) {
		bookmarkMapper.deletebookmark(boardNum, userId);
		return true;
	}

	@Override
	public List<Map<String, Object>> getBookmarks(int page, int size, String userId) {
		// 유저가 작성한 모든 boardnum 가져오기
		int offset = (page - 1) * size;

		List<Integer> boardNums = boardMapper.getBoardNumsBybookmarks(offset, size, userId);

		List<Map<String, Object>> bookmarks = new ArrayList<>();

		for (Integer boardNum : boardNums) {
			// boardNum 기반으로 데이터 가져오기
			Map<String, Object> boardData = new HashMap<>();

			// boardNum 넘겨주기
			boardData.put("boardNum", boardNum);

			// 댓글 수 가져오기
			int replyCount = replyMapper.getReplyCountByBoardNum(boardNum);
			boardData.put("replyCount", replyCount);

			// 주워진 계시물의 아이디 값을 기준으로 계시글의 좋아요 수를 카운트
			int likeCount = boardMapper.boardLikeCount(boardNum);
			boardData.put("likeCount", likeCount);

			// 이미지 URL 리스트 가져오기
			List<String> boardImg = fileMapper.getBoardFiles(boardNum);
			// 첫 번째 이미지 URL만 추출 (비어있지 않으면)
			String firstImage = (boardImg != null && !boardImg.isEmpty()) ? boardImg.get(0) : null;
			boardData.put("imageUrl", firstImage); // 첫 번째 이미지 URL만 저장

			// 데이터를 리스트에 추가
			bookmarks.add(boardData);
		}

		System.out.println("최종 결과: " + bookmarks); // 최종 리스트 확인
		return bookmarks;
	}
}