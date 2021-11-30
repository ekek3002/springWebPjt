package org.zerock.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
@Log4j
public class BoardController {
	
	private final BoardService service;
	
	@GetMapping("/list")
	public void list(Criteria cri, Model model) {
		
		log.info("-----------------------------------");
		log.info(cri);
		log.info("list...........");
		
		model.addAttribute("list", service.getList(cri));
		model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
		
	}
//	@GetMapping("/list")
//	public void list(Model model) {
//		
//		log.info("list...........");
//		
//		model.addAttribute("list", service.getList());
//		
//	}
	
	@GetMapping("/register")
	@PreAuthorize("isAuthenticated()")
	public void registerGet() {
		
	}
	
//	@PostMapping("/register")
//	public String register(BoardVO board, RedirectAttributes rttr) {
//		log.info("board: "+board);
//		
//		long bno = service.register(board);
//		
//		log.info("BNO: "+bno);
//		rttr.addFlashAttribute("result", bno);
//		
//		return "redirect:/board/list";
//	}
//	@PostMapping("/register")
//	public String register(BoardVO board, RedirectAttributes rttr) {
//		log.info("=====================================");
//		log.info("register: "+board);
//		
//		if (board.getAttachList() != null) {
//			board.getAttachList().forEach(attach -> log.info(attach));
//		}
//		
//		log.info("=====================================");
//		service.register(board);
//		rttr.addFlashAttribute("result", board.getBno());
//		
//		return "redirect:/board/list";
//	}
	@PostMapping("/register")
	@PreAuthorize("isAuthenticated()")
	public String register(BoardVO board, RedirectAttributes rttr) {
		log.info("=====================================");
		log.info("register: "+board);
		
		if (board.getAttachList() != null) {
			board.getAttachList().forEach(attach -> log.info(attach));
		}
		
		log.info("=====================================");
		service.register(board);
		rttr.addFlashAttribute("result", board.getBno());
		
		return "redirect:/board/list";
	}
	
	@GetMapping({"/get", "/modify"})
	public void get(@RequestParam("bno")Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		
		model.addAttribute("board", service.get(bno));
	}
	
	@PreAuthorize("principal.username == #board.writer")
	@PostMapping("/modify")
	public String modify(BoardVO board, Criteria cri, RedirectAttributes rttr) {
//		int count = service.modify(board);
		log.info("modify:" + board);
		if (service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		}
		
//		rttr.addAttribute("pageNum", cri.getPageNum());
//		rttr.addAttribute("amount", cri.getAmount());
//		rttr.addAttribute("type", cri.getType());
//		rttr.addAttribute("keyword", cri.getKeyword());
		
		return "redirect:/board/list" + cri.getListLink();
	}
	
//	@PostMapping("/remove")
//	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr) {
//		int count = service.remove(bno);
//		
//		if (count == 1) {
//			rttr.addFlashAttribute("result", "success");
//		}
////		rttr.addAttribute("pageNum", cri.getPageNum());
////		rttr.addAttribute("amount", cri.getAmount());
////		rttr.addAttribute("type", cri.getType());
////		rttr.addAttribute("keyword", cri.getKeyword());
//		
//		return "redirect:/board/list" + cri.getListLink();
//	}
	
	private void deleteFiles(List<BoardAttachVO> attachList) {
		if (attachList == null || attachList.size() == 0) {
			return;
		}
		
		log.info("delete attach files...........");
		log.info(attachList);
		
		attachList.forEach(attach -> {
			try {
				Path file = Paths.get("/Users/yellowin/workspace/study/spring/upload/"+attach.getUploadPath()+"/"+attach.getUuid()+"_"+attach.getFileName());
				Files.deleteIfExists(file);
				
				if (Files.probeContentType(file).startsWith("image")) {
					Path thumbNail = Paths.get("/Users/yellowin/workspace/study/spring/upload/"+attach.getUploadPath()+"/s_"+attach.getUuid()+"_"+attach.getFileName());
					
					Files.delete(thumbNail);
				}
			} catch (Exception e) {
				log.error("delete file error "+e.getMessage());
			}
		});
	}
	
//	@PostMapping("/remove")
//	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr) {
//		log.info("remove......"+bno);
//		List<BoardAttachVO> attachList = service.getAttachList(bno);
//		if (service.remove(bno)) {
//			deleteFiles(attachList);
//			rttr.addFlashAttribute("result", "success");
//		}
//		
//		return "redirect:/board/list" + cri.getListLink();
//	}
	@PreAuthorize("principal.username == #writer")
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr) {
		log.info("remove......"+bno);
		List<BoardAttachVO> attachList = service.getAttachList(bno);
		if (service.remove(bno)) {
			deleteFiles(attachList);
			rttr.addFlashAttribute("result", "success");
		}
		
		return "redirect:/board/list" + cri.getListLink();
	}
	
	@GetMapping(value = "/getAttachList", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno){
		log.info("getAttachList "+bno);
		return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);
	}
	
}
