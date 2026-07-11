package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.application.CreateKnowledgeSpaceCommand;
import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-spaces")
public class KnowledgeSpaceController {

    private final KnowledgeSpaceService service;

    public KnowledgeSpaceController(KnowledgeSpaceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeSpaceResponse create(@Valid @RequestBody CreateKnowledgeSpaceRequest request) {
        var command = new CreateKnowledgeSpaceCommand(request.name(), request.description());
        return KnowledgeSpaceResponse.from(service.create(command));
    }

    @GetMapping
    public List<KnowledgeSpaceResponse> findAll() {
        return service.findAll().stream()
                .map(KnowledgeSpaceResponse::from)
                .toList();
    }
}

