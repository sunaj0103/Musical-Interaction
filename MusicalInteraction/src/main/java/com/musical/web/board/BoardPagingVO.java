package com.musical.web.board;

public class BoardPagingVO {
	private int currentPage = 1; //현재 페이지
	private int totalRecord; //총 레코드수
	private int totalPageCount; //총 페이지수
	private int startPageNum; //시작 페이지 번호
	private int onePageRecord = 10; //한 페이지당 레코드 수
	private int onePageCount = 10; //한 페이지당 표시할 페이지 수
	private int rownumEnd; //페이지 마지막 레코드
	private int lastPageRecord = 10; //마지막 페이지 레코드 수

	public void makePaging(int currentPage, int totalRecord) {
		this.lastPageRecord = onePageRecord;
		this.currentPage = currentPage;
		this.totalRecord = totalRecord;
		setTotalPageCount();
		setStartPageNum();
		setRownumEnd();
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getTotalPageCount() {
		return totalPageCount;
	}
	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}
	
	//총페이지수 구하기
	public void setTotalPageCount(){ 
		this.totalPageCount = (int)Math.ceil(totalRecord/(double)onePageRecord);
	}
	
	public int getStartPageNum() {
		return startPageNum;
	}
	public void setStartPageNum(int startPageNum) {
		this.startPageNum = startPageNum;
	}
	
	//시작페이지
	public void setStartPageNum(){
		if(currentPage%onePageCount == 0){
			startPageNum = currentPage - onePageCount + 1;
		}else{			            
			startPageNum = currentPage/onePageCount*onePageCount + 1;
		}
	}
	
	public int getOnePageRecord() {
		return onePageRecord;
	}
	public void setOnePageRecord(int onePageRecord) {
		this.onePageRecord = onePageRecord;
	}
	public int getOnePageCount() {
		return onePageCount;
	}
	public void setOnePageCount(int onePageCount) {
		this.onePageCount = onePageCount;
	}
	public int getRownumEnd() {
		return rownumEnd;
	}
	public void setRownumEnd(int rownumEnd) {
		this.rownumEnd = rownumEnd;
	}
	
	//페이지 마지막 레코드
	public void setRownumEnd() {
		rownumEnd = currentPage * onePageRecord;
	}
	
	public int getLastPageRecord() {
		return lastPageRecord;
	}
	public void setLastPageRecord(int lastPageRecord) {
		this.lastPageRecord = lastPageRecord;
	}
}
