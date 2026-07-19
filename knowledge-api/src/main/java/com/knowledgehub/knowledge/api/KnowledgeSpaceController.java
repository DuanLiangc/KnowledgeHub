package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.application.CreateKnowledgeSpaceCommand;
import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import com.knowledgehub.knowledge.application.UpdateKnowledgeSpaceCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 提供知识空间增删改查接口。
 *
 * <p>Controller 只负责 HTTP 参数校验和对象转换，业务规则交给应用层处理。</p>
 */
@RestController
@RequestMapping("/api/knowledge-spaces")
public class KnowledgeSpaceController {

    private final KnowledgeSpaceService service;

    /**
     * 创建知识空间控制器。
     *
     * @param service 知识空间业务服务
     */
    public KnowledgeSpaceController(KnowledgeSpaceService service) {
        this.service = service;
    }

    /**
     * 创建知识空间。
     *
     * @param request 创建参数
     * @return 创建后的知识空间
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeSpaceResponse create(@Valid @RequestBody CreateKnowledgeSpaceRequest request) {
        var command = new CreateKnowledgeSpaceCommand(request.name(), request.description());
        return KnowledgeSpaceResponse.from(service.create(command));
    }

    /**
     * 查询全部知识空间。
     *
     * @return 按创建时间升序排列的知识空间
     */
    @GetMapping
    public List<KnowledgeSpaceResponse> findAll() {
        return service.findAll().stream()
                .map(KnowledgeSpaceResponse::from)
                .toList();
    }

    /**
     * 按 ID 查询知识空间。
     *
     * @param id 知识空间 ID
     * @return 匹配的知识空间
     */
    @GetMapping("/{id}")
    public KnowledgeSpaceResponse findById(@PathVariable UUID id) {
        return KnowledgeSpaceResponse.from(service.findById(id));
    }

    /**
     * 修改指定知识空间。
     *
     * @param id 知识空间 ID
     * @param request 修改参数
     * @return 修改后的知识空间
     */
    @PutMapping("/{id}")
    public KnowledgeSpaceResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateKnowledgeSpaceRequest request
    ) {
        var command = new UpdateKnowledgeSpaceCommand(request.name(), request.description());
        return KnowledgeSpaceResponse.from(service.update(id, command));
    }

    /**
     * 删除指定知识空间。
     *
     * @param id 知识空间 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
