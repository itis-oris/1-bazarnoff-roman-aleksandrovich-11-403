<#import "../layout/base.ftl" as layout>

<@layout.base title="Библиотека материалов">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span> Библиотека материалов
    </div>

    <div class="forum-box">
        <div class="forum-header">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <div class="forum-title">📚 Библиотека учебных материалов</div>
                    <div class="forum-meta">Конспекты, презентации и другие материалы от студентов</div>
                </div>
                <#if user??>
                    <a href="${contextPath}/materials/upload" class="btn btn-success">Загрузить материал</a>
                </#if>
            </div>
        </div>
    </div>

    <!-- Поиск и фильтры -->
    <div class="search-panel">
        <h3>Поиск материалов</h3>
        <form method="GET" action="${contextPath}/materials">
            <div style="display: grid; grid-template-columns: 2fr 1fr auto; gap: 1rem; align-items: end;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Название материала</label>
                    <input type="text" name="search" value="${searchQuery!''}" placeholder="Поиск...">
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

            <#if searchQuery?? || subjectFilter??>
                <div style="margin-top: 1rem;">
                    <a href="${contextPath}/materials">Сбросить фильтры</a>
                </div>
            </#if>
        </form>
    </div>

    <!-- Список материалов -->
    <#if materials?size == 0>
        <div class="alert alert-warning">
            <strong>Материалы не найдены.</strong>
            <#if user??>
                <a href="${contextPath}/materials/upload">Загрузите первый материал</a>!
            </#if>
        </div>
    <#else>
        <table>
            <thead>
            <tr>
                <th style="width: 50%;">Название материала</th>
                <th style="width: 120px;">Тип</th>
                <th style="width: 100px;">Скачиваний</th>
                <th style="width: 150px;">Загружен</th>
                <th style="width: 120px;">Действия</th>
            </tr>
            </thead>
            <tbody>
            <#list materials as material>
                <tr>
                    <td>
                        <strong style="font-size: 1.05rem;">${material.title}</strong>
                        <br>
                        <span class="text-muted text-small">
                                Автор: ${material.authorName} |
                                Дисциплина: <a href="${contextPath}/subjects/${material.subjectID}">${material.subjectName}</a>
                            </span>
                        <#if material.description?? && material.description?length gt 0>
                            <br>
                            <span class="text-muted text-small">${material.description}</span>
                        </#if>
                    </td>
                    <td>
                        <#if material.materialType == "NOTES">
                            <span class="badge" style="background: #4a7c59; color: white;">📝 Конспект</span>
                        <#elseif material.materialType == "SUMMARY">
                            <span class="badge" style="background: #2c5f8d; color: white;">📄 Вывод</span>
                        <#elseif material.materialType == "PRESENTATION">
                            <span class="badge" style="background: #c9a961; color: white;">📊 Презентация</span>
                        <#elseif material.materialType == "VIDEO_LINK">
                            <span class="badge" style="background: #b94a48; color: white;">🎥 Видео</span>
                        </#if>
                    </td>
                    <td style="text-align: center">
                        <#if material.materialType == "VIDEO_LINK">
                            <span class="text-muted">—</span>
                        <#else>
                            <strong>${material.downloadsCount}</strong>
                        </#if>
                    </td>
                    <td class="text-small text-muted">
                        ${material.uploadDate}
                    </td>
                    <td>
                        <#if material.materialType == "VIDEO_LINK">
                            <a href="${material.filePath}" target="_blank" class="btn btn-secondary btn-sm">Открыть</a>
                        <#else>
                            <a href="${contextPath}/materials//download/${material.id}" class="btn btn-primary btn-sm">Скачать</a>
                        </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>

        <div class="forum-footer" style="justify-content: center;">
            <span class="text-muted">Всего материалов: <strong>${materials?size}</strong></span>
        </div>
    </#if>

</@layout.base>