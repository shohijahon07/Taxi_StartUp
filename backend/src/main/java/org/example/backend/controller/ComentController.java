package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.repository.CommentRepo;
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

    @GetMapping
    public ResponseEntity<?> checkUserRoles(@RequestParam UUID id){
        List<Comment> comments = commentRepo.findAllByUserId(id);
        return ResponseEntity.ok(comments);
    }
}
