package com.example.loop.domain.board;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardDTO {
	private int boardNum;
	private String userId;
	private String boardContents;
	private String boardLocation;
	private String storeName;
	private LocalDateTime boardTime;
}