package com.musical.web.board;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BoardController {
	
	@Autowired
	SqlSession sqlSession;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping("/boardWrite")
	public String boardWrite(){
		return "board/boardWrite";
	}
	
	@RequestMapping("/boardWriteOk")
	public String boardWriteOk(BoardVO vo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		try {
			BoardDAOInterface dao = sqlSession.getMapper(BoardDAOInterface.class);
			
			int boardInx = dao.insertBoard(vo);
			logger.info("boardInx: "+boardInx);
			
			//업로드 위치
			String path = request.getSession().getServletContext().getRealPath("/upload");
			logger.info("path: "+path);
			
			MultipartFile multipartFile = null;
			MultipartHttpServletRequest mr = (MultipartHttpServletRequest)request;
			Iterator<String> ir = mr.getFileNames();
			
			//넘어온 파일을 리스트로 저장
	        //List<MultipartFile> mf = mr.getFiles("file");
	        
			while(ir.hasNext()){
				multipartFile = mr.getFile(ir.next());
				
	            //파일 중복명 처리
				if(!multipartFile.isEmpty()){
					String oriFileName = multipartFile.getOriginalFilename(); //본래 파일명
		        	logger.info("original file name: "+oriFileName);
	        	
	        		File f = new File(path, oriFileName);
	        		int cnt = 1; //파일명에 붙일 번호
	        		String newFileName = oriFileName;
	        		
	        		while(f.exists()){ //파일 존재하는 확인
	        			int dot = oriFileName.lastIndexOf("."); //.위치
	        			String fname = oriFileName.substring(0, dot);
	        			String ext = oriFileName.substring(dot+1);
	        			String fullName = fname + "(" + cnt++ + ")."+ext;
		 				
	        			File ff = new File(path, fullName);
	        			if(!ff.exists()){
		 					newFileName = fullName;
		 					break;
		 				}
		 			}
						
					//업로드
					try {
						multipartFile.transferTo(new File(path, newFileName));
						
						//DB 입력
						FileVO fv = new FileVO();
						fv.setBoard_idx(boardInx);
						fv.setOri_file_name(oriFileName);
						fv.setStored_file_name(newFileName);
						dao.fileUpload(fv);
						
					} catch (Exception e) {
						logger.error("Error at boardWriteOk upload", e);
					}
		 		}
	        }
			
			redirectAttributes.addFlashAttribute("ok", "write");
			return "redirect:/board";
		} catch (Exception e) {
			logger.error("Error at boardWriteOk", e);
			redirectAttributes.addFlashAttribute("msg", "게시판 입력 에러");
			return "redirect:/error";
		}
	}
	
	@RequestMapping("/board")
	public String board(HttpServletRequest request, Model model){
		try {
			BoardDAOInterface dao = sqlSession.getMapper(BoardDAOInterface.class);
			List<BoardVO> list = new ArrayList<BoardVO>();
			
			int cPage = 1;
			int count = 0;
			
			if(request.getParameter("currentPage") != null && !request.getParameter("currentPage").equals("")){
				cPage = Integer.parseInt(request.getParameter("currentPage"));
			}
			
			String searchOption = request.getParameter("searchOption");
			String keyword = request.getParameter("keyword");
			logger.info("searchOption: " + searchOption);
			logger.info("keyword: " + keyword);
			
			if(searchOption != null && !searchOption.equals("")){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("searchOption", searchOption);
				map.put("keyword", keyword);
				model.addAttribute("searchOption", searchOption);
				model.addAttribute("keyword", keyword);
				
				count = dao.recordSearchCount(map);
			}else{
				count = dao.recordCount();
			}
			
			BoardPagingVO pageVo = new BoardPagingVO();
			pageVo.makePaging(cPage, count);
			
			//마지막페이지 일때
			if(pageVo.getCurrentPage() == pageVo.getTotalPageCount()){
				//마지막페이지의 남은 레코드 수
				int modRecord = pageVo.getTotalRecord() % pageVo.getOnePageRecord();
				if(modRecord!=0){
					pageVo.setLastPageRecord(modRecord);
				}
			}
			
			if(searchOption != null && !searchOption.equals("")){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("pageVo", pageVo);
				map.put("searchOption", searchOption);
				map.put("keyword", keyword);
				list = dao.selectSearchList(map);			
			}else{
				list = dao.selectAllList(pageVo);
			}
			
			model.addAttribute("count", count);
			model.addAttribute("pageVo", pageVo);
			model.addAttribute("list", list);
			return "board/board";
			
		} catch (Exception e) {
			logger.error("Error at board", e);
			model.addAttribute("msg", "게시판 리스트 에러");
			return "error";
		}
	}
	
	@RequestMapping("/boardDetail")
	public String boardDetail(HttpServletRequest request, Model model){
		try {
			BoardDAOInterface dao = sqlSession.getMapper(BoardDAOInterface.class);
			
			int board_idx = Integer.parseInt(request.getParameter("num"));			
			dao.boardHitCount(board_idx);
			BoardVO vo = dao.selectBoard(board_idx);
			//업로드 위치
			String path = request.getSession().getServletContext().getRealPath("/upload");
			
			model.addAttribute("vo", vo);
			model.addAttribute("path", path);
			return "board/boardDetail";
			
		} catch (Exception e) {
			logger.error("Error at boardDetail", e);
			model.addAttribute("msg", "게시판 상세 에러");
			return "error";
		}
	}
	
	@RequestMapping("/sheetMusic")
	public String sheetMusic(Model model){
		try {
			String fileName = "compressed.tracemonkey-pldi-09.pdf";
			
			String url = "/web/js_css/pdfjs/web/" + fileName;
			
			model.addAttribute("url", url);
			return "board/sheetMusic";
			
		} catch (Exception e) {
			logger.error("Error at sheetMusic", e);
			model.addAttribute("msg", "pdf 화면 에러");
			return "error";
		}
		
	}
}
