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
	public String boardWriteOk(BoardVO vo, HttpServletRequest request, Model model
			/*, RedirectAttributes redirectAttributes, HttpSession session*/) {
		try {
			BoardDAOInterface dao = sqlSession.getMapper(BoardDAOInterface.class);
			
			dao.insertBoard(vo);
			int boardInx = vo.getBoard_idx();
			logger.info("boardInx: "+boardInx);
			
			//업로드 위치
			String path = request.getSession().getServletContext().getRealPath("/upload");
			//logger.info("path: "+path);
			
			// 디레토리가 없다면 생성 
			File dir = new File(path); 
			if (!dir.isDirectory()) { 
				dir.mkdirs(); 
			}
			
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
	        		
	        		while(f.exists()){ //파일 존재하는지 확인
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
						fv.setFile_size(multipartFile.getSize());
						dao.fileUpload(fv);
						
					} catch (Exception e) {
						logger.error("Error at boardWriteOk upload", e);
					}
		 		}
	        }
			
			//redirectAttributes.addFlashAttribute("ok", "write");
			//session.setAttribute("stat", "write"); 
			//return "redirect:/board";
			model.addAttribute("stat", "write");
			return "board/boardWrite";
		} catch (Exception e) {
			logger.error("Error at boardWriteOk", e);
			model.addAttribute("msg", "게시판 입력 에러");
			return "error";
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
			List<FileVO> fVo = dao.selectFileList(board_idx);
			
			//업로드 위치
			String path = request.getSession().getServletContext().getRealPath("/upload");
			
			model.addAttribute("vo", vo);
			model.addAttribute("fVo", fVo);
			model.addAttribute("path", path);
			return "board/boardDetail";
			
		} catch (Exception e) {
			logger.error("Error at boardDetail", e);
			model.addAttribute("msg", "게시판 상세 에러");
			return "error";
		}
	}
	
	/*@RequestMapping("/downloadFile")
	public void downloadFile(@RequestParam("oriFile") String oriFile, @RequestParam("fileName") String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String path = request.getSession().getServletContext().getRealPath("/upload");
		byte fileByte[] = FileUtils.readFileToByteArray(new File(path+"/"+fileName));
	     
	    response.setContentType("application/octet-stream");
	    response.setContentLength(fileByte.length);
	    response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(oriFile,"UTF-8")+"\";");
	    response.setHeader("Content-Transfer-Encoding", "binary");
	    response.getOutputStream().write(fileByte);
	     
	    response.getOutputStream().flush();
	    response.getOutputStream().close();
	}*/
	
	@RequestMapping("/downloadFile")
	public ModelAndView download(@RequestParam("oriFile") String oriFile, @RequestParam("fileName") String fileName, HttpServletRequest request) {
		logger.debug("oriFile: "+oriFile);
		
		ModelAndView mav = new ModelAndView("downloadView");
		String path = request.getSession().getServletContext().getRealPath("/upload");
        
        mav.addObject("path", path);
        mav.addObject("fileName", fileName);
        mav.addObject("oriFile", oriFile);
                 
        return mav;

	}
	
	/*@RequestMapping("/downloadFile")
    public void downloadFile(@RequestParam("oriFile") String oriFile, @RequestParam("fileName") String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception{
        
        //===전달 받은 정보를 가지고 파일객체 생성(S)===//
        
        oriFile = new String(oriFile.getBytes("ISO8859_1"),"UTF-8"); 
        //서버설정(server.xml)에 따로 인코딩을 지정하지 않았기 때문에 get방식으로 받은 값에 대해 인코딩 변환
        
        logger.info(fileName);
        logger.info(oriFile);
        
        //웹사이트 루트디렉토리의 실제 디스크상의 경로 알아내기.
        String path = request.getServletContext().getRealPath("upload");
        //String path = request.getSession().getServletContext().getRealPath("upload");
        //String path = WebUtils.getRealPath(request.getServletContext(), "upload");        
        //String path = "D:\\upload\\";        
        
        String fullPath = path+"/"+fileName;
        
        logger.info("path :"+path);        
        logger.info("fullPath:" + fullPath);
        File downloadFile = new File(fullPath);
        
        //===전달 받은 정보를 가지고 파일객체 생성(E)===//
        
        
        //파일 다운로드를 위해 컨테츠 타입을 application/download 설정
        //response.setContentType("application/download; charset=utf-8");
                
        //파일 사이즈 지정
        response.setContentLength((int)downloadFile.length());
        
        //다운로드 창을 띄우기 위한 헤더 조작
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="
                                        + new String(oriFile.getBytes(), "ISO8859_1"));
        
        response.setHeader("Content-Transfer-Encoding","binary");
        
         * Content-disposition 속성
         * 1) "Content-disposition: attachment" 브라우저 인식 파일확장자를 포함하여 모든 확장자의 파일들에 대해
         *                          , 다운로드시 무조건 "파일 다운로드" 대화상자가 뜨도록 하는 헤더속성이다
         * 2) "Content-disposition: inline" 브라우저 인식 파일확장자를 가진 파일들에 대해서는 
         *                                  웹브라우저 상에서 바로 파일을 열고, 그외의 파일들에 대해서는 
         *                                  "파일 다운로드" 대화상자가 뜨도록 하는 헤더속성이다.
         
        
        FileInputStream fin = new FileInputStream(downloadFile);
        ServletOutputStream sout = response.getOutputStream();

        byte[] buf = new byte[1024];
        int size = -1;

        while ((size = fin.read(buf, 0, buf.length)) != -1) {
            sout.write(buf, 0, size);
        }
        fin.close();
        sout.close();
        
    }*/

	@RequestMapping("/sheetMusic")
	public String sheetMusic(HttpServletRequest request, Model model){
		try {
			String storedFileName = request.getParameter("stored_file_name");
			String path = request.getSession().getServletContext().getRealPath("/upload");
			
			model.addAttribute("fileName", storedFileName);
			return "board/sheetMusic";
			
		} catch (Exception e) {
			logger.error("Error at sheetMusic", e);
			model.addAttribute("msg", "pdf 화면 에러");
			return "error";
		}
		
	}
}
