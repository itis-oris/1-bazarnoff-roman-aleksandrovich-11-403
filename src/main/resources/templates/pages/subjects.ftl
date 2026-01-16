<#import "../layout/base.ftl" as layout>

<@layout.base title="Академические дисциплины">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span> Дисциплины
    </div>

    <div class="forum-box">
        <div class="forum-header">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <div class="forum-title">📚 Академические дисциплины</div>
                    <div class="forum-meta">Список всех учебных предметов на платформе</div>
                </div>
                <#if user?? && user.role?? && user.role == "MODERATOR">
                    <a href="${contextPath}/subjects/add" class="btn btn-success">Добавить дисциплину</a>
                </#if>
            </div>
        </div>
    </div>

    <#if user?? && (!user.role?? || user.role != "MODERATOR")>
        <div class="alert alert-info">
            <strong>💡 Не нашли нужную дисциплину?</strong><br>
            Обратитесь к модератору через <a href="${contextPath}/requests/create">форум вопросов</a>,
            указав название дисциплины в теме вопроса. Модератор добавит дисциплину в систему.
        </div>
    </#if>

    <!-- Поиск и фильтры -->
    <div class="search-panel">
        <h3>Поиск дисциплин</h3>
        <form method="GET" action="${contextPath}/subjects">
            <div style="display: grid; grid-template-columns: 2fr 1fr 1fr auto; gap: 1rem; align-items: end;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Название дисциплины</label>
                    <input type="text" name="search" value="${searchQuery!''}" placeholder="Поиск...">
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label>Факультет</label>
                    <input type="text" name="faculty" value="${facultyFilter!''}" placeholder="ФКН">
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label>Семестр</label>
                    <input type="number" name="semester" value="${semesterFilter!''}" min="1" max="12">
                </div>

                <button type="submit" class="btn btn-primary">Найти</button>
            </div>

            <#if searchQuery?? || facultyFilter?? || semesterFilter??>
                <div style="margin-top: 1rem;">
                    <a href="${contextPath}/subjects">Сбросить фильтры</a>
                </div>
            </#if>
        </form>
    </div>

    <!-- Список дисциплин -->
    <#if subjects?size == 0>
        <div class="alert alert-warning">
            <strong>Дисциплины не найдены.</strong>
            <#if user?? && user.role?? && user.role.name()?upper_case == "MODERATOR">
                <br>
                <a href="${contextPath}/subjects/add" class="btn btn-success" style="margin-top: 10px;">
                    ➕ Добавить дисциплину
                </a>
            <#else>
                Попробуйте изменить параметры поиска.
            </#if>
        </div>
    <#else>
        <table>
            <thead>
            <tr>
                <th style="width: 50%;">Название дисциплины</th>
                <th style="width: 150px;">Факультет</th>
                <th style="width: 100px;">Семестр</th>
                <th style="width: 150px;">Добавлена</th>
            </tr>
            </thead>
            <tbody>
            <#list subjects as subject>
                <tr>
                    <td>
                        <a href="${contextPath}/subjects/${subject.id}" style="font-weight: bold; font-size: 1.05rem;">
                            ${subject.name}
                        </a>
                        <#if subject.description?? && subject.description?length gt 0>
                            <br>
                            <span class="text-muted text-small">
                                <#if subject.description?length lte 120>
                                    ${subject.description}
                                <#else>
                                    ${subject.description?substring(0, 120)}...
                                </#if>
                            </span>
                        </#if>
                    </td>
                    <td>
                        <#if subject.faculty?? && subject.faculty?length gt 0>
                            <span class="badge badge-difficulty">${subject.faculty}</span>
                        <#else>
                            <span class="text-muted">—</span>
                        </#if>
                    </td>
                    <td style="text-align: center;">
                        <#if subject.semester??>
                            <strong>${subject.semester}</strong>
                        <#else>
                            <span class="text-muted">—</span>
                        </#if>
                    </td>
                    <td class="text-small text-muted">
                        ${subject.createdAt}
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>

        <div class="forum-footer" style="justify-content: space-between; align-items: center;">
            <span class="text-muted">Всего дисциплин: <strong>${subjects?size}</strong></span>
            <#if user?? && user.role?? && user.role == "MODERATOR">
                <a href="${contextPath}/subjects/add" class="btn btn-success">➕ Добавить дисциплину</a>
            </#if>
        </div>
    </#if>

</@layout.base>
