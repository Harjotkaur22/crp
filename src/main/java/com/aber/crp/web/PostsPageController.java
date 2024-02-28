package com.aber.crp.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aber.crp.dto.CommentsDto;
import com.aber.crp.dto.PostDto;
import com.aber.crp.dto.UserRegistrationDto;
import com.aber.crp.model.Comments;
import com.aber.crp.service.PostService;

@Controller
@RequestMapping("/user/posts")
public class PostsPageController {
	
	@Autowired
	PostService postService;

	@GetMapping
	public String loadPostsListingPage(Model model) {
		List<PostDto> postDtoList = postService.findAllPost();
		model.addAttribute("postList", postDtoList);
		return "user/posts";
	}
	
	
	
	
	@ModelAttribute("post")
	public PostDto postDto() {
		return new PostDto();
	}
	
	@PostMapping("/createPost")
	public String createNewPost(@ModelAttribute("post") PostDto dto) {
		
		postService.savePost(dto);		
		return "redirect:/user/posts";
	}
	
	
	@GetMapping("/view")
	public String viewPost(@RequestParam("id") Long id, Model model) {
		
		PostDto postDto = postService.findPostById(id);
		String[] codeSampleByLines = postDto.getCodeSample().split(System.lineSeparator());
		int count = 1;
		StringBuffer code = new StringBuffer();
		for(String temp : codeSampleByLines) {
			code.append(count + " : " + temp +System.lineSeparator());
			count++;
		}
		postDto.setCodeSampleWithIndex(code.toString());
		CommentsDto commentDto = new CommentsDto();
		commentDto.setPostId(id);
		Set<CommentsDto> commentsList = postService.findAllCommentsByPostId(id);
        model.addAttribute("post", postDto);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        model.addAttribute("tempDate", LocalDateTime.now());

        model.addAttribute("codeSampleByLines", codeSampleByLines);
        model.addAttribute("comment", commentDto);
        model.addAttribute("comments", commentsList);

		return "user/viewPost";
	}
	
	@PostMapping("/addComment")
	public String addComment(@ModelAttribute("comment") CommentsDto dto) {
		
		postService.saveComment(dto);
		
		return "redirect:/user/posts/view?id="+dto.getPostId();
	}
	
}