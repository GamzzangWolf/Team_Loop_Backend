package com.example.loop.domain.reply;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReplyDTO {
	private int replyNum;
	private String replyContents;
	private int boardNum;
	private String userId;
	private LocalDateTime replyTime; // LocalDate에서 LocalDateTime으로 변경
}
	