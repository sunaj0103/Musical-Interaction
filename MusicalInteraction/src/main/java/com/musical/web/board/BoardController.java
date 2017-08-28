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
			
			//���ε� ��ġ
			String path = request.getSession().getServletContext().getRealPath("/upload");
			//logger.info("path: "+path);
			
			// ���丮�� ���ٸ� ���� 
			File dir = new File(path); 
			if (!dir.isDirectory()) { 
				dir.mkdirs(); 
			}
			
			MultipartFile multipartFile = null;
			MultipartHttpServletRequest mr = (MultipartHttpServletRequest)request;
			Iterator<String> ir = mr.getFileNames();
			
			//�Ѿ�� ������ ����Ʈ�� ����
	        //List<MultipartFile> mf = mr.getFiles("file");
	        
			while(ir.hasNext()){
				multipartFile = mr.getFile(ir.next());
				
	            //���� �ߺ��� ó��
				if(!multipartFile.isEmpty()){
					String oriFileName = multipartFile.getOriginalFilename(); //���� ���ϸ�
		        	logger.info("original file name: "+oriFileName);
	        	
	        		File f = new File(path, oriFileName);
	        		int cnt = 1; //���ϸ� ���� ��ȣ
	        		String newFileName = oriFileName;
	        		
	        		while(f.exists()){ //���� �����ϴ��� Ȯ��
	        			int dot = oriFileName.lastIndexOf("."); //.��ġ
	        			String fname = oriFileName.substring(0, dot);
	        			String ext = oriFileName.substring(dot+1);
	        			String fullName = fname + "(" + cnt++ + ")."+ext;
		 				
	        			File ff = new File(path, fullName);
	        			if(!ff.exists()){
		 					newFileName = fullName;
		 					break;
		 				}
		 			}
						
					//���ε�
					try {
						multipartFile.transferTo(new File(path, newFileName));
						
						//DB �Է�
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
			model.addAttribute("msg", "�Խ��� �Է� ����");
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
			
			//������������ �϶�
			if(pageVo.getCurrentPage() == pageVo.getTotalPageCount()){
				//�������������� ���� ���ڵ� ��
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
			model.addAttribute("msg", "�Խ��� ����Ʈ ����");
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
			
			//���ε� ��ġ
			String path = request.getSession().getServletContext().getRealPath("/upload");
			
			model.addAttribute("vo", vo);
			model.addAttribute("fVo", fVo);
			model.addAttribute("path", path);
			return "board/boardDetail";
			
		} catch (Exception e) {
			logger.error("Error at boardDetail", e);
			model.addAttribute("msg", "�Խ��� �� ����");
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
        
        //===���� ���� ������ ������ ���ϰ�ü ����(S)===//
        
        oriFile = new String(oriFile.getBytes("ISO8859_1"),"UTF-8"); 
        //��������(server.xml)�� ���� ���ڵ��� �������� �ʾұ� ������ get������� ���� ���� ���� ���ڵ� ��ȯ
        
        logger.info(fileName);
        logger.info(oriFile);
        
        //������Ʈ ��Ʈ���丮�� ���� ��ũ���� ��� �˾Ƴ���.
        String path = request.getServletContext().getRealPath("upload");
        //String path = request.getSession().getServletContext().getRealPath("upload");
        //String path = WebUtils.getRealPath(request.getServletContext(), "upload");        
        //String path = "D:\\upload\\";        
        
        String fullPath = path+"/"+fileName;
        
        logger.info("path :"+path);        
        logger.info("fullPath:" + fullPath);
        File downloadFile = new File(fullPath);
        
        //===���� ���� ������ ������ ���ϰ�ü ����(E)===//
        
        
        //���� �ٿ�ε带 ���� ������ Ÿ���� application/download ����
        //response.setContentType("application/download; charset=utf-8");
                
        //���� ������ ����
        response.setContentLength((int)downloadFile.length());
        
        //�ٿ�ε� â�� ���� ���� ��� ����
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="
                                        + new String(oriFile.getBytes(), "ISO8859_1"));
        
        response.setHeader("Content-Transfer-Encoding","binary");
        
         * Content-disposition �Ӽ�
         * 1) "Content-disposition: attachment" ������ �ν� ����Ȯ���ڸ� �����Ͽ� ��� Ȯ������ ���ϵ鿡 ����
         *                          , �ٿ�ε�� ������ "���� �ٿ�ε�" ��ȭ���ڰ� �ߵ��� �ϴ� ����Ӽ��̴�
         * 2) "Content-disposition: inline" ������ �ν� ����Ȯ���ڸ� ���� ���ϵ鿡 ���ؼ��� 
         *                                  �������� �󿡼� �ٷ� ������ ����, �׿��� ���ϵ鿡 ���ؼ��� 
         *                                  "���� �ٿ�ε�" ��ȭ���ڰ� �ߵ��� �ϴ� ����Ӽ��̴�.
         
        
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
			model.addAttribute("msg", "pdf ȭ�� ����");
			return "error";
		}
		
	}
}
