package com.musical.web.board;

import java.util.List;
import java.util.Map;

public interface BoardDAOInterface {
	//�Խ��� �� ���ε�()
	public int insertBoard(BoardVO vo);
	//���� ���ε�
	public void fileUpload(FileVO fv);
	//Board �˻� �� ���ڵ� ��
	public int recordSearchCount(Map<String, Object> map);
	//Board �� ���ڵ� ��
	public int recordCount();
	//Board �˻� ����Ʈ
	public List<BoardVO> selectSearchList(Map<String, Object> map);
	//Board ��ü ����Ʈ
	public List<BoardVO> selectAllList(BoardPagingVO pageVo);
	//��ȸ��
	public void boardHitCount(int board_idx);
	//�Խù� ����
	public BoardVO selectBoard(int board_idx);
	
	
	
}
