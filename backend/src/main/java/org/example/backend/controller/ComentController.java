package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.Comment1;
import org.example.backend.repository.CommentRepo;
import org.example.backend.repository.CommentRepo1;
import org.example.backend.repository.RoleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class ComentController {
    final CommentRepo commentRepo;
    final CommentRepo1 commentRepo1;

    @GetMapping
    public ResponseEntity<?> checkUserRoles(@RequestParam Integer language,@RequestParam UUID id) {
        if (language==1){
            List<Comment> comments = commentRepo.findAllByUserId(id);
            return ResponseEntity.ok(comments);
        }else if (language==2){
            List<Comment1> comments = commentRepo1.findAllByUserId(id);
            return ResponseEntity.ok(comments);
        }
       return null;
    }
}
