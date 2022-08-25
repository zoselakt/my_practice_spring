package com.medici.arang.board.notice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.medici.arang.board.notice.command.NoticeCommand;
import com.medici.arang.board.notice.service.NoticeServiceImpl;

@Controller("noticeController")
public class NoticeController {
	private NoticeServiceImpl noticeServiceImpl;
	
	@Autowired
	public NoticeController(NoticeServiceImpl noticeServiceImpl) {
		this.noticeServiceImpl = noticeServiceImpl;
	}
	
	@GetMapping("notice/notice")
	public String noticeAllFindGet(NoticeCommand command, Model model, 
			HttpServletRequest request, HttpSession session, 
			@RequestParam(required = false, defaultValue = "") String field,
			@RequestParam(required = false, defaultValue = "") String word) {
		List<NoticeCommand> noticeFindAll = noticeServiceImpl.findAllNotice();
		model.addAttribute("noticeFindAll", noticeFindAll);
		
		// 페이징
		int page = 0;
		if(request.getParameter("page") != null) {
			page = Integer.parseInt(request.getParameter("page"));			
		}
		Pageable pageable = PageRequest.of(page, 5, Sort.Direction.DESC, "regDate");
		Page<NoticeCommand> noticeList = noticeServiceImpl.findAll(pageable);
		
		//검색기능
		if(field.equals("title")) {
			noticeList = noticeServiceImpl.findAllbyTitle(pageable, word);
		}else if(field.equals("writer")){
			noticeList = noticeServiceImpl.findAllbyWriter(pageable, word);
		}
		
		//현재페이지
		int pageNumber = noticeList.getPageable().getPageNumber();
		//총 페이지수
		int totalPages = noticeList.getTotalPages();
		//블럭의 수
		int pageBlock = 5;
		//현재 페이지가 7이라면 1*5+1=6
		int startBlockPage = ((pageNumber)/pageBlock)*pageBlock+1;
		//6+5-1=10. 6,7,8,9,10해서 10.
		int endBlockPage = startBlockPage+pageBlock-1;
		endBlockPage= totalPages<endBlockPage? totalPages:endBlockPage;
		
		model.addAttribute("startBlockPage", startBlockPage);
		model.addAttribute("endBlockPage", endBlockPage);
		model.addAttribute("noticeList", noticeList);
		
		return "notice/notice";
	}
	
	@GetMapping("/getSearchList")
	@ResponseBody
	public List<NoticeCommand> getSearchList (NoticeCommand command, Model model,
			HttpServletResponse response, String type, String keyword) {
		String commandType = command.getType();
		String commandKeyword = command.getKeyword(); 
//		String title = command.getTitle();
//		List<NoticeCommand> searchTiele = noticeServiceImpl.searchByTitle(title);
//		
//		String content = command.getContent();
//		List<NoticeCommand> searchContent = noticeServiceImpl.searchByContent(content);
//		
//		String writer = command.getWriter();
//		List<NoticeCommand> searchWriter = noticeServiceImpl.searchByWriter(writer);
//		
//		model.addAttribute("searchTiele", searchTiele);
//		model.addAttribute("searchContent", searchContent);
//		model.addAttribute("searchWriter", searchWriter);
		
		List<NoticeCommand> search = noticeServiceImpl.selectSearchList(commandType, commandKeyword);
		model.addAttribute("search", search);
		
		return search;
	}
	
	@GetMapping("notice/findOneNoticeForm")
	public String noticeOneFind(NoticeCommand noticeCommand, Model model, @RequestParam long num, HttpServletRequest request) {
		List<NoticeCommand> noticeFindOne = noticeServiceImpl.findOneNotice(num);
		
		request.setAttribute("noticeCommand", noticeCommand);
		model.addAttribute("noticeFindOne", noticeFindOne);
		return "notice/findOneNoticeForm";
	}
	@PostMapping("notice/findOneNoticeForm")
	public String noticeOnePost(NoticeCommand command, Model model, @RequestParam long num) {
		List<NoticeCommand> noticeFindOne = noticeServiceImpl.findOneNotice(num);
		model.addAttribute("noticeFindOne", noticeFindOne);
		return "notice/findOneNoticeForm";
	}	
	
}
