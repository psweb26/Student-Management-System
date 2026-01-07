package com.prs.studentmanagement.controller;

import com.prs.studentmanagement.dto.StudentSummary;
import com.prs.studentmanagement.service.ParentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
public class ParentController {

    private static final Logger log = LoggerFactory.getLogger(ParentController.class);

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    /**
     * SIMPLIFIED VERSION - Removes complex auth checks for development.
     * Returns all children linked to the given parentId.
     *
     * For production, add proper authentication/authorization middleware.
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<StudentSummary>> getChildren(@PathVariable String parentId) {
        log.debug("ParentController.getChildren called for parentId={}", parentId);

        try {
            List<StudentSummary> children = parentService.getChildrenForParent(parentId);
            log.debug("Returning {} children for parentId={}", children.size(), parentId);
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            log.error("Error fetching children for parentId={}: {}", parentId, e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }
}