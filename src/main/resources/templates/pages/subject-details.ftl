<#import "../layout/base.ftl" as layout>

<@layout.base title="${subject.name}">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/subjects">Дисциплины</a> <span>→</span>
        ${subject.name}
    </div>

    <div class="forum-box">
        <div class="forum-header">
            <div class="forum-title">📘 ${subject.name}</div>
            <div class="forum-meta">
                <#if subject.faculty??>Факультет: ${subject.faculty}</#if>
                <#if subject.semester??> | Семестр: ${subject.semester}</#if>
            </div>
        </div>

        <div class="forum-body">
            <#if subject.description??>
                <h3>Описание дисциплины</h3>
                <p style="line-height: 1.8;">${subject.description}</p>
            </#if>

            <table style="margin-top: 1.5rem; border: none;">
                <tr>
                    <td style="font-weight: bold; width: 200px; border: none;">Дата добавления:</td>
                    <td style="border: none;">${subject.createdAt}</td>
                </tr>
            </table>
        </div>
    </div>

    <h2>📝 Вопросы по этой дисциплине</h2>
    <div class="alert alert-info">
        <strong>Информация:</strong> Здесь будут отображаться вопросы студентов по этой дисциплине
    </div>

    <h2>📚 Учебные материалы</h2>
    <div class="alert alert-info">
        <strong>Информация:</strong> Здесь будут отображаться учебные материалы по этой дисциплине
    </div>

    <div style="margin-top: 2rem;">
        <a href="${contextPath}/subjects" class="btn btn-secondary">← Вернуться к списку дисциплин</a>
    </div>

</@layout.base>