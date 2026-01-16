<#import "../layout/base.ftl" as layout>

<@layout.base title="Добавить дисциплину">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/subjects">Дисциплины</a> <span>→</span>
        Добавить дисциплину
    </div>

    <div style="max-width: 800px; margin: 0 auto;">
        <div class="form-box">
            <h1>Добавить новую дисциплину</h1>
            <p class="text-muted">Заполните информацию об учебной дисциплине</p>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/subjects/add">
                <div class="form-group">
                    <label>Название дисциплины *</label>
                    <input type="text" name="name" value="${name!''}" required>
                    <small>Полное название учебной дисциплины</small>
                </div>

                <div class="form-group">
                    <label>Описание</label>
                    <textarea name="description" rows="5">${description!''}</textarea>
                    <small>Краткое описание содержания и целей дисциплины</small>
                </div>

                <div class="form-group">
                    <label>Факультет</label>
                    <input type="text" name="faculty" value="${faculty!''}">
                    <small>Например: ФКН, Физический факультет</small>
                </div>

                <div class="form-group">
                    <label>Семестр изучения</label>
                    <input type="number" name="semester" value="${semester!''}" min="1" max="12">
                    <small>Номер семестра (1-12)</small>
                </div>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">Добавить дисциплину</button>
                    <a href="${contextPath}/subjects" class="btn btn-secondary" style="flex: 1; text-align: center;">Отмена</a>
                </div>
            </form>
        </div>
    </div>

</@layout.base>