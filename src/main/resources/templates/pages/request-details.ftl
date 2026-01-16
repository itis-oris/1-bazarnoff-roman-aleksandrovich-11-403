<#import "../layout/base.ftl" as layout>

<@layout.base title="${helpRequest.title}">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/requests">Форум вопросов</a> <span>→</span>
        ${helpRequest.title}
    </div>

    <!-- Вопрос -->
    <div class="forum-box">
        <div class="forum-header">
            <div style="display: flex; justify-content: space-between; align-items: start;">
                <div>
                    <div class="forum-title">${helpRequest.title}</div>
                    <div class="forum-meta">
                        Дисциплина: <a href="${contextPath}/subjects/${helpRequest.subjectID}">${helpRequest.subjectName}</a>
                        <#if helpRequest.difficultyLevel??> | Сложность: ${helpRequest.difficultyLevel?lower_case}</#if>
                    </div>
                </div>
                <div>
                    <#if helpRequest.status == "OPEN">
                        <span class="badge badge-open">Открыт</span>
                    <#elseif helpRequest.status == "IN_PROGRESS">
                        <span class="badge badge-progress">Есть ответы</span>
                    <#elseif helpRequest.status == "CLOSED">
                        <span class="badge badge-closed">Решён</span>
                    </#if>
                </div>
            </div>
        </div>

        <div class="post">
            <div class="post-author">
                <div class="post-author-name">
                    <a href="${contextPath}/users/${helpRequest.authorID}">${helpRequest.authorName}</a>
                </div>
                <div class="post-author-info">Автор вопроса</div>
            </div>
            <div class="post-content">
                <div class="post-date">
                    Опубликовано: ${helpRequest.createdAt}
                </div>
                <div class="post-text">
                    ${helpRequest.description?replace("\n", "<br>")?html}
                </div>
            </div>
        </div>
    </div>

    <!-- Ответы -->
    <h2>💬 Ответы (${responses?size})</h2>

    <#if responses?size == 0>
        <div class="alert alert-info">
            <strong>Пока нет ответов.</strong>
            <#if user?? && helpRequest.status == "OPEN" && user.id != helpRequest.authorID>
                Будьте первым, кто поможет!
            </#if>
        </div>
    <#else>
        <#list responses as response>
            <div class="post" <#if response.isAccepted>style="border-left: 4px solid #4a7c59;"</#if>>
                <div class="post-author">
                    <div class="post-author-name">
                        <a href="${contextPath}/users/${response.responderID}">${response.responderName}</a>
                    </div>
                    <#if response.isAccepted>
                        <div class="post-author-info" style="color: #4a7c59; font-weight: bold;">✓ Решение принято</div>
                    </#if>
                </div>
                <div class="post-content">
                    <div class="post-date">
                        ${response.createdAt}
                        <#if response.isAccepted>
                            <span style="color: #4a7c59; font-weight: bold; margin-left: 1rem;">✓ Лучший ответ</span>
                        </#if>
                    </div>
                    <div class="post-text">
                        ${response.message?replace("\n", "<br>")?html}
                    </div>

                    <#if user?? && !response.isAccepted && helpRequest.status != "CLOSED" && user.id == helpRequest.authorID>
                        <div class="post-actions">
                            <form method="POST" action="${contextPath}/requests/${helpRequest.id}/accept" style="display: inline;">
                                <input type="hidden" name="responseID" value="${response.id}">
                                <button type="submit" class="btn btn-success btn-sm">✓ Принять как решение</button>
                            </form>
                        </div>
                    </#if>
                </div>
            </div>
        </#list>
    </#if>

    <!-- Форма ответа -->
    <#if user??>
        <#if helpRequest.status != "CLOSED" && user.id != helpRequest.authorID>
            <div class="forum-box" style="margin-top: 2rem;">
                <div class="forum-header">
                    <div class="forum-title">Ваш ответ</div>
                </div>
                <div class="forum-body">
                    <form method="POST" action="${contextPath}/requests/${helpRequest.id}/respond">
                        <div class="form-group">
                            <label>Текст ответа *</label>
                            <textarea name="message" rows="8" required placeholder="Напишите подробный ответ на вопрос студента..."></textarea>
                            <small>Минимум 10 символов</small>
                        </div>
                        <button type="submit" class="btn btn-primary">Опубликовать ответ</button>
                    </form>
                </div>
            </div>
        <#elseif user.id == helpRequest.authorID && helpRequest.status != "CLOSED">
            <div class="alert alert-info">
                <strong>Информация:</strong> Вы не можете ответить на собственный вопрос. Дождитесь ответов от других студентов.
            </div>
        <#elseif helpRequest.status == "CLOSED">
            <div class="alert alert-success">
                <strong>Вопрос решён.</strong> Автор вопроса принял один из ответов как решение.
            </div>
        </#if>
    <#else>
        <div class="alert alert-warning">
            <a href="${contextPath}/login">Войдите в систему</a>, чтобы ответить на этот вопрос.
        </div>
    </#if>

</@layout.base>