<#import "../layout/base.ftl" as layout>

<@layout.base title="Задать вопрос">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/requests">Форум вопросов</a> <span>→</span>
        Задать вопрос
    </div>

    <div style="max-width: 900px; margin: 0 auto;">
        <div class="form-box">
            <h1>Задать вопрос</h1>
            <p class="text-muted">Опишите вашу проблему максимально подробно, чтобы другие студенты смогли вам помочь</p>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/requests/create">
                <div class="form-group">
                    <label>Дисциплина *</label>
                    <select name="subjectId" required>
                        <option value="">Выберите дисциплину</option>
                        <#list subjects as subject>
                            <option value="${subject.id}" <#if subjectId?? && subjectId == subject.id?string>selected</#if>>
                                ${subject.name}
                            </option>
                        </#list>
                    </select>
                    <small>По какой дисциплине возник вопрос</small>
                </div>

                <div class="form-group">
                    <label>Заголовок вопроса *</label>
                    <input type="text" name="title" value="${title!''}" required placeholder="Краткая формулировка вопроса">
                    <small>Минимум 5 символов</small>
                </div>

                <div class="form-group">
                    <label>Подробное описание проблемы *</label>
                    <textarea name="description" rows="10" required placeholder="Опишите вашу проблему максимально подробно. Укажите, что вы уже пробовали, какие трудности возникли...">${description!''}</textarea>
                    <small>Минимум 20 символов. Чем подробнее описание, тем быстрее получите качественный ответ.</small>
                </div>

                <div class="alert alert-info" style="margin-top: 1.5rem;">
                    <strong>💡 Совет:</strong> Качественное описание проблемы поможет другим студентам быстрее понять вашу ситуацию и дать полезный ответ.
                </div>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">Опубликовать вопрос</button>
                    <a href="${contextPath}/requests" class="btn btn-secondary" style="flex: 1; text-align: center;">Отмена</a>
                </div>
            </form>
        </div>
    </div>

</@layout.base>