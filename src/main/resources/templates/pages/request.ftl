<#import "../layout/base.ftl" as layout>

<@layout.base title="Форум вопросов">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span> Форум вопросов
    </div>

    <div class="forum-box">
        <div class="forum-header">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <div class="forum-title">❓ Форум вопросов и ответов</div>
                    <div class="forum-meta">Здесь студенты задают вопросы и помогают друг другу</div>
                </div>
                <#if user??>
                    <a href="${contextPath}/requests/create" class="btn btn-success">Задать вопрос</a>
                </#if>
            </div>
        </div>
    </div>

    <!-- Фильтры -->
    <div class="search-panel">
        <h3>Поиск и фильтрация</h3>
        <form id = "filter-form" method="GET" action="${contextPath}/requests">
            <div style="display: grid; grid-template-columns: 2fr 1fr 1fr auto; gap: 1rem; align-items: end;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Поиск по тексту</label>
                    <input type="text" name="search" value="${searchQuery!''}" placeholder="Ключевые слова...">
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label>Статус</label>
                    <select name="status">
                        <option value="">Все статусы</option>
                        <option value="OPEN" <#if statusFilter?? && statusFilter == "OPEN">selected</#if>>Открыт</option>
                        <option value="IN_PROGRESS" <#if statusFilter?? && statusFilter == "IN_PROGRESS">selected</#if>>Есть ответы</option>
                        <option value="CLOSED" <#if statusFilter?? && statusFilter == "CLOSED">selected</#if>>Закрыт</option>
                    </select>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label>Дисциплина</label>
                    <select name="subject">
                        <option value="">Все дисциплины</option>
                        <#list subjects as subject>
                            <option value="${subject.id}" <#if subjectFilter?? && subjectFilter == subject.id?string>selected</#if>>
                                ${subject.name}
                            </option>
                        </#list>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary">Найти</button>
            </div>

            <#if searchQuery?? || statusFilter?? || subjectFilter??>
                <div style="margin-top: 1rem;">
                    <a href="${contextPath}/requests">Сбросить фильтры</a>
                </div>
            </#if>
        </form>
    </div>

    <div id = "request-list">
    <#if requests?size == 0>
        <div class="alert alert-warning">
            <strong>Вопросы не найдены.</strong>
            <#if user??>
                <a href="${contextPath}/requests/create">Задайте первый вопрос</a>!
            </#if>
        </div>
    <#else>
        <#list requests as request>
            <div class="forum-box">
                <div class="forum-body" style="padding: 1.5rem;">
                    <div style="display: flex; justify-content: space-between; align-items: start;">
                        <div style="flex: 1;">
                            <h3 style="margin: 0;">
                                <a href="${contextPath}/requests/${request.id}" style="color: #1e3a5f;">
                                    ${request.title}
                                </a>
                            </h3>
                            <div class="text-muted text-small" style="margin-top: 0.5rem;">
                                Автор: <strong>${request.authorName}</strong> |
                                Дисциплина: <a href="${contextPath}/subjects/${request.subjectID}">${request.subjectName}</a> |
                                ${request.createdAt}
                            </div>
                        </div>
                        <div style="margin-left: 1rem;">
                            <#if request.status == "OPEN">
                                <span class="badge badge-open">Открыт</span>
                            <#elseif request.status == "IN_PROGRESS">
                                <span class="badge badge-progress">Есть ответы</span>
                            <#elseif request.status == "CLOSED">
                                <span class="badge badge-closed">Закрыт</span>
                            </#if>

                            <#if request.difficultyLevel??>
                                <span class="badge badge-difficulty">${request.difficultyLevel?lower_case}</span>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>
        </#list>

        <div class="forum-footer" style="justify-content: center;">
            <span class="text-muted">Всего вопросов: <strong>${requests?size}</strong></span>
        </div>
    </#if>
    </div>
</@layout.base>