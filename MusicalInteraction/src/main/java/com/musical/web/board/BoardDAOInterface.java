package com.musical.web.board;

import java.util.List;
import java.util.Map;

public interface BoardDAOInterface {
	//게시판 글 업로드()
	public int insertBoard(BoardVO vo);
	//파일 업로드
	public void fileUpload(FileVO fv);
	//Board 검색 총 레코드 수
	public int recordSearchCount(Map<String, Object> map);
	//Board 총 레코드 수
	public int recordCount();
	//Board 검색 리스트
	public List<BoardVO> selectSearchList(Map<String, Object> map);
	//Board 전체 리스트
	public List<BoardVO> selectAllList(BoardPagingVO pageVo);
	//조회수
	public void boardHitCount(int board_idx);
	//게시물 선택
	public BoardVO selectBoard(int board_idx);
	
	
	
}
