<#import "../layout/base.ftl" as layout>

<@layout.base title="Мои предметы">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/profile">Профиль</a> <span>→</span>
        Мои предметы
    </div>


    <#if successMessage??>
        <#if successMessage == "added">
            <div class="alert alert-success">
                ✓ Предмет успешно добавлен!
            </div>
        <#elseif successMessage == "removed">
            <div class="alert alert-success">
                ✓ Предмет успешно удален!
            </div>
        </#if>
    </#if>

<#-- Сообщения об ошибке -->
    <#if errorMessage??>
        <div class="alert alert-danger">
            <strong>Ошибка!</strong>
            <#if errorMessage == "invalid_id">
                Неверный идентификатор предмета.
            <#elseif errorMessage == "unexpected_error">
                Произошла непредвиденная ошибка. Попробуйте позже.
            <#else>
                ${errorMessage}
            </#if>
        </div>
    </#if>

    <div class="forum-box">
        <div class="forum-header">
            <div class="forum-title">📚 Управление предметами</div>
            <div class="forum-meta">Добавьте предметы, которые изучаете, и укажите свой уровень владения</div>
        </div>
    </div>


<#-- Форма добавления нового предмета -->
    <#if availableSubjects?size gt 0>
        <div class="search-panel">
            <h3>➕ Добавить предмет</h3>
            <form method="POST" action="${contextPath}/profile/subjects/add">
                <div style="display: grid; grid-template-columns: 2fr 1fr auto auto; gap: 1rem; align-items: end;">
                    <div class="form-group" style="margin-bottom: 0;">
                        <label>Предмет *</label>
                        <select name="subjectId" required>
                            <option value="">Выберите предмет</option>
                            <#list availableSubjects as subject>
                                <option value="${subject.id}">
                                    ${subject.name}
                                    <#if subject.faculty??>- ${subject.faculty}</#if>
                                </option>
                            </#list>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 0;">
                        <label>Уровень владения *</label>
                        <select name="proficiencyLevel" id="proficiencyLevel" required>
                            <option value="LEARNING">📘 Изучаю</option>
                            <option value="KNOWS">📗 Знаю</option>
                            <option value="EXPERT">📕 Эксперт</option>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 0;">
                        <label style="display: flex; align-items: center; cursor: pointer;">
                            <input type="checkbox" name="canHelp" id="canHelp" style="margin-right: 0.5rem;">
                            Могу помогать
                        </label>
                    </div>

                    <button type="submit" class="btn btn-success">Добавить</button>
                </div>
            </form>
        </div>

        <script>
            // Автоматически снимаем галочку "Могу помогать" для уровня "Изучаю"
            document.getElementById('proficiencyLevel').addEventListener('change', function () {
                const canHelpCheckbox = document.getElementById('canHelp');
                if (this.value === 'LEARNING') {
                    canHelpCheckbox.checked = false;
                    canHelpCheckbox.disabled = true;
                } else {
                    canHelpCheckbox.disabled = false;
                }
            });
        </script>
    <#else>
        <div class="alert alert-info">
            <strong>Все предметы добавлены!</strong> Вы уже добавили все доступные предметы.
        </div>
    </#if>

<#-- Список текущих предметов -->
    <h3 style="margin-top: 2rem;">Мои предметы (${userSubjects?size})</h3>

    <#if userSubjects?size == 0>
        <div class="alert alert-warning">
            <strong>У вас пока нет предметов.</strong> Добавьте первый предмет выше!
        </div>
    <#else>
        <table>
            <thead>
            <tr>
                <th style="width: 55%;">Предмет</th>
                <th style="width: 20%;">Уровень</th>
                <th style="width: 15%;">Могу помогать</th>
                <th style="width: 10%;">Действия</th>
            </tr>
            </thead>
            <tbody>
            <#list userSubjects as item>
                <tr>
                    <td>
                        <strong style="font-size: 1.05rem;">
                            <a href="${contextPath}/subjects/${item.subject.id}">${item.subject.name}</a>
                        </strong>
                        <#if item.subject.faculty??>
                            <br>
                            <span class="text-muted text-small">Факультет: ${item.subject.faculty}</span>
                        </#if>
                        <#if item.subject.semester??>
                            <br>
                            <span class="text-muted text-small">Семестр: ${item.subject.semester}</span>
                        </#if>
                    </td>
                    <td>
                        <#if item.userSubject.proficiencyLevel == "LEARNING">
                            <span class="badge" style="background: #5b9bd5; color: white;">📘 Изучаю</span>
                        <#elseif item.userSubject.proficiencyLevel == "KNOWS">
                            <span class="badge" style="background: #70ad47; color: white;">📗 Знаю</span>
                        <#elseif item.userSubject.proficiencyLevel == "EXPERT">
                            <span class="badge" style="background: #c9302c; color: white;">📕 Эксперт</span>
                        </#if>
                    </td>
                    <td style="text-align: center;">
                        <#if item.userSubject.canHelp>
                            <span style="color: #70ad47; font-size: 1.5rem;">✓</span>
                        <#else>
                            <span style="color: #ccc; font-size: 1.5rem;">✗</span>
                        </#if>
                    </td>
                    <td>
                        <form method="POST" action="${contextPath}/profile/subjects/remove"
                              onsubmit="return confirm('Удалить предмет из списка?');"
                              style="display: inline;">
                            <input type="hidden" name="subjectId" value="${item.subject.id}">
                            <button type="submit" class="btn btn-danger btn-sm">Удалить</button>
                        </form>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>


        <div class="forum-footer" style="justify-content: space-between;">
            <span class="text-muted">
                Всего предметов: <strong>${userSubjects?size}</strong>
            </span>
            <span class="text-muted">
                Могу помогать: <strong>${userSubjects?filter(s -> s.userSubject.canHelp)?size}</strong>
            </span>
        </div>
    </#if>

    <div style="margin-top: 2rem; text-align: center;">
        <a href="${contextPath}/profile" class="btn btn-secondary">← Вернуться в профиль</a>
    </div>

</@layout.base>